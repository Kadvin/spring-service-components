/**
 * Developer: Kadvin Date: 14/12/23 下午2:07
 */
package net.happyonroad.type;

/**
 * 尺寸大小
 */
public class Size {
    public int width;
    public int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Size() {
        this(0, 0);
    }

    public String toString() {
        return "Size(" + this.width + ", " + this.height + ")";
    }

}
