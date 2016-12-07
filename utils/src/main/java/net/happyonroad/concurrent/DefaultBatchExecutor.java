/**
 * Developer: Kadvin Date: 14-2-19 下午5:58
 */
package net.happyonroad.concurrent;

import net.happyonroad.spring.Bean;
import net.happyonroad.util.MiscUtils;
import net.happyonroad.util.NamedThreadFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.*;

/**
 * The default batch executor
 * <p/>
 * 这里完成了一个经典的性能优化模式，就是将多次单个操作合并为一次批量操作
 * 单个操作: 将单个操作需要的操作信息封装到类型T的实例中，并通过submit将T存储起来
 * 批量操作: 通过BatchCallback将多个工作单位合并传回给调用者指定的具体实现
 */
@ManagedResource
public class DefaultBatchExecutor<T> extends Bean
        implements BatchExecutor<T>, Runnable {

    private volatile ArrayDeque<T> payloads = new ArrayDeque<T>(500);
    private NamedThreadFactory threadFactory;
    private int                threads;
    private BatchCallback<T>   callback;
    private int maxQueueSize = 10000; //内存临时队列的最大数量，超过这个数量，即便没有过间隔，也会立刻尝试处理数据
    private int interval     = 500; //批量检测间隔，越大，批量处理数据可能就越大，但时效性就降低
    private ExecutorService pool;
    private       Measurement input          = new Measurement();
    private       Measurement output         = new Measurement();
    // 用于对payloads控制
    private final Lock        lock           = new ReentrantLock();
    // 就绪通知
    private final Object      readyFeedback = new Object();
    // 工作通知
    private final Object      outputFeedback = new Object();
    // 输入反馈
    private final Object      inputFeedback  = new Object();

    @Override
    protected void performStart() {
        super.performStart();
        input.start();
        output.start();
        startThreadPool();
    }

    private void startThreadPool() {
        pool = Executors.newFixedThreadPool(threads, threadFactory);
        for (int i = 0; i < threads; i++) {
            pool.execute(this);
        }
        logger.debug("Started fixed thread pool {}", threads);
    }

    @Override
    public void submit(T task) {
        input.prepare();
        //用锁进行payloads修改
        lock.lock();
        try {
            payloads.add(task);
        } finally {
            lock.unlock();
        }
        input.increase(1);
        //当瞬时未处理的负载大于一定数量时，主动触发工作线程来处理
        notifyWorkersIfNecessary();
    }

    void notifyWorkersIfNecessary() {
        //以下代码为普通工作模式， 队列过长时，及时通知工作线程就罢了
        if (payloads.size() > maxQueueSize) {
            synchronized (outputFeedback) {
                outputFeedback.notifyAll();
            }
        }
        //以下代码为对输入线程的负反馈环节， 抑制输入速度，否则，输入速度过高，会导致处理环节 OOM
        if (payloads.size() > maxQueueSize * 5 ) {
            synchronized (inputFeedback){
                try {
                    inputFeedback.wait();
                } catch (InterruptedException e) {
                    //skip it
                }
            }
        }
    }

    @Override
    public void submit(T[] tasks) {
        input.prepare();
        //用锁进行payloads修改
        lock.lock();
        try{
            payloads.addAll(Arrays.asList(tasks));
        }finally {
            lock.unlock();
        }

        input.increase(tasks.length);
        //当瞬时未处理的负载大于一定数量时，主动触发工作线程来处理
        notifyWorkersIfNecessary();
    }

    @Override
    public void callbackWith(BatchCallback<T> callback) {
        this.callback = callback;
        synchronized (readyFeedback) {
            readyFeedback.notifyAll(); // notify all threads
        }
    }

    @Override
    public void run() {
        if (callback == null) {
            try {
                //做callback ready的通知
                synchronized (readyFeedback) {
                    readyFeedback.wait();
                }
                //wait the callback is set
            } catch (InterruptedException e) {
                //skip
            }
        }
        logger.info("Started");
        ArrayDeque<T> local = new ArrayDeque<T>(100);
        while (isRunning()) {
            //注意， 不要和数据对象交互用同一个锁
            output.prepare();

            if (payloads.isEmpty()) {
                //用这个锁，作为 queue size over 的通知， 表示可以继续输出
                synchronized (outputFeedback){
                    //在高峰情况，这个await时，可能会有n次submit，导致payloads里面积压许多数据
                    try {
                        outputFeedback.wait(interval);
                    } catch (InterruptedException e) {
                        //skip it
                    }
                }
            }
            if (payloads.isEmpty())
                continue;//进入下一次检测

            //用读锁做对payloads修改的控制
            lock.lock();
            try {
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
            } finally {
                lock.unlock();
            }

            // 工作时不需要任何锁
            try {
                callback.batchPerform(local);
                //工作完毕，通知所有可能被抑制的输入源
                synchronized (inputFeedback){
                    inputFeedback.notifyAll();
                }
                output.increase(local.size());
            } catch (Exception ex) {
                logger.error("Error while perform batch task: {}", MiscUtils.describeException(ex));
            } finally {
                local.clear();
            }
        }
        logger.info("Finished");
    }

    public void setThreadFactory(NamedThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
    }

    @ManagedAttribute
    public int getThreads() {
        return threads;
    }

    @ManagedAttribute
    public void setThreads(int threads) {
        if (this.threads == threads)
            return;
        this.threads = threads;
        if (this.pool != null) {
            this.stopping();
            this.pool.shutdown();
            try {
                pool.awaitTermination(5, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                logger.warn("Interrupted while wait");
            }
            this.starting();
            //restart them again
            startThreadPool();
        }
    }

    @ManagedAttribute
    public int getInterval() {
        return interval;
    }

    @ManagedAttribute
    public void setInterval(int interval) {
        this.interval = interval;
    }

    @ManagedAttribute
    public int getMaxQueueSize() {
        return maxQueueSize;
    }

    @ManagedAttribute
    public int getCurrentQueueSize() {
        return payloads.size();
    }

    @ManagedAttribute
    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    @ManagedOperation(description = "重置统计数据")
    public void resetMeasure() {
        input.reset();
        output.reset();
    }

    @ManagedAttribute(description = "每秒输入速度")
    public String getInputSpeedPerSecond() {
        return String.format("%.2f 个/秒", input.getSpeed());
    }

    @ManagedAttribute(description = "每分钟输入速度")
    public String getInputSpeedPerMinute() {
        return String.format("%.2f 个/分钟", input.getSpeed() * 60);
    }

    @ManagedAttribute(description = "每秒处理速度")
    public String getOutputSpeedPerSecond() {
        return String.format("%.2f 个/秒", output.getSpeed());
    }

    @ManagedAttribute(description = "每分钟处理速度")
    public String getOutputSpeedPerMinute() {
        return String.format("%.2f 个/分钟", output.getSpeed() * 60);
    }

    @ManagedAttribute(description = "每千个负载实际处理耗时")
    public String getAvgCost() {
        return String.format("%.2f 毫秒/千个", output.getAvgCost());
    }

}
