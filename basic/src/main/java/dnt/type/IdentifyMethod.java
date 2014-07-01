/**
 * Developer: Kadvin Date: 14-5-14 下午1:14
 */
package dnt.type;

/**
 * Node识别方法
 */
public enum IdentifyMethod {
    ByIp, ByDnsName, Auto
    // auto method: 针对不同的设备类型，采用不同的方式
    //   网络设备，Server，路由器按照ip地址
    //   其他类型按照： dns name
}
