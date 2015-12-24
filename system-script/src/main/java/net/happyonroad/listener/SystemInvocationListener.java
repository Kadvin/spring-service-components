/**
 * Developer: Kadvin Date: 14-9-18 上午9:08
 */
package net.happyonroad.listener;

import net.happyonroad.exception.SystemInvokeException;
import net.happyonroad.model.SystemInvocation;

import java.util.EventListener;

/**
 * <h1>系统任务执行情况的监听器</h1>
 */
public interface SystemInvocationListener extends EventListener {
    /**
     * 询问这个  listener 是否关心该调用消息
     * @param invocation 调用
     * @return  是否关心
     */
    boolean care(SystemInvocation invocation);
    /**
     * 当一个系统调用被加入到系统任务中时，本监听者将会被通过该接口通知到
     *
     * @param invocation 调用
     */
    void added(SystemInvocation invocation);

    /**
     * 当一个系统调用的线程被实际启动时，本监听者将会被通过该接口通知到
     *
     * @param invocation 调用
     */
    void started(SystemInvocation invocation);

    /**
     * 当某个系统调用的特定步骤被执行时，会发出该消息
     *
     * @param invocation 系统调用
     */
    void stepExecuted(SystemInvocation invocation);

    /**
     * 接收到到消息
     *
     * @param invocation 调用
     * @param message 消息
     */
    void onMessage(SystemInvocation invocation, String message);

    /**
     * 当一个系统调用正常完成时，本监听者将会被通过该接口通知到
     *
     * @param invocation 调用
     */
    void finished(SystemInvocation invocation);

    /**
     * 当一个系统调用被取消时，本监听者将会被通过该接口通知到
     *
     * @param invocation 调用
     */
    void cancelled(SystemInvocation invocation);

    /**
     * 当一个系统调用失败时，本监听者将会被通过该接口通知到
     *
     * @param invocation 调用
     * @param e exception
     */
    void failed(SystemInvocation invocation, SystemInvokeException e);
}
