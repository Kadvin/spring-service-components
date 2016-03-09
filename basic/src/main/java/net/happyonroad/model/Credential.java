/**
 * Developer: Kadvin Date: 14-6-16 下午1:30
 */
package net.happyonroad.model;


import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.core.PriorityOrdered;

import java.io.Serializable;

/**
 * <h2>一般性的访问方式抽象</h2>
 * 认证方式也有先后优先级
 */
@SuppressWarnings("UnusedDeclaration")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
public interface Credential extends Serializable, PriorityOrdered {
    String Snmp       = "snmp";
    String CLI        = "cli";
    String CLI_SQL    = "cli_sql";
    String Ssh        = "ssh";
    String Telnet     = "telnet";
    String Windows    = "windows";
    String Agent      = "agent";
    String Hypervisor = "hypervisor";

    String Local = "local";

    String Webem = "webem";
    String Jdbc  = "jdbc";
    String Jmx   = "jmx";

    /**
     * <h2>返回本认证方式的实际名称</h2>
     *
     * @return 认证方式的名称
     */
    String getName();

    /**
     * <h2>返回本认证方式的类型</h2>
     *
     * @return 认证方式的类型
     */
    String getType();

    /**
     * 返回本认证是否生效
     *
     * @return 是否生效
     */
    boolean isEnabled();

    /**
     * <h2>判断本认证信息，是否可以用于特定采集方式</h2>
     * 所谓的采集方式，指的是一种engine到被管对象之间的协议交互方式，如:
     * <ul>
     * <li>cli 是指 执行命令
     * <li>snmp 是指 执行snmp get/walk
     * <li>jdbc 是指 执行 sql
     * <li>jmx 是指 执行 jmx
     * </ul>
     *
     * @param type 采集方式
     * @return 是否可用
     */
    boolean support(String type);
}
