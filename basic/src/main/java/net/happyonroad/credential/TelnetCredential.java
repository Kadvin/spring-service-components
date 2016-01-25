package net.happyonroad.credential;

/**
 * <h1>Telnet 访问参数</h1>
 *
 * @author Jay Xiong
 */
public class TelnetCredential extends AbstractCredential implements CliCredential {
    private static final long serialVersionUID = 1200524055861549577L;
    private String term = "VT100";
    //用户名是可选的，密码也是可选的
    private String user, password;
    //从普通用户变为超级用户的命令，以及超级用户密码
    private String changeSuper = "su", superPassword;
    //Telnet的端口
    private int    port    = 23;
    //命令超时
    private String timeout = "30s";
    private String charset = "utf8";
    private String promotions = "\\$>#%";

    public TelnetCredential() {
        setName(Telnet);
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
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

    public String getChangeSuper() {
        return changeSuper;
    }

    public void setChangeSuper(String changeSuper) {
        this.changeSuper = changeSuper;
    }

    public String getPromotions() {
        return promotions;
    }

    public void setPromotions(String promotions) {
        this.promotions = promotions;
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
