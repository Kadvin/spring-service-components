/**
 * Developer: Kadvin Date: 14-6-16 下午1:30
 */
package net.happyonroad.model;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * 一般性的访问方式抽象
 */
@SuppressWarnings("UnusedDeclaration")
@JsonTypeInfo(use= JsonTypeInfo.Id.CLASS, include= JsonTypeInfo.As.PROPERTY, property = "class")
public interface Credential {
    String Snmp       = "snmp";
    String Ssh        = "ssh";
    String Windows    = "windows";
    String Agent      = "agent";
    String Hypervisor = "hypervisor";
}
