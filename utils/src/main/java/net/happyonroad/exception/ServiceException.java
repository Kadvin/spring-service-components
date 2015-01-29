/**
 * Developer: Kadvin Date: 14-7-10 下午9:58
 */
package net.happyonroad.exception;

/**
 * 服务层抛出的错误
 *
 * 这层的异常不定义为Runtime Exception子类的目的，
 * 是为了让WEB层开发者在调用到底层的服务时，必须显性化的Catch这些异常，
 * 并转换为Web层Exception: WebClientSideException, WebServerSideException
 */
public class ServiceException extends Exception {
    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ServiceException() {
    }
}
