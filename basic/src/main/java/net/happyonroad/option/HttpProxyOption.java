/**
 * Developer: Kadvin Date: 14-5-6 下午8:54
 */
package net.happyonroad.option;

import net.happyonroad.model.Option;

/**
 * Http proxy option
 */
public class HttpProxyOption implements Option {
    private String host;
    private int    port;
    private String user;
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
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
