/**
 * Developer: Kadvin Date: 14-9-21 下午4:45
 */
package net.happyonroad.service;

import net.happyonroad.exception.SystemInvokeException;
import net.happyonroad.model.SystemInvocation;

/**
 * <h1>The system invoker</h1>
 */
public interface SystemInvoker {
    /**
     * <h2>invoke a invocation chain</h2>
     *
     * @param invocation the invocation chain
     * @return the last return code
     * @throws SystemInvokeException any exception
     */
    int invoke(SystemInvocation invocation) throws SystemInvokeException;
}
