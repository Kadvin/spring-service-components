/**
 * Developer: Kadvin Date: 14-6-13 下午2:11
 */
package net.happyonroad.util;

import com.fasterxml.jackson.databind.util.StdConverter;
import net.happyonroad.credential.HypervisorCredential;
import net.happyonroad.credential.SnmpCredential;
import net.happyonroad.credential.SshCredential;
import net.happyonroad.credential.WindowsCredential;
import net.happyonroad.model.Credential;
import net.happyonroad.credential.CredentialProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Credential Converter
 */
public class CredentialConverter extends StdConverter<Map<String, Object>, Map<String, Credential>> {
    @Override
    public Map<String, Credential> convert(Map<String, Object> map) {
        if( map == null ) return new HashMap<String, Credential>();
        Map<String, Credential> converted = new HashMap<String, Credential>(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.equalsIgnoreCase(Credential.Ssh)) {
                SshCredential credential;
                if (value instanceof Map) {
                    credential = new SshCredential((Map) value);
                } else if (value instanceof SshCredential) {
                    credential = (SshCredential) value;
                } else
                    throw new IllegalArgumentException("Can't convert " + value + " as SshCredential");
                converted.put(key, credential);
            } else if (key.equalsIgnoreCase(Credential.Snmp)) {
                SnmpCredential credential;
                if (value instanceof Map) {
                    credential = new SnmpCredential((Map) value);
                } else if (value instanceof SnmpCredential) {
                    credential = (SnmpCredential) value;
                } else {
                    throw new IllegalArgumentException("Can't convert " + value + " as SnmpCredential");
                }
                converted.put(key, credential);
            } else if (key.equalsIgnoreCase(Credential.Windows)) {
                WindowsCredential credential;
                if( value instanceof Map){
                    credential = new WindowsCredential((Map)value);
                }else{
                    credential = (WindowsCredential) value;
                }
                converted.put(key, credential);
            } else if (key.equalsIgnoreCase(Credential.Hypervisor)) {
                HypervisorCredential credential;
                if( value instanceof Map){
                    credential = new HypervisorCredential((Map)value);
                }else{
                    credential = (HypervisorCredential) value;
                }
                converted.put(key, credential);
            } else if( value instanceof Map){
                Map rawMap = (Map) value;
                CredentialProperties credential = new CredentialProperties();
                credential.putAll(rawMap);
                converted.put(key, credential);
            }else {
                throw new UnsupportedOperationException("Do not support the credential key: "
                                                        + key + ", and value is:" + value);
            }
        }
        return converted;
    }
}
