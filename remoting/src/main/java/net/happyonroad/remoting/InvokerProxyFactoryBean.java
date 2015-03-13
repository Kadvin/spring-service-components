/**
 * @author XiongJie, Date: 13-11-1
 */
package net.happyonroad.remoting;

import net.happyonroad.annotation.Timeout;
import net.happyonroad.cache.CacheService;
import net.happyonroad.cache.ListChannel;
import net.happyonroad.cache.MutableCacheService;
import net.happyonroad.type.TimeInterval;
import org.apache.commons.proxy.Invoker;
import org.apache.commons.proxy.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.UUID;

/** 在使用端的配置的代理bean，代表了远程服务 */
public class InvokerProxyFactoryBean
        implements FactoryBean<Object>, InitializingBean, Invoker {
    static String DEFAULT_TIMEOUT = "2m"; //默认一次调用，2分钟以内应该返回

    private Class serviceInterface;
    private Object serviceProxy;
    private CacheService cacheService;
    /**
     * 设置一个队列名
     */
    private String queueName;

    //默认binary
    private InvocationMessageConverter converter;

    public InvokerProxyFactoryBean(CacheService cacheService) throws UnsupportedEncodingException {

        if (cacheService == null) {
            throw new IllegalArgumentException("'cacheService' is required");
        }

        this.cacheService = cacheService;
        setConverterType("binary");
    }

    /**
     * 设置代理的服务接口.
     *
     * @param serviceInterface 服务接口
     * @throws IllegalArgumentException 不支持null
     */
    public void setServiceInterface(Class serviceInterface) {
        if (serviceInterface == null || !serviceInterface.isInterface()) {
            throw new IllegalArgumentException("'serviceInterface' must be an interface");
        }
        this.serviceInterface = serviceInterface;
        if(!StringUtils.hasText(this.queueName)){
            this.queueName = this.serviceInterface.getName();
        }
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public void setConverterType(String type){
        if("binary".equalsIgnoreCase(type)){
            this.converter = new BinaryInvocationMessageConverter();
        }else if("string".equalsIgnoreCase(type)){
            this.converter = new JsonStringInvocationMessageConverter();
        }else{
            try{
                this.converter = (InvocationMessageConverter) Class.forName(type).newInstance();
            }catch (Exception ex){
                throw new IllegalArgumentException("Unknown or error converter type: " + type, ex);
            }
        }
    }

    public void afterPropertiesSet() {
        if (this.serviceInterface == null) {
            throw new IllegalArgumentException("Property 'serviceInterface' is required");
        }
        this.serviceProxy = new ProxyFactory().createInvokerProxy(this, new Class[]{this.serviceInterface});
    }


    public Object getObject() {
        return this.serviceProxy;
    }

    public Class<?> getObjectType() {
        return this.serviceInterface;
    }

    public boolean isSingleton() {
        return true;
    }

    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
        String methodName = method.getName();
        if("equals".equals(methodName)) {
            return false;
        } else if ("toString".equals(methodName)) {
            return "Proxy<" + getObjectType().getSimpleName() + ">";
        } else if ("hashCode".equals(methodName)) {
            return Integer.MIN_VALUE;
        } else if ("notify".equals(methodName) || "notifyAll".equals(methodName)) {
            return Void.TYPE;
        } else if ("getClass".equals(methodName)) {
            return getObjectType();
        }

        // 获取超时时间
        Timeout timeoutConfig = AnnotationUtils.findAnnotation(method, Timeout.class);
        if( timeoutConfig == null ) {
            timeoutConfig = AnnotationUtils.findAnnotation(this.serviceInterface, Timeout.class);
        }
        String timeout;
        if( timeoutConfig == null ){
            timeout = DEFAULT_TIMEOUT;
        }else {
            timeout = timeoutConfig.value();
        }

        InvocationRequestMessage request = new InvocationRequestMessage();
        String replyTo = "Replies/" + this.queueName + "/" + UUID.randomUUID();
        request.setServiceName(getObjectType().getName());
        request.setMethodName(method.getName());
        request.populateArguments(method.getParameterTypes(), arguments);

        ListChannel requestChannel = getChannel(this.queueName);
        //采用二进制进行对象序列化与反序列化，暂时不采用json机制
        byte[] rawRequest = dump(request);
        requestChannel.pushLeft(rawRequest);
        ListChannel responseChannel = getChannel(request.getServiceName());
        //所以，这个方法
        byte[] rawResponse = responseChannel.blockPopRight((int)new TimeInterval(timeout).getMilliseconds());
        //没有用TimeoutException来返回，而是用null，这样可以减少栈成本
        if (null == rawResponse)
            throw new RemoteAccessException("Invoke  `" + getObjectType().getSimpleName() + "#" + method.getName() +
                                            "` timeout. " +
                                            "call queue:  `" + this.queueName + "`, " +
                                            "reply queue: `" + replyTo + "`");

        InvocationResponseMessage response = load(rawResponse);
        //noinspection ThrowableResultOfMethodCallIgnored
        if(response.getError() != null )
            throw response.recreateError();
        return response.getValue();
    }

    private byte[] dump(InvocationRequestMessage request) throws IOException {
        return converter.dump(request);
    }

    private InvocationResponseMessage load(byte[] message) throws IOException {
        return (InvocationResponseMessage) converter.parse(message);
    }

    protected ListChannel getChannel(String channelName){
        //与 InvokerServiceExport保持一致
        if(cacheService instanceof MutableCacheService){
            return new RemoteListChannel((MutableCacheService) cacheService, channelName);
        }else{
            return cacheService.getListContainer(channelName);
        }
    }


}
