/**
 * Developer: Kadvin Date: 14-4-24 下午5:23
 */
package dnt.neo4j;

import org.springframework.stereotype.Component;

/**
 * The neo4j config
 */
@Component("config")
public class Neo4jConfig {
    private String url;
    private String username;
    private String password;

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
