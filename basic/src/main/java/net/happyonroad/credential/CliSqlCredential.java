package net.happyonroad.credential;

/**
 * <h1>SQL Over CLI</h1>
 *
 * @author Jay Xiong
 */
public class CliSqlCredential extends CredentialProperties implements CliCredential {
    private static final long serialVersionUID = 8149474930282501659L;

    public CliSqlCredential() {
        setName(CLI_SQL);
        setType(CLI_SQL);
    }

    public String getSubType() {
        return getProperty("subType");
    }

    public void setSubType(String subType) {
        setProperty("subType", subType);
    }

    public String getUser() {
        return getProperty("user");
    }

    public void setUser(String user) {
        setProperty("user", user);
    }

    public String getPassword() {
        return getProperty("password");
    }

    public void setPassword(String password) {
        setProperty("password", password);
    }

    public String getBinPath() {
        return getProperty("binPath");
    }

    public void setBinPath(String binPath) {
        setProperty("binPath", binPath);
    }
}
