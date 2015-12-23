/**
 * Developer: Kadvin Date: 14-9-23 上午9:51
 */
package net.happyonroad.listener;

import net.happyonroad.exception.SystemInvokeException;
import net.happyonroad.model.SystemInvocation;

/**
 * The system invocation listener interface adapter
 */
public abstract class SystemInvocationListenerAdapter  implements SystemInvocationListener{
    @Override
    public boolean care(SystemInvocation invocation) {
        return true;
    }

    @Override
    public void added(SystemInvocation invocation) {

    }

    @Override
    public void started(SystemInvocation invocation) {

    }

    @Override
    public void stepExecuted(SystemInvocation invocation) {

    }

    @Override
    public void finished(SystemInvocation invocation) {

    }

    @Override
    public void cancelled(SystemInvocation invocation) {

    }

    @Override
    public void failed(SystemInvocation invocation, SystemInvokeException e) {

    }
}
