/**
 * Developer: Kadvin Date: 14-5-26 下午2:36
 */
package net.happyonroad.type;

import java.util.Collections;
import java.util.Set;

/**
 * <h1>配置状态</h1>
 */
public enum ConfigStatus {
    // 配置变化与否未知
    Unknown, //ordinal = 0
    // 配置发生变更
    Unchanged,//ordinal = 1
    // 配置未变更
    Changed; //ordinal = 2


    public static ConfigStatus highest(Set<ConfigStatus> configStatuses) {
        return Collections.max(configStatuses);
    }
}
