package net.happyonroad.el;

/**
 * <h1>表达式计算异常</h1>
 *
 * @author Jay Xiong
 */
public class CalculateException extends Exception {
    public CalculateException(String message, Throwable cause) {
        super(message, cause);
    }
}
