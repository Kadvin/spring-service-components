/**
 * Developer: Kadvin Date: 14-5-7 下午2:22
 */
package net.happyonroad.type;

/**
 * <h1>优先级</h1>
 */
public enum Priority {
    VeryLow(-2), Low(-1), Normal(0), High(1), VeryHigh(2);

    private int value;

    Priority(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
