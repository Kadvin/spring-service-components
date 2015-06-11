package net.happyonroad.exception;

/**
 * <h1>查询出来的对象不唯一</h1>
 *
 * @author Jay Xiong
 */
public class RecordNotUniqueException extends RuntimeException {
    private static final long serialVersionUID = -7450881681584067479L;

    public RecordNotUniqueException(String message) {
        super(message);
    }
}
