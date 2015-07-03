/**
 * @author XiongJie, Date: 13-11-5
 */
package net.happyonroad.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

/** 统一命名的线程工厂 */
@SuppressWarnings("UnusedDeclaration")
public class NamedThreadFactory implements ThreadFactory, Thread.UncaughtExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(NamedThreadFactory.class);

    private String                          groupName;
    private int                             sequence;
    private ThreadGroup                     group;
    private Thread.UncaughtExceptionHandler handler;
    // Unit: Bytes
    private long                            stackSize;

    public NamedThreadFactory(String groupName) {
        this(groupName, null, "0k");//zero will be ignored
    }

    public NamedThreadFactory(String groupName, String stackSize) {
        this(groupName, null, stackSize);
    }

    public NamedThreadFactory(String groupName, Thread.UncaughtExceptionHandler handler, String stackSize) {
        this.groupName = groupName;
        this.group = new ThreadGroup(groupName);
        this.handler = (handler != null) ? handler : this;
        setStackSize(stackSize);
    }

    // treat as bytes
    public void setStackSize(long stackSize) {
        this.stackSize = stackSize;
    }

    public void setStackSize(String stackSize) {
        this.stackSize = StringUtils.parseBytes(stackSize);
    }

    public String getStackSize() {
        return StringUtils.humanReadableByteCount(stackSize);
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public Thread newThread(Runnable job) {
        Thread thread = new Thread(group, job, groupName + String.format("-%03d", ++sequence), stackSize);
        thread.setUncaughtExceptionHandler(handler);
        return thread;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        logger.error("Thread " + t.getName() + " exit on: " + e.getMessage(), e);
    }

    public void setName(String name) {
        this.groupName = name;
    }
}
