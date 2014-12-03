/**
 * Developer: Kadvin Date: 14-5-6 下午8:34
 */
package net.happyonroad.credential;

import net.happyonroad.model.Credential;

import java.util.Collections;
import java.util.Map;

/**
 * Hypervisor(Vmware, OpenStack, Xen) Credential
 */
public class HypervisorCredential implements Credential {
    private boolean https;
    private String user;
    private String password;

    public HypervisorCredential() {
        this(Collections.emptyMap());
    }

    public HypervisorCredential(Map map) {
        if(map.containsKey("https")){
            https = (Boolean) map.get("https");
        }
        this.user = (String) map.get("user");
        this.password = (String) map.get("password");
    }

    public boolean isHttps() {
        return https;
    }

    public void setHttps(boolean https) {
        this.https = https;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
