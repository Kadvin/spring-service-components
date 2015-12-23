/**
 * Developer: Kadvin Date: 14-9-15 下午5:08
 */
package net.happyonroad.exception;

/**
 * 系统调用的异常
 */
public class SystemInvokeException extends ServiceException {
    public SystemInvokeException(String message) {
        super(message);
    }

    public SystemInvokeException(String message, Exception e) {
        super(message);
        initCause(e);
    }
}
