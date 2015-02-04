/**
 * @author XiongJie, Date: 14-7-25
 */
package net.happyonroad.exception;

/**
 *  The extension load exception
 */
@SuppressWarnings("UnusedDeclaration")
public class ExtensionException extends Exception {
    public ExtensionException() {
    }

    public ExtensionException(String message) {
        super(message);
    }

    public ExtensionException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExtensionException(Throwable cause) {
        super(cause);
    }

    public ExtensionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ExtensionException(String message, Exception cause) {
        super(message, cause);
    }
}
