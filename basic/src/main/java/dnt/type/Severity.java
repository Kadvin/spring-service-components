/**
 * Developer: Kadvin Date: 14-3-3 下午1:32
 */
package dnt.type;

/**
 * 事件级别
 */
public enum Severity {
    /* 清除 */
    CLEAR,
    /* 一般 */
    INDETERMINATE,
    /* 警告 */
    WARNING,
    /* 次要 */
    MINOR,
    /* 严重 */
    MAJOR,
    /* 致命*/
    CRITICAL;

    public int value() { return ordinal() + 1; }
}
