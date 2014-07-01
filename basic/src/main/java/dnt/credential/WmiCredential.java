/**
 * Developer: Kadvin Date: 14-5-6 下午8:25
 */
package dnt.credential;

import dnt.model.Credential;

import java.util.Collections;
import java.util.Map;

/**
 * WMI 认证所需资料
 */
public class WmiCredential implements Credential {
    //如果用户有限定在某个主机
    //那么用户名应该形如： Host/User
    private String user;
    private String password;


    public WmiCredential() {
        this(Collections.emptyMap());
    }

    public WmiCredential(Map map) {
        this.user = (String) map.get("user");
        this.password = (String) map.get("password");
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
