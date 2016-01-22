package net.happyonroad.credential;

/**
 * <h1>Telnet 访问参数</h1>
 *
 * @author Jay Xiong
 */
public class TelnetCredential extends AbstractCredential implements CliCredential {
    private static final long serialVersionUID = 1200524055861549577L;
    //用户名是可选的，密码也是可选的
    private String user, password, superPassword;
    //普通提示符
    private String promotion = ">", superPromotion = "#";
    //Telnet的端口
    private int    port    = 23;
    private String timeout = "30s";
    private String charset = "utf8";

    public TelnetCredential() {
        setName(Telnet);
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

    public String getSuperPassword() {
        return superPassword;
    }

    public void setSuperPassword(String superPassword) {
        this.superPassword = superPassword;
    }

    public String getPromotion() {
        return promotion;
    }

    public void setPromotion(String promotion) {
        this.promotion = promotion;
    }

    public String getSuperPromotion() {
        return superPromotion;
    }

    public void setSuperPromotion(String superPromotion) {
        this.superPromotion = superPromotion;
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

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }
}
