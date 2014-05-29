/**
 * Developer: Kadvin Date: 14-5-26 下午2:23
 */
package dnt.type;

/**
 * <h1>可用性状态</h1>
 *
 * 资源或组件的可用性状态，分为:
 * <ul>
 * <li>可用
 * <li>不可用
 * <li>未知
 * </ul>
 * 资源或者组件的可用性往往需要依据某个关键指标的计算，如：
 * 对于某个主机，其可用性状态可以基于其Ping服务的可用性
 */
public enum Availability {
    /**
     * 可用
     */
    Available,
    /**
     * 不可用
     */
    Unavailable,
    /**
     * 未知
     */
    Unknown
}
