/**
 * Developer: Kadvin Date: 14-3-3 下午2:37
 */
package net.happyonroad.type;

/**
 * 事件状态
 */
public enum AckStatus {
    Unacked(0),
    Acked(1);

    private int value;

    AckStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
