/**
 * xiongjie on 14-8-6.
 */
package net.happyonroad.util;

import net.happyonroad.service.AutoNumberService;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>基础的自动序号服务</h1>
 * 主要是完成了格式和数字的转换，实际实现类只需要做计数即可
 */
public abstract class AbstractAutoNumberService implements AutoNumberService {


    protected Map<String, NumberRule> configurations = new HashMap<String, NumberRule>();

    @Override
    public void configure(String catalog, String rule) {
        NumberRule numberRule = NumberRule.parse(rule);
        configurations.put(process(catalog), numberRule);
    }

    public NumberRule getConfiguration(String catalog) {
        String name = process(catalog);
        NumberRule rule = configurations.get(name);
        if (rule == null ){
            rule = NumberRule.parse(name + "_%06d@1000");
            setConfiguration(name, rule);
        }
        return rule;
    }

    public void setConfiguration(String catalog, NumberRule rule){
        configurations.put(process(catalog), rule);
    }

    protected String process(String catalog) {
        return catalog.toUpperCase();
    }

}
