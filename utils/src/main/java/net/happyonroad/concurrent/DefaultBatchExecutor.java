/**
 * Developer: Kadvin Date: 14-2-19 下午5:58
 */
package net.happyonroad.concurrent;

import net.happyonroad.spring.Bean;
import net.happyonroad.util.NamedThreadFactory;

import java.util.ArrayDeque;

/**
 * The default batch executor
 * <p/>
 * 这里完成了一个经典的性能优化模式，就是将多次单个操作合并为一次批量操作
 * 单个操作: 将单个操作需要的操作信息封装到类型T的实例中，并通过submit将T存储起来
 * 批量操作: 通过BatchCallback将多个工作单位合并传回给调用者指定的具体实现
 */
public class DefaultBatchExecutor<T> extends Bean
        implements BatchExecutor<T>, Runnable {

    private volatile ArrayDeque<T> payloads = new ArrayDeque<T>(500);
    private NamedThreadFactory threadFactory;
    private int                threads;
    private int interval = 500; //批量检测间隔，越大，批量处理数据可能就越大，但时效性就降低
    private BatchCallback<T> callback;

    @Override
    protected void performStart() {
        for (int i = 0; i < threads; i++) {
            threadFactory.newThread(this).start();
        }
    }

    @Override
    public void submit(T task) {
        payloads.add(task);
    }

    @Override
    public void callbackWith(BatchCallback<T> callback) {
        this.callback = callback;
    }

    @Override
    public void run() {
        ArrayDeque<T> local = new ArrayDeque<T>(100);
        while (isRunning()) {
            synchronized (this) {
                if (callback == null)
                    throw new IllegalStateException("The batch callback is not bind");
                if (payloads.isEmpty()) {
                    //在高峰情况，这个wait之后，可能会有n次submit，导致payloads里面积压许多数据
                    try {
                        wait(interval);
                    } catch (InterruptedException e) {
                        //skip
                    }
                }
                if(payloads.isEmpty())
                    continue;
                // redis hmset do not accept empty collection(map)
                if (local.isEmpty()) {
                    //把当前任务交换到local变量里面去
                    ArrayDeque<T> temp = local;
                    local = payloads;
                    payloads = temp;
                } else {
                    //把payloads里面的任务放到local里面去
                    local.addAll(payloads);
                    payloads.clear();
                }
                try {
                    callback.batchPerform(local);
                } catch (Exception ex) {
                    logger.error("Error while perform batch task", ex);
                } finally {
                    local.clear();
                }
            }
        }
    }

    public void setThreadFactory(NamedThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}
