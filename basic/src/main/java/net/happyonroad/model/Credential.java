/**
 * Developer: Kadvin Date: 14-6-16 下午1:30
 */
package net.happyonroad.model;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

/**
 * 一般性的访问方式抽象
 */
@SuppressWarnings("UnusedDeclaration")
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public interface Credential extends Serializable {
    String Snmp       = "snmp";
    String Ssh        = "ssh";
    String Windows    = "windows";
    String Agent      = "agent";
    String Hypervisor = "hypervisor";
    String Webem      = "webem";
    String Jdbc       = "jdbc";
    String Jmx        = "jmx";
    String Local      = "local";

    /**
     * <h2>返回本认证方式的实际名称</h2>
     *
     * @return 认证方式的名称
     */
    String name();
}
