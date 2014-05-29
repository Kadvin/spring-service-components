/**
 * Developer: Kadvin Date: 14-5-14 下午1:27
 */
package dnt.type;

/**
 * <h1>性能状态</h1>
 *
 * 这可能是性能指标的状态，也可能是组件或者资源/链路的性能状态
 * <ul>
 * 有以下几个注意点:
 * <li>关键指标的状态决定宿主(组件/资源/链路)的状态
 * <li>关键组件的状态决定资源的状态
 * </ul>
 * 指标状态的原始来源：可计算的性能指标，如CPU平均利用率，落在不同的区间，导致不通的指标状态
 */
public enum Performance {
    // Normal: 正常工作中，对应绿色
    Normal,
    // Warning: 警告状态，对应黄色，或者叫做 “轻微超标”
    Warning,// Minor in Mocha
    // Error：错误状态，对应红色，或者叫做 “严重超标”
    Error,  // Serious in Mocha
    // 未知
    Unknown
}
