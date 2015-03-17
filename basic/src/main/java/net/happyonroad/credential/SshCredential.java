/**
 * Developer: Kadvin Date: 14-6-16 下午1:34
 */
package net.happyonroad.credential;

import net.happyonroad.model.Credential;
import net.happyonroad.util.ParseUtils;

import java.util.Collections;
import java.util.Map;

/**
 * <h1>SSH访问参数</h1>
 * 虽然在现实中，几乎只有*nix主机采用SSH认证后采集方式
 * 但其实Windows主机也可以在开通SSH服务之后，采用SSH认证/PowerShell, Batch, VBS采集方式
 */
public class SshCredential implements Credential {
    private static final long serialVersionUID = 6186435172976706185L;
    private String user, password;
    //SSH的端口
    private int port = 22;
    private int timeout;

    public SshCredential() {
        this(Collections.emptyMap());
    }

    public SshCredential(String user, String password) {
        this.user = user;
        this.password = password;
        this.port = 22;
        this.timeout = 1000;
    }

    public SshCredential(Map map) {
        this.user = (String) map.get("user");
        this.password = (String) map.get("password");
        this.port = ParseUtils.parseInt(map.get("port"), 22);
        this.timeout = ParseUtils.parseInt(map.get("timeout"), 1000);
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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
