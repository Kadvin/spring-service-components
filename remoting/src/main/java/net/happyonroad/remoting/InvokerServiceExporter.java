/**
 * @author XiongJie, Date: 13-11-1
 */
package net.happyonroad.remoting;

import net.happyonroad.cache.CacheService;
import net.happyonroad.cache.ListChannel;
import net.happyonroad.cache.MutableCacheService;
import net.happyonroad.cache.remote.RemoteCacheConnectedEvent;
import net.happyonroad.cache.remote.RemoteCacheEvent;
import net.happyonroad.concurrent.OnceTrigger;
import net.happyonroad.concurrent.ZombieKiller;
import net.happyonroad.type.TimeInterval;
import net.happyonroad.util.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.scheduling.TaskScheduler;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/** 在提供端的服务暴露器，将本地的实际服务通过消息服务暴露出去 */
//现在每个服务都会对接到远端一个Cache List Channel上
public class InvokerServiceExporter extends RemoteInvocationBasedExporter
        implements DisposableBean, Runnable, ApplicationListener<RemoteCacheEvent> {
    private static Logger logger = LoggerFactory.getLogger(InvokerServiceExporter.class);

    static NamedThreadFactory factory = new NamedThreadFactory("InvokeDispatcher", new ServiceExitHandler(), "32K");

    private CacheService cacheService;
    private String       queueName;
    private ListChannel  channel;

    // 用于接收请求消息，并将其转发给执行线程
    // 其执行体为本对象
    private Thread daemon;

    // 用于执行特定请求
    // 其执行体为基于收到消息的实例对象
    private ExecutorService taskExecutor;

    //用于清理长时间block的派发任务
    protected TaskScheduler cleanScheduler;

    private Object  proxy;
    private boolean isRunning;
    @Value("${engine.identifier}")
    private String  engineId;
    //对Proxy调用的超时
    private TimeInterval timeout = new TimeInterval("2m");

    //默认binary
    private InvocationMessageConverter converter;


    public InvokerServiceExporter() {
        setConverterType("binary");
    }

    @Override
    public void setServiceInterface(Class serviceInterface) {
        super.setServiceInterface(serviceInterface);
    }

    public void setTaskExecutor(ExecutorService taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    public void setTimeout(String timeout) {
        this.timeout = new TimeInterval(timeout);
    }

    public void setCleanScheduler(TaskScheduler cleanScheduler) {
        this.cleanScheduler = cleanScheduler;
    }

    public void setConverterType(String type) {
        if ("binary".equalsIgnoreCase(type)) {
            this.converter = new BinaryInvocationMessageConverter();
        } else if ("string".equalsIgnoreCase(type)) {
            this.converter = new JsonStringInvocationMessageConverter();
        } else {
            try {
                this.converter = (InvocationMessageConverter) Class.forName(type).newInstance();
            } catch (Exception ex) {
                throw new IllegalArgumentException("Unknown or error converter type: " + type, ex);
            }
        }
    }

    @Override
    public void onApplicationEvent(RemoteCacheEvent event) {
        //Queue的名称必须与其他引擎区分开来
        this.queueName = this.engineId + "/" + getServiceInterface().getName();
        if (event instanceof RemoteCacheConnectedEvent) {
            cacheService = (CacheService) event.getSource();
            exportService();
        } else {
            closeService();
        }
    }

    public void exportService() {
        this.channel = getChannel(queueName);
        this.proxy = getProxyForService();
        isRunning = true;
        daemon = factory.newThread(this);
        daemon.setDaemon(true);
        daemon.setName(daemon.getName() + "-" + getServiceInterface().getSimpleName());
        daemon.start();
        logger.info("Export " + getServiceInterface().getName()+ " on " + queueName);
    }

    private void closeService(){
        try {
            destroy();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    ListChannel getChannel(String channelName){
        // 对于DefaultCache实现而言
        // 不能将ListContainer从服务器上直接拿过来
        // 但对于RedisCache，就可以
        if(cacheService instanceof MutableCacheService){
            return new RemoteListChannel((MutableCacheService) cacheService, channelName);
        }else{
            return cacheService.getListContainer(channelName);
        }
    }

    @Override
    public void run() {
        while (isRunning)  {
            // timeout 则返回null，说明没消息
            byte[] message = channel.blockPopRight(1);//unit seconds
            if (null == message)
                continue;
            try{
                InvokeJob job = new InvokeJob(this, message);
                Future<?> future = taskExecutor.submit(job);
                ZombieKiller killer = new ZombieKiller(future, "Invoke timeout on " + getServiceInterface().getSimpleName());
                OnceTrigger trigger = new OnceTrigger(System.currentTimeMillis() + timeout.getMilliseconds());
                cleanScheduler.schedule(killer, trigger);
            }catch (Exception e) {
                logger.error("Failed to process received message\n" +
                            "Current class loader is:" + Thread.currentThread().getContextClassLoader(), e);

            }
        }
    }

    //////////////////////////////////////////////////////
    // For InvokeJob
    //////////////////////////////////////////////////////

    InvocationRequestMessage load(byte[] msg) throws IOException {
        return (InvocationRequestMessage) converter.parse(msg);
    }

    byte[] dump(InvocationResponseMessage response) throws IOException {
        return converter.dump(response);
    }

    ClassLoader getClassLoader(){
        return getBeanClassLoader();
    }


    RemoteInvocationResult invokeIt(RemoteInvocation remoteInvocation) {
        return invokeAndCreateResult(remoteInvocation, proxy);
    }

    @Override
    public void destroy() throws Exception {
        isRunning = false;
        if (daemon != null) {
            try {
                daemon.interrupt();
            } catch (Exception ex) {
                //skip it
            }
        }
    }

    //for test assertion
    public Object getProxy() {
        return proxy;
    }

    private static class ServiceExitHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            logger.error(t.getName() + " exit on: " + e.getMessage());
        }
    }
}
