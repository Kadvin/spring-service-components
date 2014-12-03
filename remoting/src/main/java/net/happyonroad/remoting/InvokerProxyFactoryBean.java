/**
 * @author XiongJie, Date: 13-11-1
 */
package net.happyonroad.remoting;

import net.happyonroad.cache.CacheService;
import net.happyonroad.cache.ListChannel;
import net.happyonroad.cache.MutableCacheService;
import org.apache.commons.proxy.Invoker;
import org.apache.commons.proxy.ProxyFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.UUID;

/** 在使用端的配置的代理bean，代表了远程服务 */
public class InvokerProxyFactoryBean
        implements FactoryBean<Object>, InitializingBean, Invoker {

    private Class serviceInterface;
    private Object serviceProxy;
    private CacheService cacheService;
    /**
     * 设置一个队列名
     */
    private String queueName;
    /**
     * 消息的timeout时间(单位 seconds)
     * TODO 这个timeout时间应该每次调用都可以设置的
     */
    private int receiveTimeout = 120;

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

    /**
     * 设置接收消息的timeout时间(单位 second)
     */
    @SuppressWarnings("UnusedDeclaration")
    public void setReceiveTimeout(int receiveTimeout) {
        this.receiveTimeout = receiveTimeout;
    }

    public Object invoke(Object proxy, Method method, Object[] arguments) throws Throwable {
        if("equals".equals(method.getName())) {
            return false;
        }

        InvocationRequestMessage request = new InvocationRequestMessage();
        String replyTo = "Replies/" + this.queueName + "/" + UUID.randomUUID();
        request.setReplyTo(replyTo);
        request.setMethodName(method.getName());
        request.fillParameterClasses(method.getParameterTypes());
        request.setArguments(arguments);

        ListChannel requestChannel = getChannel(this.queueName);
        //采用二进制进行对象序列化与反序列化，暂时不采用json机制
        byte[] rawRequest = dump(request);
        requestChannel.pushLeft(rawRequest);
        ListChannel responseChannel = getChannel(request.getReplyTo());
        //所以，这个方法
        byte[] rawResponse = responseChannel.blockPopRight(this.receiveTimeout);
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
