/**
 * @author XiongJie, Date: 14-1-17
 */
package dnt.concurrent;

import dnt.util.NamedThreadFactory;
import dnt.util.TimeInterval;
import org.apache.commons.lang.WordUtils;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.export.naming.SelfNaming;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;

/**
 * <h1>基于策略的线程池</h1>
 * 设定其id，自动会去系统属性中获取：
 * <ul>
 * <li>name: 线程组名称</li>
 * <li>stackSize: 缺省的线程栈大小，byte格式，如 1024k, 64 mb 等</li>
 * <li>policy: 可以取值为{fixed(缺省), dynamic, cached}</li>
 * <li>
 * <dt>如果policy = fixed，则会取值:</dt>
 * <dl>
 * <dd>threadSize: 固定线程大小</dd>
 * </dl>
 * </li>
 * <li>
 * <dt>如果 policy = dynamic，则会取值:</dt>
 * <dl>
 * <dd>coreSize: 最少线程数量</dd>
 * <dd>maxSize: 最多线程数量</dd>
 * <dd>queueSize: 任务池大小，超过该大小时，开始进行线程扩充；如果线程池满了，则拒绝执行</dd>
 * <dd>keepAlive: 当线程过多的时候，多余的线程保留的时间，时间间隔格式，如 60s </dd>
 * </dl>
 * </li>
 * </ul>
 */
@SuppressWarnings("NullableProblems")
@ManagedResource(description = "多线程任务执行器")
public class StrategyExecutorService implements ExecutorService, SelfNaming {
    public static final String POLICY_FIXED   = "fixed"; //including single: threadSize = 1
    public static final String POLICY_DYNAMIC = "dynamic";
    public static final String POLICY_CACHED  = "cached";

    private final String              name;
    private       String              id;
    private       String              policy;
    private       ThreadPoolExecutor     underlying;
    private       NamedThreadFactory  factory;

    public StrategyExecutorService(String id) {
        this(id, null);
    }

    public StrategyExecutorService(String id, String name) {
        this.id = id;
        if (name == null) {
            this.name = System.getProperty(id + ".name", WordUtils.capitalize(id));
        } else {
            this.name = name;
        }
        String defaultPolicy = System.getProperty("default.threadingPolicy", POLICY_FIXED);
        this.policy = System.getProperty(id + ".policy", defaultPolicy);
        String stackSize = getStringSetting("stackSize", "128k");
        this.factory = new NamedThreadFactory(this.name, stackSize);
        this.underlying = build(factory);
    }

    private ThreadPoolExecutor build(ThreadFactory factory) {
        if (POLICY_FIXED.equalsIgnoreCase(policy)) {
            Integer threadSize = getIntegerSetting("threadSize", 2);
            return (ThreadPoolExecutor) Executors.newFixedThreadPool(threadSize, factory);
        } else if (POLICY_CACHED.equalsIgnoreCase(policy)) {
            return (ThreadPoolExecutor) Executors.newCachedThreadPool(factory);
        } else if (POLICY_DYNAMIC.equalsIgnoreCase(policy)) {
            Integer queueSize = getIntegerSetting("queueSize", 50);
            Integer coreSize = getIntegerSetting("coreSize", 5);
            Integer maxSize = getIntegerSetting("maxSize", 20);
            String keepAlive = getStringSetting("keepAlive", "60s");
            int interval = TimeInterval.parseInt(keepAlive);
            ArrayBlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(queueSize);
            return new ThreadPoolExecutor(coreSize, maxSize, interval, TimeUnit.MILLISECONDS, queue, factory);
        } else {
            throw new IllegalArgumentException("The executor policy value " + policy + " is illegal, " +
                                               "we accept (fixed|dynamic|cached)");
        }
    }

    @ManagedAttribute
    public String getId() {
        return id;
    }

    @ManagedAttribute
    public String getPolicy() {
        return policy;
    }

    @ManagedAttribute
    public String getName() {
        return name;
    }

    @ManagedAttribute(description = "线程池新建的线程栈大小")
    public String getStackSize() {
        return this.factory.getStackSize();
    }

    @ManagedAttribute
    public void setStackSize(String stackSize) {
        this.factory.setStackSize(stackSize);
    }

    @ManagedAttribute(description = "固定线程池大小")
    public Integer getThreadSize() {
        if (this.policy.equalsIgnoreCase(POLICY_FIXED)) {
            return underlying.getCorePoolSize();
        } else return null;
    }

    @ManagedAttribute
    public void setThreadSize(Integer threadSize){
        if( this.policy.equalsIgnoreCase(POLICY_FIXED)){
            this.underlying.setCorePoolSize(threadSize);
            this.underlying.setMaximumPoolSize(threadSize);
        }else{
            throw new IllegalArgumentException("thread size only supported by policy = fixed");
        }
    }

    @ManagedAttribute(description = "动态线程池任务缓存队列大小(设置值)")
    public int getQueueSize() {
        if (this.policy.equalsIgnoreCase(POLICY_DYNAMIC)) {
            ArrayBlockingQueue queue = (ArrayBlockingQueue) underlying.getQueue();
            return queue.size() + queue.remainingCapacity();
        } else return 0;
    }

    @ManagedAttribute(description = "当前动态线程池任务缓存队列大小(实际值)")
    public Integer getCurrentQueueSize(){
        if (this.policy.equalsIgnoreCase(POLICY_DYNAMIC)) {
            ArrayBlockingQueue queue = (ArrayBlockingQueue) underlying.getQueue();
            return queue.size();
        } else return null;
    }

    @ManagedAttribute(description = "动态线程池最少线程数量")
    public Integer getCoreSize() {
        if (this.policy.equalsIgnoreCase(POLICY_DYNAMIC)) {
            return underlying.getCorePoolSize();
        } else return null;
    }

    @ManagedAttribute
    public void setCoreSize(Integer coreSize){
        if( this.policy.equalsIgnoreCase(POLICY_DYNAMIC)){
            this.underlying.setCorePoolSize(coreSize);
        }else{
            throw new IllegalArgumentException("Core size only supported by policy = dynamic");
        }
    }

    @ManagedAttribute(description = "动态线程池最多线程数量")
    public Integer getMaxSize() {
        if (this.policy.equalsIgnoreCase(POLICY_DYNAMIC)) {
            return this.underlying.getMaximumPoolSize();
        } else return null;

    }

    @ManagedAttribute
    public void setMaxSize(Integer maxSize){
        if( this.policy.equalsIgnoreCase(POLICY_DYNAMIC)){
            this.underlying.setMaximumPoolSize(maxSize);
        }else{
            throw new IllegalArgumentException("Max size only supported by policy = dynamic");
        }
    }


    @ManagedAttribute(description = "动态线程池空余线程闲置时间")
    public String getKeepAlive() {
        if (this.policy.equalsIgnoreCase(POLICY_DYNAMIC)) {
            return this.underlying.getKeepAliveTime(TimeUnit.SECONDS) + "s" ;
        } else return null;
    }

    @ManagedAttribute
    public void setKeepAlive(String keepAlive){
        if( this.policy.equalsIgnoreCase(POLICY_DYNAMIC)){
            int interval = TimeInterval.parseInt(keepAlive);
            this.underlying.setKeepAliveTime(interval, TimeUnit.MILLISECONDS);
        }else{
            throw new IllegalArgumentException("Keep alive only supported by policy = dynamic");
        }
    }

    /////////////////////////////////////////////////////////////////////
    // Delegate 接口实现
    /////////////////////////////////////////////////////////////////////

    @ManagedAttribute(description = "当前线程池中线程数量")
    public int getPoolThreadCount(){
        return underlying.getPoolSize();
    }

    @ManagedAttribute(description = "线程池中正在执行的线程数量")
    public int getActiveThreadCount(){
        return underlying.getActiveCount();
    }

    @ManagedAttribute(description = "线程池最多曾有过的线程数量")
    public int getLargestThreads(){
        return underlying.getLargestPoolSize();
    }

    @ManagedAttribute(description = "提交过来的任务总数")
    public long getSubmitTaskCount(){
        return underlying.getTaskCount();
    }

    @ManagedAttribute(description = "完成的任务总数")
    public long getCompletedTaskCount(){
        return underlying.getCompletedTaskCount();
    }

    protected Integer getIntegerSetting(String name, Integer systemDefaultValue) {
        return Integer.valueOf(getStringSetting(name, systemDefaultValue.toString()));
    }

    protected String getStringSetting(String name, String systemDefaultValue) {
        String defaultValue = System.getProperty("default." + name, systemDefaultValue);
        return System.getProperty(id + "." + name, defaultValue);
    }


    /////////////////////////////////////////////////////////////////////
    // Delegate 接口实现
    /////////////////////////////////////////////////////////////////////


    @Override
    public void shutdown() {
        underlying.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
        return underlying.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
        return underlying.isShutdown();
    }

    @Override
    public boolean isTerminated() {
        return underlying.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return underlying.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return underlying.submit(task);
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
        return underlying.submit(task, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return underlying.submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return underlying.invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException {
        return underlying.invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return underlying.invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
            throws InterruptedException, ExecutionException, TimeoutException {
        return underlying.invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(Runnable command) {
        underlying.execute(command);
    }

    @Override
    public ObjectName getObjectName() throws MalformedObjectNameException {
        return new ObjectName("dnt.components:name=" + getId() + "ExecutorService");
    }
}
