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
    /* 提示 */
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

    public static Severity valueOf(int severity){
        switch (severity){
            case 0: return CLEAR;
            case 1: return INDETERMINATE;
            case 2: return WARNING;
            case 3: return MINOR;
            case 4: return MAJOR;
            case 5: return CRITICAL;
            default: throw new IllegalArgumentException("Invalid severity value " + severity);
        }
    }
}
