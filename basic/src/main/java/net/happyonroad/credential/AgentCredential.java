package net.happyonroad.credential;

/**
 * <h1>Agent Credential</h1>
 * Agent可以认为也是一种CLI通道，可以通过Agent的API执行命令
 *
 * @author Jay Xiong
 */
public class AgentCredential extends AbstractCredential implements CliCredential{
    //提供给agent的身份，可以为空
    private String user;
    //密码
    private String password;
    //agent的端口
    private int    port    = 5032;
    //命令超时
    private String timeout = "30s";

    public AgentCredential() {
        setType(Agent);
        setName(Agent);
        setOrder(15);
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

    public String getTimeout() {
        return timeout;
    }

    public void setTimeout(String timeout) {
        this.timeout = timeout;
    }

    private static final long serialVersionUID = -3218259110326361407L;
}
