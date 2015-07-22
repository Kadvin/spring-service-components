package net.happyonroad.model;

/**
 * <h1>反向合并能力</h1>
 * 反向合并是指，this.merge(another)时，如果出现冲突的属性，应保留this的属性，而非another的
 *
 * @author mnnjie
 */
public interface ReverseMergeable<T> {
    void reverseMerge(T another);
}
