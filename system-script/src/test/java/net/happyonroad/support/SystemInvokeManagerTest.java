/**
 * Developer: Kadvin Date: 14-9-18 上午11:07
 */
package net.happyonroad.support;

import net.happyonroad.SystemInvokeConfig;
import net.happyonroad.exception.SystemInvokeException;
import net.happyonroad.listener.SystemInvocationListenerAdapter;
import net.happyonroad.model.LocalInvocation;
import net.happyonroad.model.RemoteInvocation;
import net.happyonroad.model.SystemInvocation;
import net.happyonroad.system.*;
import net.happyonroad.system.Process;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Test the system invoke manager
 */
@ContextConfiguration(classes = SystemInvokeConfig.class)
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
//@Ignore("Need linux/osx, trusted by app2.dev.itsnow.com")
public class SystemInvokeManagerTest extends AbstractProcessTest {
    private static  Logger logger = LoggerFactory.getLogger(SystemInvokeManagerTest.class);

    @Autowired
    SystemInvokeManager systemInvokerManager;
    SystemInvocationMonitor monitor = new SystemInvocationMonitor();
    SystemInvocation invocation;

    Semaphore semaphore = new Semaphore(0);

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        systemInvokerManager.addListener(monitor);
        invocation = new LocalInvocation() {
            @Override
            public int perform(Process process) throws Exception {
                logger.info("First invocation started and blocked");
                semaphore.acquire();
                logger.info("First invocation started and resumed");
                return process.run("./test.sh", "hello", "world");
            }
        }.next(new RemoteInvocation(remoteHost, remoteDir) {
            @Override
            public int perform(net.happyonroad.system.Process process) throws Exception {
                logger.info("Second invocation started and blocked");
                semaphore.acquire();
                logger.info("Second invocation started and resumed");
                return process.run("./test.sh", "hi", "itsnow!");
            }
        });
    }

    @Override
    @After
    public void tearDown() throws Exception {
        systemInvokerManager.removeListener(monitor);
    }

    @Test

    public void testAddJob() throws Exception {
        String invocationId = systemInvokerManager.addJob(invocation);
        Assert.assertTrue(monitor.heard(invocationId, "added"));
        Assert.assertFalse(systemInvokerManager.isFinished(invocationId));
        monitor.waitMessage(invocationId, "started");
        Assert.assertTrue(monitor.heard(invocationId, "started"));
        Assert.assertFalse(systemInvokerManager.isFinished(invocationId));
        semaphore.release();
        logger.info("Release first semaphore");
        Assert.assertFalse(systemInvokerManager.isFinished(invocationId));
        monitor.waitMessage(invocationId, "stepExecuted");
        semaphore.release();
        logger.info("Release second semaphore");
        monitor.waitMessage(invocationId, "finished");
        Assert.assertTrue(monitor.heard(invocationId, "finished"));
        Assert.assertTrue(systemInvokerManager.isFinished(invocationId));
    }

    @Test
    public void testCancelJob() throws Exception {
        String invocationId = systemInvokerManager.addJob(invocation);
        Assert.assertTrue(monitor.heard(invocationId, "added"));
        Assert.assertFalse(systemInvokerManager.isFinished(invocationId));
        monitor.waitMessage(invocationId, "started");
        Assert.assertTrue(monitor.heard(invocationId, "started"));
        Assert.assertFalse(systemInvokerManager.isFinished(invocationId));
        semaphore.release();
        logger.info("Release first semaphore");
        Assert.assertFalse(systemInvokerManager.isFinished(invocationId));
        monitor.waitMessage(invocationId, "stepExecuted");
        systemInvokerManager.cancelJob(invocationId);
        monitor.waitMessage(invocationId, "cancelled");
        Assert.assertTrue(monitor.heard(invocationId, "cancelled"));
        Assert.assertTrue(systemInvokerManager.isFinished(invocationId));
    }

    @Test
    public void testWaitJobFinished() throws Exception {
        final StringBuilder sb = new StringBuilder();
        final String invocationId = systemInvokerManager.addJob(invocation);
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    if( !monitor.heard(invocationId, "started") )
                        monitor.waitMessage(invocationId, "started");
                } catch (InterruptedException e) {
                    //ensure the semaphore released when the invocation started
                }
                sb.append("releasing(2)\n");
                semaphore.release(2);
                sb.append("released(2)\n");
            }
        });
        systemInvokerManager.waitJobFinished(invocationId);
        Assert.assertTrue(sb.length() > 0);
    }

    @Test
    public void testJobTimeout() throws Exception {
        invocation.timeout(100);
        invocation.getNext().timeout(100);
        String invocationId = systemInvokerManager.addJob(invocation);
        try {
            systemInvokerManager.waitJobFinished(invocationId);
            Assert.fail("It should throw timeout related exception");
        } catch (SystemInvokeException e) {
            Assert.assertTrue(e.getMessage().startsWith("Execution timeout"));
        }
    }

    private static class SystemInvocationMonitor extends SystemInvocationListenerAdapter {
        private Map<String, List<String>>           messages   = new LinkedHashMap<String, List<String>>();
        private Map<String, Map<String, Semaphore>> semaphores = new LinkedHashMap<String, Map<String, Semaphore>>();

        @Override
        public void added(SystemInvocation invocation) {
            logger.info("Heard  {} added", invocation.getId());
            getMessageList(invocation).add("added");
            notifyWaiter(invocation.getId(), "added");
            logger.info("Notify {} added", invocation.getId());
        }

        @Override
        public void started(SystemInvocation invocation) {
            logger.info("Heard  {} started", invocation.getId());
            getMessageList(invocation).add("started");
            notifyWaiter(invocation.getId(), "started");
            logger.info("Notify {} started", invocation.getId());
        }

        @Override
        public void stepExecuted(SystemInvocation invocation) {
            logger.info("Heard  {} stepExecuted:{}", invocation.getId(), invocation.getSequence());
            getMessageList(invocation).add("stepExecuted");
            notifyWaiter(invocation.getId(), "stepExecuted");
            logger.info("Notify {} stepExecuted:{}", invocation.getId(), invocation.getSequence());
        }

        @Override
        public void finished(SystemInvocation invocation) {
            logger.info("Heard  {} finished", invocation.getId());
            getMessageList(invocation).add("finished");
            notifyWaiter(invocation.getId(), "finished");
            logger.info("Notify {} finished", invocation.getId());
        }

        @Override
        public void cancelled(SystemInvocation invocation) {
            logger.info("Heard  {} cancelled", invocation.getId());
            getMessageList(invocation).add("cancelled");
            notifyWaiter(invocation.getId(), "cancelled");
            logger.info("Notify {} cancelled", invocation.getId());
        }

        @Override
        public void failed(SystemInvocation invocation, SystemInvokeException e) {
            logger.info("Heard  {} failed", invocation.getId());
            getMessageList(invocation).add("failed");
            notifyWaiter(invocation.getId(), "failed");
            logger.info("Notify {} failed", invocation.getId());
        }

        public boolean heard(String invocationId, String message) {
            return getMessageList(invocationId).contains(message);
        }

        void notifyWaiter(String invocationId, String message) {
            Semaphore semaphore = getSemaphore(invocationId, message);
            semaphore.release(10);// acquirers should be meet
        }

        synchronized Semaphore getSemaphore(String invocationId, String message) {
            Semaphore semaphore;
            Map<String, Semaphore> semaphores = this.semaphores.get(invocationId);
            if (semaphores == null) {
                semaphores = new HashMap<String, Semaphore>();
                this.semaphores.put(invocationId, semaphores);
            }
            semaphore = semaphores.get(message);
            if (semaphore == null) {
                semaphore = new Semaphore(0);
                semaphores.put(message, semaphore);
            }
            return semaphore;
        }

        private List<String> getMessageList(String invocationId) {
            List<String> list = messages.get(invocationId);
            if (list == null) {
                list = new LinkedList<String>();
                messages.put(invocationId, list);
            }
            return list;
        }

        private List<String> getMessageList(SystemInvocation invocation) {
            return getMessageList(invocation.getId());
        }


        public void waitMessage(String invocationId, String message) throws InterruptedException {
            getSemaphore(invocationId, message).acquire();
        }
    }
}
