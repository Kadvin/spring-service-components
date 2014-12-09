/**
 * Developer: Kadvin Date: 14-2-21 下午1:13
 */
package net.happyonroad.concurrent;

import junit.framework.Assert;
import net.happyonroad.concurrent.StrategyExecutorService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.RejectedExecutionException;

/**
 * 测试基于策略的线程池
 */
public class StrategyExecutorServiceTest {
    StrategyExecutorService service;
    boolean ending = false;

    @Before
    public void setUp() throws Exception {
        System.setProperty("test.policy", "dynamic");
        System.setProperty("test.stackSize", "32k");
        System.setProperty("test.queueSize", "2");
        System.setProperty("test.coreSize", "1");
        System.setProperty("test.maxSize", "2");
        System.setProperty("test.keepAlive", "60s");
        service = new StrategyExecutorService("test");
    }

    @After
    public void tearDown() throws Exception {
        ending = true;
        service.shutdown();
    }

    /**
     * 测试目的：
     * 针对实际运行中，经常出现的，当采取动态线程策略时
     * 队列满时，线程数量并不从coreSize向maxSize增长的问题
     *
     * @throws Exception
     */
    @Test
    public void testDynamicThreadGrown() throws Exception {
        addTask();
        assertSize(1, 0);
        addTask();
        assertSize(1, 1);
        addTask();
        assertSize(1, 2);
        addTask();
        assertSize(2, 2);
        try {
            addTask();
            Assert.fail("It shouldn't go here");
        } catch (Exception e) {
            Assert.assertTrue(e instanceof RejectedExecutionException);
        }
    }

    synchronized void addTask(){
        service.submit(new InfiniteTask());

        try {
            wait(500);
        } catch (InterruptedException e) {
            //skip it
        }
    }

    void assertSize(int threadSize, int queueSize){
        Assert.assertEquals(threadSize, service.getActiveThreadCount());
        Assert.assertEquals(new Integer(queueSize), service.getCurrentQueueSize());
    }

    class InfiniteTask implements Runnable {
        @Override
        public void run() {
            while(!ending){
                try {
                    System.out.println(Thread.currentThread().getName() + " perform infinite task");
                    Thread.sleep(10000);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
