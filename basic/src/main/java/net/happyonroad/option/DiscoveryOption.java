/**
 * Developer: Kadvin Date: 14-5-20 下午2:27
 */
package net.happyonroad.option;

import net.happyonroad.model.Option;
import net.happyonroad.type.IdentifyMethod;

import java.util.Set;

/**
 * 运行(一次)网络发现时所需要用到的选项
 */
public class DiscoveryOption implements Option {
    // 设备识别/标志方式(根据ip，dns name，或是别的方法)
    private IdentifyMethod identifyMethod;
    // 需要自动发现的网络服务名称，要与采集层约定
    private Set<String>    networkServices;
    // 开放心态：针对相应网段所有的ip进行发现
    // 不开放的心态：则仅针对已有节点进行重新发现
    private boolean        openMind;

    public IdentifyMethod getIdentifyMethod() {
        return identifyMethod;
    }

    public void setIdentifyMethod(IdentifyMethod identifyMethod) {
        this.identifyMethod = identifyMethod;
    }

    public Set<String> getNetworkServices() {
        return networkServices;
    }

    public void setNetworkServices(Set<String> networkServices) {
        this.networkServices = networkServices;
    }

    public boolean isOpenMind() {
        return openMind;
    }

    public void setOpenMind(boolean openMind) {
        this.openMind = openMind;
    }
}
