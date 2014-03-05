/**
 * @author XiongJie, Date: 13-10-24
 */
package dnt.cache;

import java.util.List;

/**
 * <h1>Cache中 list 类型数据的访问接口</h1>
 * <p/>
 * 主要用于在不将整个List获取到当前进程内时进行操作
 * <p>各个API的含义，可参考Redis的List Commands</p>
 */
public interface ListContainer extends ListChannel{

    /**
     * 从 list 的左边取出一个值
     *
     * @return list 的左边的第一个值, list为空时返回null
     */
    String popLeft();

    /**
     * 从 list 的右边取出一个值
     *
     * @return list 的右边的第一个值, list为空时返回null
     */
    String popRight();

    /**
     * 从 list 的左边取出一个值
     *
     * @param timeout 超时时间(单位秒)
     * @return list 的左边的第一个值, list为空时则等待直到list有值或超时, 超时时返回null
     */
    String blockPopLeft(int timeout);


    /**
     * 向 list 的右边插入一个值
     *
     * @param value 值
     */
    void pushRight(String value);

    /**
     * 向 list 的右边插入一系列值
     *
     * @param value 值
     */
    void pushRight(List<String> value);

    /**
     * 移除 list 中左边第 start 开始 end 结束的数据
     *
     * @param start 开始位置
     * @param end   结束位置
     */
    boolean trim(int start, int end);

    /**
     * 列出 list 中左边第 start 开始 end 结束的数据
     *
     * @param start 开始位置
     * @param end   结束位置
     * @return 子表内容
     */
    String[] subList(final int start, final int end);

    /**
     * 列出所有的值
     *
     * @return 被导出的列表
     */
    String[] toArray();

    /**
     * 获取列表的大小
     *
     * @return 大小
     */
    int size();
}
