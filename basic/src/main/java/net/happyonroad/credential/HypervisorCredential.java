/**
 * Developer: Kadvin Date: 14-5-6 下午8:34
 */
package net.happyonroad.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.happyonroad.model.Credential;

import java.util.Collections;
import java.util.Map;

/**
 * <h1>Hypervisor(Vmware, OpenStack, Xen) Credential</h1>
 * 通过 hypervisor 访问（具体使用方式尚不清楚）
 */
public class HypervisorCredential extends AbstractCredential {
    private static final long serialVersionUID = 1241488875878014909L;
    private boolean https;
    private String user;
    private String password;

    public HypervisorCredential() {
        this(Collections.emptyMap());
    }

    public HypervisorCredential(Map map) {
        setType(Hypervisor);
        if (map.containsKey("https")) {
            https = (Boolean) map.get("https");
        }
        setName((String) map.get("name"));
        this.user = (String) map.get("user");
        this.password = (String) map.get("password");
    }

    @JsonIgnore
    @Override
    public int getOrder() {
        return 30;
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
