/**
 * @author XiongJie, Date: 13-11-11
 */
package dnt.cache.support;

import dnt.cache.CacheClient;

import java.io.Serializable;

/** 客户端 */
public class DefaultCacheClient implements CacheClient, Serializable {
    private static final long serialVersionUID = -3622521109456270121L;
    private String identifier;
    private String name;

    public DefaultCacheClient(String clientId) {
        this.identifier = clientId;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + "(" + identifier + ")";
    }
}
