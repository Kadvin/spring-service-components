/**
 * Developer: Kadvin Date: 14-3-3 下午1:32
 */
package net.happyonroad.type;

/**
 * 事件级别
 */
public enum Severity {
    /* 清除 */
    CLEAR(0),
    /* 一般 */
    INDETERMINATE(1),
    /* 警告 */
    WARNING(2),
    /* 次要 */
    MINOR(3),
    /* 严重 */
    MAJOR(4),
    /* 致命*/
    CRITICAL(5);

    private int value;

    Severity(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
