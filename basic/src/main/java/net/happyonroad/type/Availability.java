/**
 * Developer: Kadvin Date: 14-5-26 下午2:23
 */
package net.happyonroad.type;

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
 *
 * 这个状态与TMN定义的Availability Status有很大的差异，其包括：
 * <ul>
 * <li>In test
 * <li>Failed
 * <li>Power off
 * <li>Off line
 * <li>Off duty
 * <li>Dependency
 * <li>Degraded
 * <li>Not installed
 * <li>Log full
 * </ul>
 */
public enum Availability {
    /**
     * 可用
     */
    Available, //  Operational status: Up
    /**
     * 不可用
     */
    Unavailable,// Operational status: Down
    /**
     * 未知
     */
    Unknown
}
