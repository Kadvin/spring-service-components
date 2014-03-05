/**
 * @author XiongJie, Date: 13-11-14
 */
package dnt.cache;

/**
 * 将用于通讯的List抽取为一个最简单的接口，便于远端Wrap
 */
public interface ListChannel {
    /**
     * 从 list 的右边所有的消息
     *
     * @param timeout 超时时间(单位秒)
     * @return list 所有的值, 按照从右到左的顺序排序,最右边在list的第一位，依次类推 超时时返回null
     */
    byte[] blockPopRight(int timeout);


    /**
     * 向 list 的左边插入一个值
     *
     * @param value 值
     */
    void pushLeft(byte[] value);
}
