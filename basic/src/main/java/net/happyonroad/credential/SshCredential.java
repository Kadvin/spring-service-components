/**
 * Developer: Kadvin Date: 14-6-16 下午1:34
 */
package net.happyonroad.credential;

import net.happyonroad.model.Credential;
import net.happyonroad.support.JsonSupport;

import java.util.Map;

/**
 * SSH访问参数
 */
public class SshCredential extends JsonSupport implements Credential {
    private String user, password;
    private int timeout;

    public SshCredential() {
    }

    public SshCredential(String user, String password, int timeout) {
        this.user = user;
        this.password = password;
        this.timeout = timeout;
    }

    public SshCredential(Map map) {
        this.user = (String) map.get("user");
        this.password = (String) map.get("password");
        this.timeout = parseInt(map.get("timeout"), 1000);
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

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SshCredential)) return false;

        SshCredential sshAccess = (SshCredential) o;

        if (timeout != sshAccess.timeout) return false;
        if (password != null ? !password.equals(sshAccess.password) : sshAccess.password != null) return false;
        if (!user.equals(sshAccess.user)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + timeout;
        return result;
    }
}
