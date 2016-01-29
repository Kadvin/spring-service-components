/**
 * Developer: Kadvin Date: 14-9-15 下午4:14
 */
package net.happyonroad.service;

import net.happyonroad.exception.SystemInvokeException;
import net.happyonroad.listener.SystemInvocationListener;
import net.happyonroad.model.SystemInvocation;

/**
 * <h1>系统调用服务</h1>
 * TODO 本模块，包括模型，可能需要抽取到平台中
 */
public interface SystemInvokeService {
    /**
     * <h2>调度一个系统调用的任务</h2>
     *
     * @param invocation 任务信息
     * @return 任务描述符
     */
    String addJob(SystemInvocation invocation);

    /**
     * <h2>取消执行某个任务</h2>
     * <p/>
     * 只有进入队列，尚未执行的任务可以正常的cancel
     * 如果任务已经在执行了，尝试interrupt，但不保证一定能取消
     *
     * @param invocationId 任务描述符
     */
    void cancelJob(String invocationId);

    /**
     * <h2>判断某个任务是否已经完成 </h2>
     *
     * @param invocationId 任务描述符
     * @return 是否完成
     */
    boolean isFinished(String invocationId);

    /**
     * <h2>等待任务结束</h2>
     *
     * @param invocationId 任务描述符
     * @return Result Code, 有时候，有些调用返回码错误，但没异常
     * @throws SystemInvokeException 异常
     */
    int waitJobFinished(String invocationId) throws SystemInvokeException;

    /**
     * <h2>增加一个系统任务执行的监听器</h2>
     *
     * @param listener 监听器
     */
    void addListener(SystemInvocationListener listener);

    /**
     * <h2>取消特定监听器对系统任务执行的监听</h2>
     *
     * @param listener 监听器
     */
    void removeListener(SystemInvocationListener listener);

}
