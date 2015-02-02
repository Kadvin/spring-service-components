/**
 * @author XiongJie, Date: 13-10-25
 */
package net.happyonroad.messaging.support;

import net.happyonroad.concurrent.OnceTrigger;
import net.happyonroad.concurrent.ZombieKiller;
import net.happyonroad.messaging.MessageBus;
import net.happyonroad.messaging.MessageListener;
import net.happyonroad.util.PatternUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.TaskScheduler;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

/**
 * <h1>缺省的消息总线实现</h1>
 * <p/>
 * 基于内存实现，应用于单元测试，演示，快速交付等场景
 * <p/>
 * 没有任何多线程控制，空值检测等保障手段
 */

@ManagedResource(objectName = "net.happyonroad:name=messageBus")
public class DefaultMessageBus implements MessageBus {
    private static Logger                       logger        = LoggerFactory.getLogger(DefaultMessageBus.class);
    private Map<String, MessageListener> listeners     = new HashMap<String, MessageListener>();
    //channel -> listener ids
    private Map<String, List<String>>    routes        = new HashMap<String, List<String>>();
    private Map<String, Pattern>         patternRoutes = new HashMap<String, Pattern>();
    //用于派发接收到的任务的
    @Autowired
    @Qualifier("messagingPoolExecutor")
    private   ExecutorService executorService;
    //用于清理长时间block的派发任务
    @Autowired
    @Qualifier("timeoutScheduler")
    protected TaskScheduler   cleanScheduler;

    private int timeout = 1000 * 60 * 3;//3 minutes

    public void publish(String channel, String event) {
        List<String> listenerIds = findListenerIds(channel);
        for (String listenerId : listenerIds) {
            MessageListener listener = listeners.get(listenerId);
            DispatchJob job = new DispatchJob(listener, channel, event);
            submitJob(channel, listenerId, job);
        }
    }

    private List<String> findListenerIds(String channel) {
        List<String> staticListenerIds = routes.get(channel);
        List<String> dynamicListenerIds = filterByPattern(channel);

        if (staticListenerIds != null) {
            dynamicListenerIds.addAll(staticListenerIds);
        }
        return dynamicListenerIds;
    }

    private List<String> filterByPattern(String channel) {
        List<String> list = new LinkedList<String>();
        for (String listenerId : patternRoutes.keySet()) {
            Pattern pattern = patternRoutes.get(listenerId);
            if (pattern.matcher(channel).find()) {
                list.add(listenerId);
            }
        }
        return list;
    }

    @Override
    public void publish(String channel, byte[] event) {
        List<String> listenerIds = findListenerIds(channel);
        for (String listenerId : listenerIds) {
            MessageListener listener = listeners.get(listenerId);
            DispatchJob job = new DispatchJob(listener, channel, event);
            submitJob(channel,listenerId, job);
        }
    }

    @Override
    public void publishAll(String channel, String... events) {
        List<String> listenerIds = findListenerIds(channel);
        for (String listenerId : listenerIds) {
            MessageListener listener = listeners.get(listenerId);
            for (String event : events) {
                DispatchJob job = new DispatchJob(listener, channel, event);
                submitJob(channel, listenerId, job);
            }
        }
    }

    @Override
    public void publishAll(String channel, byte[]... events) {
        List<String> listenerIds = findListenerIds(channel);
        for (String listenerId : listenerIds) {
            MessageListener listener = listeners.get(listenerId);
            for (byte[] event : events) {
                DispatchJob job = new DispatchJob(listener, channel, event);
                submitJob(channel, listenerId, job);
            }
        }
    }

    private void submitJob(String channel, String listenerId, DispatchJob job) {
        Future<?> future = getExecutorService().submit(job);
        ZombieKiller timeoutJob = new ZombieKiller(future, "Publish to " + listenerId + "@" + channel);
        OnceTrigger trigger = new OnceTrigger(System.currentTimeMillis() + timeout);
        cleanScheduler.schedule(timeoutJob, trigger);
    }

    @Override
    public void subscribe(String listenerId, String[] channels, MessageListener listener) {
        logger.info("{} subscribe to {}", listenerId, StringUtils.join(channels, ","));
        listeners.put(listenerId, listener);
        for (String channel : channels) {
            List<String> listenerIds = routes.get(channel);
            if (listenerIds == null) {
                listenerIds = new ArrayList<String>();
                routes.put(channel, listenerIds);
            }
            if (!listenerIds.contains(listenerId)) {
                listenerIds.add(listenerId);
            }
        }
    }

    @Override
    public void subscribe(String listenerId, String pattern, MessageListener listener) {
        logger.info("{} subscribe to {}", listenerId, pattern);
        listeners.put(listenerId, listener);
        Pattern compiledPattern = PatternUtils.compile(pattern, Pattern.CASE_INSENSITIVE);
        patternRoutes.put(listenerId, compiledPattern);
    }

    @Override
    public void unsubscribe(String listenerId) {
        logger.info("{} unsubscribed", listenerId);
        listeners.remove(listenerId);
        patternRoutes.remove(listenerId);
        for (List<String> listenerIds : routes.values()) {
            listenerIds.remove(listenerId);
        }

    }

    /////////////////////////////////////////////////////////////////////
    // 可测试性相关接口
    /////////////////////////////////////////////////////////////////////

    /**
     * 额外提供的检测接口
     *
     * @param listenerId 消息接收者名称
     * @return 消息接收者
     */
    public MessageListener getListener(String listenerId) {
        return listeners.get(listenerId);
    }

    /**
     * 额外提供的检测接口
     *
     * @param channelId 通道名称
     * @return 消息接收者名称列表
     */
    public List<String> getListenerIds(String channelId) {
        return routes.get(channelId);
    }

    @ManagedAttribute(description = "消息转发线程池")
    public ExecutorService getExecutorService() {
        return executorService;
    }

    void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    void setCleanScheduler(TaskScheduler cleanScheduler) {
        this.cleanScheduler = cleanScheduler;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
