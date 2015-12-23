/**
 * Developer: Kadvin Date: 14-9-20 上午11:21
 */
package net.happyonroad.model;

import net.happyonroad.util.StringUtils;

/**
 * <h1>对远程主机的调用</h1>
 */
public abstract class RemoteInvocation extends SystemInvocation{
    private final String host;
    private final String user;
    private final String password;

    public RemoteInvocation(String address) {
        this(address, DEFAULT_WD);
    }

    public RemoteInvocation(String address, String wd) {
        this(address, null ,null, wd);
    }

    public RemoteInvocation(String address, String user, String password) {
        this(address, user, password, DEFAULT_WD);
    }
    public RemoteInvocation(String address, String user, String password, String wd) {
        super(wd);
        this.host = address;
        this.user = user;
        this.password = password;
    }

    public String getHost() {
        return host;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        String command = getCommand();
        if(StringUtils.isBlank(command)){
            command = "<no execution>";
        }
        if( !command.equals("<no execution>") ) return command;
        return String.format("%s@%d\nroot@%s:%s %s\n\n", id, seq, host, wd, command);
    }
}
