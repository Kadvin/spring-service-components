/**
 * Developer: Kadvin Date: 14-9-15 下午4:15
 */
package net.happyonroad.support;

import net.happyonroad.concurrent.OnceTrigger;
import net.happyonroad.exception.SystemInvokeException;
import net.happyonroad.listener.InvocationEventBroadcaster;
import net.happyonroad.listener.ListenerNotifier;
import net.happyonroad.listener.SystemInvocationListener;
import net.happyonroad.model.SystemInvocation;
import net.happyonroad.service.SystemInvokeService;
import net.happyonroad.service.SystemInvoker;
import net.happyonroad.spring.Bean;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

/**
 * <h1>系统调用服务</h1>
 */
@Service
public class SystemInvokeManager extends Bean implements SystemInvokeService, InvocationEventBroadcaster {
    private Set<SystemInvocationListener> listeners = new LinkedHashSet<SystemInvocationListener>();
    @Autowired
    @Qualifier("systemInvokeExecutor")
    ExecutorService systemInvokeExecutor;
    @Autowired
    SystemInvoker systemInvoker;

    @Autowired
    protected TaskScheduler cleanScheduler;

    private Map<String, Future<Integer>>             futures   = new ConcurrentHashMap<String, Future<Integer>>();
    private Map<String, InvocationExecutor> executors = new ConcurrentHashMap<String, InvocationExecutor>();

    @Override
    public void addListener(SystemInvocationListener listener) {
        if(!listeners.contains(listener))
            listeners.add(listener);
    }

    @Override
    public void removeListener(SystemInvocationListener listener) {
        listeners.remove(listener);
    }

    @Override
    public long getFileLength(String invocationId) {
        File logFile = getFile(invocationId);
        if (logFile.exists()) return logFile.length();
        return 0;
    }

    @Override
    public String addJob(final SystemInvocation invocation) {
        String originId = invocation.getId();
        if( originId == null ) {
            invocation.setId(UUID.randomUUID().toString());
        } else if( futures.containsKey(originId)){
            int i = 1;
            String newId = originId + "@" + i;
            while(futures.containsKey(newId)){
                newId = originId + "@" + ++i;
            }
            invocation.setId(newId);
        }
        InvocationExecutor executor = createExecutor(invocation);
        executors.put(invocation.getId(), executor);
        Future<Integer> future = systemInvokeExecutor.submit(executor);
        futures.put(invocation.getId(), future);

        InvocationKiller killer = createKiller(executor, future);
        long timeout = invocation.totalTimeout();
        OnceTrigger trigger = new OnceTrigger(System.currentTimeMillis() + timeout);
        logger.debug("Schedule invocation killer for {} after: {} ms", invocation.getId(), timeout);
        cleanScheduler.schedule(killer, trigger);

        broadcast(new ListenerNotifier() {
            public void notify(SystemInvocationListener listener) {
                if( listener.care(invocation)) listener.added(invocation);
            }
        });
        return invocation.getId();
    }

    @Override
    public void cancelJob(String invocationId) {
        Future future = futures.get(invocationId);
        if( future == null ) return;
        final InvocationExecutor executor = getExecutor(invocationId);
        if(executor == null) return;
        cancel(future, executor);
    }

    private InvocationExecutor getExecutor(String invocationId) {
        return executors.get(invocationId);
    }

    @Override
    public boolean isFinished(String invocationId) {
        Future future = futures.get(invocationId);
        return future == null || future.isDone();
    }

    @Override
    public int waitJobFinished(String invocationId) throws SystemInvokeException {
        Future<Integer> future = futures.get(invocationId);
        InvocationExecutor executor = executors.get(invocationId);
        if( future == null || executor == null) return 0;
        //最多等任务原先设置的最大超时时间
        long timeout = executor.invocation.totalTimeout();
        try {
            return future.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            throw new SystemInvokeException("Interrupted while wait", e);
        } catch (ExecutionException e) {
            throw new SystemInvokeException("Execution failed while wait", e);
        } catch (TimeoutException e) {
            throw new SystemInvokeException("Execution timeout: " + timeout + " while wait", e);
        } catch (CancellationException e) {
            throw new SystemInvokeException("Execution timeout: " + timeout + " while wait", e);
        }
    }

    @Override
    public long read(String invocationId, long offset, final List<String> result) {
        File logFile = getFile(invocationId);
        if( !logFile.exists() ) return offset;
        FileInputStream stream = null;
        List<String> lines = null;
        try {
            stream = new FileInputStream(logFile);
            IOUtils.skip(stream, offset);
            lines = IOUtils.readLines(stream);
        } catch (IOException e) {
            return offset;
        } finally {
            IOUtils.closeQuietly(stream);
        }
        result.addAll(lines);
        long newOffset = offset;
        for (String line : lines) {
            newOffset += line.getBytes().length + 1;
        }
        return newOffset;
    }

    private File getFile(String invocationId) {
        return new File(System.getProperty("app.home"), "tmp/" + invocationId + ".log");
    }

    ////////////////////////////////////////////////////////////////////
    // Inner Implementations
    ////////////////////////////////////////////////////////////////////


    InvocationExecutor createExecutor(SystemInvocation invocation) {
        return new InvocationExecutor(invocation);
    }

    InvocationKiller createKiller(InvocationExecutor executor, Future<?> future) {
        return new InvocationKiller(executor, future);
    }

    public void broadcast(ListenerNotifier notifier){
        for (SystemInvocationListener listener : listeners) {
            try {
                notifier.notify(listener);
            } catch (Exception e) {
                logger.warn("Error while notify " + listener, e);
            }
        }
    }

    protected void cancel(Future future, final InvocationExecutor executor) {
        logger.trace("Cancel {}/{}", future, executor);
        if( future.isDone() ) return;
        if( future.isCancelled() ) return;
        executor.cancel();
        if( future.cancel(true)){
            broadcast(new ListenerNotifier() {
                @Override
                public void notify(SystemInvocationListener listener) {
                    if(listener.care(executor.invocation)) listener.cancelled(executor.invocation);
                }
            });
        }
    }


    class InvocationExecutor implements Callable<Integer>{
        private final SystemInvocation invocation;

        public InvocationExecutor(SystemInvocation invocation) {
            this.invocation = invocation;
        }

        @Override
        public Integer call() {
            broadcast(new ListenerNotifier() {
                @Override
                public void notify(SystemInvocationListener listener) {
                    if(listener.care(invocation)) listener.started(invocation);
                }
            });
            // perform real invocation
            try {
                int result = systemInvoker.invoke(invocation);
                clean();
                broadcast(new ListenerNotifier() {
                    @Override
                    public void notify(SystemInvocationListener listener) {
                        if(listener.care(invocation)) listener.finished(invocation);
                    }
                });
                return result;
            } catch (final SystemInvokeException e) {
                logger.error("Failed to run:" + e.getMessage(), e);
                clean();
                broadcast(new ListenerNotifier() {
                    @Override
                    public void notify(SystemInvocationListener listener) {
                        if(listener.care(invocation)) listener.failed(invocation, e);
                    }
                });
                return 1;
            }
        }

        public void cancel() {
            // TODO: TRY TO CANCEL this executing task
            // such as set the flag to break the while loop
            // or run a kill -9 for the started process
        }

        protected void clean() {
            executors.remove(invocation.getId());
            futures.remove(invocation.getId());
        }
    }

    class InvocationKiller implements Runnable {
        private final InvocationExecutor executor;
        private final Future<?>          future;

        public InvocationKiller(InvocationExecutor executor, Future<?> future) {
            this.executor = executor;
            this.future = future;
        }

        @Override
        public void run() {
            cancel(future, executor);
            executor.clean();
        }
    }

}
