/**
 * Developer: Kadvin Date: 14-5-26 下午2:36
 */
package net.happyonroad.type;

import java.util.Collections;
import java.util.Set;

/**
 * <h1>配置状态</h1>
 */
public enum Configuration {
    // 配置变化与否未知
    Unknown("U"), //ordinal = 0
    // 配置未变更
    Unchanged("N"),//ordinal = 1
    // 配置发生变更
    Changed("C"); //ordinal = 2


    public static Configuration highest(Set<Configuration> configurations) {
        return Collections.max(configurations);
    }

    private String flag;

    Configuration(String flag) {
        this.flag = flag;
    }

    public String getFlag() {
        return flag;
    }
}
