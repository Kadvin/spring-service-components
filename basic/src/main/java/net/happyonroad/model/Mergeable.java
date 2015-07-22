package net.happyonroad.model;

/**
 * <h1>合并的能力</h1>
 *
 * @author Jay Xiong
 */
public interface Mergeable<T> {

    void merge(T another);

}
