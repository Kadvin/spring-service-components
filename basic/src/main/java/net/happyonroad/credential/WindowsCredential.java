/**
 * Developer: Kadvin Date: 14-5-6 下午8:25
 */
package net.happyonroad.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.Map;

/**
 * <h1>Windows 认证所需资料</h1>
 * Windows认证信息支持多种采集方式，包括：
 * <ul>
 * <li> WMI
 * <li> WinRM
 * </ul>
 */
public class WindowsCredential extends AbstractCredential implements WmiCredential {
    private static final long serialVersionUID = 1008151410229282958L;
    //认证域，可以为空
    private String domain;
    //如果用户有限定在某个主机
    //那么用户名应该形如： Host/User
    private String user;
    private String password;


    public WindowsCredential() {
        this(Collections.emptyMap());
    }

    public WindowsCredential(Map map) {
        setName ((String) map.get("name"));
        if( getName() == null ) setName(Windows);
        this.user = (String) map.get("user");
        this.password = (String) map.get("password");
    }

    @JsonIgnore
    @Override
    public int getOrder() {
        return 20;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
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

    public String toString() {
        if (StringUtils.isEmpty(domain)) {
            return "WindowCredential(" + user + ")";
        } else {
            return "WindowCredential(" + user + "@" + domain + ")";
        }
    }
}
