/**
 * @author XiongJie, Date: 14-7-25
 */
package net.happyonroad.exception;

/**
 *  The extension load exception
 */
public class ExtensionException extends Exception {
    public ExtensionException(String message, Exception cause) {
        super(message, cause);
    }
}
