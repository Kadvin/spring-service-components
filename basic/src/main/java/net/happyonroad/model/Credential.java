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
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
public interface Credential extends Serializable, PriorityOrdered {
    String Snmp       = "snmp";
    String Ssh        = "ssh";
    String Windows    = "windows";
    String Agent      = "agent";
    String Hypervisor = "hypervisor";

    String Local      = "local";

    String Webem      = "webem";
    String Jdbc       = "jdbc";
    String Jmx        = "jmx";

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
}
