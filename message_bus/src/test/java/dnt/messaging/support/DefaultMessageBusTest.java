/**
 * @author XiongJie, Date: 13-10-28
 */
package dnt.messaging.support;

import dnt.messaging.MessageAdapter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/** 测试默认的消息服务是否可用 */
public class DefaultMessageBusTest {
    private DefaultMessageBus messageBus;
    private MessageReceiver   listener, anotherListener;

    @Before
    public void setUp() throws Exception {
        messageBus = new DefaultMessageBus();
        messageBus.setExecutorService(Executors.newFixedThreadPool(2));
        messageBus.setCleanScheduler(new ConcurrentTaskScheduler());
        listener = new MessageReceiver();
        anotherListener = new MessageReceiver();
    }

    /**
     * 测试目的：
     * 默认的消息服务可以接受订阅
     * 验证方式：
     * 通过DefaultMessageListener额外提供的检测接口，检测消息Listener存在，并监听了相应的通道
     *
     * @throws Exception
     */
    @Test
    public void testSubscribe() throws Exception {
        messageBus.subscribe("the/listener", new String[]{"channel-a", "channel-b"}, listener);
        Assert.assertNotNull(messageBus.getListener("the/listener"));
        Assert.assertEquals(listener, messageBus.getListener("the/listener"));

        List<String> listeners = messageBus.getListenerIds("channel-a");
        Assert.assertTrue(listeners.contains("the/listener"));
        listeners = messageBus.getListenerIds("channel-b");
        Assert.assertTrue(listeners.contains("the/listener"));
    }

    /**
     * 测试目的：
     * 已订阅的消息服务可以取消
     * 验证方式：
     * 通过DefaultMessageListener额外提供的检测接口，检测消息Listener被移除，相应的通道也没有对应的监听器
     *
     * @throws Exception
     */
    @Test
    public void testUnsubscribe() throws Exception {
        messageBus.subscribe("the/listener", new String[]{"channel-a", "channel-b"}, listener);
        messageBus.unsubscribe("the/listener");

        Assert.assertNull(messageBus.getListener("the/listener"));

        List<String> listeners = messageBus.getListenerIds("channel-a");
        Assert.assertFalse(listeners.contains("the/listener"));
        listeners = messageBus.getListenerIds("channel-b");
        Assert.assertFalse(listeners.contains("the/listener"));
    }

    /**
     * 测试目的：
     * 消息服务能够发送到对应的Listeners处
     * 验证方式：
     * 检测listener接收到了消息，不该听到的Listener没有收到消息
     *
     * @throws Exception
     */
    @Test
    public void testPublish() throws Exception {
        messageBus.subscribe("the/listener", new String[]{"the/channel"}, listener );
        messageBus.publish("the/channel", "the/event");
        // 等待消息总线发出消息之后的对外notify通知，最多10ms
        //noinspection SynchronizeOnNonFinalField
        synchronized (listener){
            listener.wait(10);
        }
        Assert.assertEquals(1, listener.receivedSize());
        Assert.assertEquals(1, listener.receivedSize("the/channel"));
        Assert.assertEquals(0, listener.receivedSize("the/bad/channel"));

        Assert.assertEquals(0, anotherListener.receivedSize());
    }

    /**
     * 测试目的：
     * 消息服务能够发送到对应的Listeners处
     * 验证方式：
     * 检测两个listener均接收到了多个消息
     *
     * @throws Exception
     */
    @Test
    @Ignore("It will fail random because of performance issues")
    public void testPublishAllString() throws Exception {
        messageBus.subscribe("the/listener", new String[]{"the/channel"}, listener );
        messageBus.publishAll("the/channel", "the/event/1", "the/event/2");
        // 等待消息总线发出消息之后的对外notify通知, 2次, 每次最多10ms
        //noinspection SynchronizeOnNonFinalField
        synchronized (listener){
            listener.wait(5000);
            listener.wait(5000);
        }
        Assert.assertEquals(2, listener.receivedSize());
        Assert.assertEquals(2, listener.receivedSize("the/channel"));
        Assert.assertEquals(0, listener.receivedSize("the/bad/channel"));

        Assert.assertEquals(0, anotherListener.receivedSize());
    }

    /**
     * 测试目的：
     * 消息服务能够发送到对应的Listeners处
     * 验证方式：
     * 检测两个listener均接收到了多个消息
     *
     * @throws Exception
     */
    @Test
    @Ignore("It will fail random because of performance issues")
    public void testPublishAllBinary() throws Exception {
        messageBus.subscribe("the/listener", new String[]{"the/channel"}, listener );
        messageBus.publishAll("the/channel", "the/event/1".getBytes(), "the/event/2".getBytes());
        // 等待消息总线发出消息之后的对外notify通知, 2次, 每次最多10ms
        //noinspection SynchronizeOnNonFinalField
        synchronized (listener){
            listener.wait(5000);
            listener.wait(5000);
        }
        Assert.assertEquals(2, listener.receivedSize());
        Assert.assertEquals(2, listener.receivedSize("the/channel"));
        Assert.assertEquals(0, listener.receivedSize("the/bad/channel"));

        Assert.assertEquals(0, anotherListener.receivedSize());
    }

    /**
     * 辅助测试的消息接收器
     */
    private static class MessageReceiver extends MessageAdapter {
        private Map<String, List<byte[]>> receivedMessages = new HashMap<String, List<byte[]>>();

        @Override

        public void onMessage(String channel, String message) {
            List<byte[]> list = receivedMessages.get(channel);
            if (list == null) {
                list = new ArrayList<byte[]>();
                receivedMessages.put(channel, list);
            }
            list.add(message.getBytes());
            synchronized (this){
                notifyAll();
            }
        }

        @Override
        public void onMessage(String channel, byte[] message) {
            List<byte[]> list = receivedMessages.get(channel);
            if (list == null) {
                list = new ArrayList<byte[]>();
                receivedMessages.put(channel, list);
            }
            list.add(message);
            synchronized (this){
                notifyAll();
            }
        }

        public int receivedSize() {
            int i = 0;
            for (List<byte[]> strings : receivedMessages.values()) {
                i += strings.size();
            }
            return i;
        }

        public int receivedSize(String channel) {
            List<byte[]> list = receivedMessages.get(channel);
            return list == null ? 0 : list.size();
        }
    }
}
