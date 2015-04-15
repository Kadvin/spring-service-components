/**
 * Developer: Kadvin Date: 14/12/23 下午2:07
 */
package net.happyonroad.type;

import java.io.Serializable;

/**
 * 尺寸大小
 */
public class Size implements Serializable{
    private static final long serialVersionUID = -5473196418893325374L;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Size)) return false;

        Size size = (Size) o;

        if (height != size.height) return false;
        if (width != size.width) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + height;
        return result;
    }
}
