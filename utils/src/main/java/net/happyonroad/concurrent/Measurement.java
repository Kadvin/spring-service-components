package net.happyonroad.concurrent;

import java.util.concurrent.atomic.AtomicLong;

/**
 * <h1>用于统计性能指标</h1>
 * <p/>
 * 本对象不应该被用于多个线程之间，仅用于特定线程的性能统计
 *
 * @author Jay Xiong
 */
public class Measurement {
    //本次统计的起始时间
    private long       startAt     = 0;
    //总计处理的payload数量
    private AtomicLong total       = new AtomicLong(0);
    //总计的耗时，不包括wait时间
    private AtomicLong cost        = new AtomicLong(0);
    private long       costStartAt = 0;

    public void start() {
        this.startAt = System.currentTimeMillis();
    }

    public void prepare() {
        this.costStartAt = System.currentTimeMillis();
    }

    /**
     * 本环节已经处理完毕的数量
     *
     * @param count payload数量
     */
    public void increase(int count) {
        cost.addAndGet((System.currentTimeMillis() - costStartAt));
        total.addAndGet(count);
    }

    public void reset() {
        this.startAt = System.currentTimeMillis();
        this.total.set(0);
        this.costStartAt = 0;
        this.cost.set(0);
    }

    public double getSpeed() {
        if (startAt == 0) return 0;
        return 1000 * total.get() / (double) (System.currentTimeMillis() - startAt);
    }

    public double getAvgCost() {
        if (total.get() == 0) return 0;
        return 1000.0 * cost.get() / total.get();
    }

    public long getTotal() {
        return total.get();
    }

    public long getCost() {
        return cost.get();
    }
}
