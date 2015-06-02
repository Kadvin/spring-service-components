/**
 * Developer: Kadvin Date: 14-5-14 下午1:27
 */
package net.happyonroad.type;

import java.util.Collections;
import java.util.Set;

/**
 * <h1>性能状态</h1>
 * <p/>
 * 这可能是性能指标的状态，也可能是组件或者资源/链路的性能状态
 * <ul>
 * 有以下几个注意点:
 * <li>关键指标的状态决定宿主(组件/资源/链路)的状态
 * <li>关键组件的状态决定资源的状态
 * </ul>
 * 指标状态的原始来源：可计算的性能指标，如CPU平均利用率，落在不同的区间，导致不通的指标状态
 */
public enum Performance implements Comparable<Performance>{
    // Unknown, 性能状态未知, ordinal = 0
    Unknown,
    // Normal: 正常工作中，对应绿色，ordinal = 1
    Normal,
    // Warning: 警告状态，对应黄色，或者叫做 “轻微超标”，ordinal = 2
    Warning,// Minor in Mocha
    // Error：错误状态，对应红色，或者叫做 “严重超标”， ordinal = 3
    Critical;  // Serious in Mocha

    /**
     * <h2>下一级性能状态</h2>
     *
     * @return 性能状态
     */
    public Performance decrease() {
        if (this == Critical)
            return Warning;
        else if (this == Warning)
            return Normal;
        else
            return this;
    }

    public static Performance highest(Set<Performance> set) {
        return Collections.max(set);
    }
}
