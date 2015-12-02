/**
 * Developer: Kadvin Date: 14-6-16 下午1:34
 */
package net.happyonroad.credential;

import com.fasterxml.jackson.annotation.JsonIgnore;
import net.happyonroad.type.TimeInterval;
import net.happyonroad.util.ParseUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Collections;
import java.util.Map;

import static net.happyonroad.util.ParseUtils.parseBoolean;

/**
 * <h1>SSH访问参数</h1>
 * 虽然在现实中，几乎只有*nix主机采用SSH认证后采集方式
 * 但其实Windows主机也可以在开通SSH服务之后，采用SSH认证/PowerShell, Batch, VBS采集方式
 */
public class SshCredential extends AbstractCredential implements CliCredential {
    private static final long serialVersionUID = 6186435172976706185L;
    public static final  String DEFAULT_TIMEOUT  = "30s";

    public static final String AUTH_PASSWORD    = "password";
    public static final String AUTH_PUBLICKEY   = "publickey";
    public static final String AUTH_INTERACTIVE = "interactive";
    public static final String AUTH_NONE        = "none";

    private String authenticateMethod;// password, publickey, interactive, none
    // 当认证方式为 publickey时，用户名/密码仍然是需要的，只是此时的密码是对私钥的保护密码
    private String user, password;
    // 私钥有两种方式，一种是文件，一种直接是内容
    private String permFile, privateKey;
    //SSH的端口
    private int port = 22;
    private String timeout;

    public SshCredential() {
        this(Collections.emptyMap());
    }

    public SshCredential(String user, String password) {
        setType(Ssh);
        setUser(user);
        setAuthenticateMethod(AUTH_PASSWORD);
        setPassword(password);
        setPort(22);
        setTimeout(DEFAULT_TIMEOUT);
    }

    public SshCredential(String user, File permFile) {
        setType(Ssh);
        setUser(user);
        setAuthenticateMethod(AUTH_PUBLICKEY);
        setPermFile(permFile.getAbsolutePath());
        setPort(22);
        setTimeout(DEFAULT_TIMEOUT);
    }

    public SshCredential(Map map) {
        setType(Ssh);
        setName((String) map.get("name"));
        if (getName() == null) setName(getType());
        setEnabled(parseBoolean(map.get("enabled"), true));
        setUser((String) map.get("user"));
        setPassword((String) map.get("password"));
        setPermFile((String) map.get("permFile"));
        setPrivateKey((String) map.get("privateKey"));
        if (StringUtils.hasText(this.permFile) || StringUtils.hasText(this.privateKey)) {
            setAuthenticateMethod(AUTH_PUBLICKEY);
        }
        setPort(ParseUtils.parseInt(map.get("port"), 22));
        setTimeout(ParseUtils.parseString(map.get("timeout"), DEFAULT_TIMEOUT));
    }

    @JsonIgnore
    @Override
    public int getOrder() {
        return 20;
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

    public String getPermFile() {
        return permFile;
    }

    public void setPermFile(String permFile) {
        this.permFile = permFile;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    @JsonIgnore
    public TimeInterval getTimeoutInterval(){
        return new TimeInterval(timeout);
    }

    public String getAuthenticateMethod() {
        return authenticateMethod;
    }

    public void setAuthenticateMethod(String authenticateMethod) {
        this.authenticateMethod = authenticateMethod;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SshCredential)) return false;

        SshCredential sshAccess = (SshCredential) o;

        if (!timeout.equals(sshAccess.timeout)) return false;
        if (password != null ? !password.equals(sshAccess.password) : sshAccess.password != null) return false;
        if (!user.equals(sshAccess.user)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = user.hashCode();
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + timeout.hashCode();
        return result;
    }

    public String toString() {
        return "SshCredential(" + user + ")";
    }

    @JsonIgnore
    public boolean isAuthByPassword() {
        return AUTH_PASSWORD.equals(this.authenticateMethod);
    }

    @JsonIgnore
    public boolean isAuthByPublickey() {
        return AUTH_PUBLICKEY.equals(this.authenticateMethod);
    }
}
