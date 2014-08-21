/**
 * Developer: Kadvin Date: 14-2-14 上午11:18
 */
package dnt.redis;

import dnt.util.StringUtils;
import org.apache.commons.pool.BasePoolableObjectFactory;
import redis.clients.jedis.Jedis;

/**
 * A enhanced redis factory copied from JedisFactory, to naming the connection
 */
public class RedisFactory extends BasePoolableObjectFactory {
    // jedis connection index
    static int index = 0;

    private final String host;
    private final int port;
    private final int timeout;
    private final String password;
    private final int database;

    public RedisFactory(final String host, final int port,
                        final int timeout, final String password, final int database) {
        super();
        this.host = host;
        this.port = port;
        this.timeout = timeout;
        this.password = password;
        this.database = database;
    }

    public Object makeObject() throws Exception {
        final Jedis jedis = new Jedis(this.host, this.port, this.timeout);
        jedis.connect();
        if (!StringUtils.isNotBlank(password)) {
            jedis.auth(this.password);
        }
        if( database != 0 ) {
            jedis.select(database);
        }
        try {
            jedis.clientSetname(System.getProperty("app.name") + "#" + index++);
        } catch (Exception e) {
            //the redis version is too lower to set name, we ignore it
            System.err.println("the redis version is too lower to set client name");
        }

        return jedis;
    }

    @Override
    public void activateObject(Object obj) throws Exception {
        if (obj instanceof Jedis) {
            final Jedis jedis = (Jedis)obj;
            if (jedis.getDB() != database) {
                jedis.select(database);
            }
        }
    }

    public void destroyObject(final Object obj) throws Exception {
        if (obj instanceof Jedis) {
            final Jedis jedis = (Jedis) obj;
            if (jedis.isConnected()) {
                try {
                    try {
                        jedis.quit();
                    } catch (Exception e) {
                        //skip
                    }
                    jedis.disconnect();
                } catch (Exception e) {
                    //skip
                }
            }
        }
    }

    public boolean validateObject(final Object obj) {
        if (obj instanceof Jedis) {
            final Jedis jedis = (Jedis) obj;
            try {
                return jedis.isConnected() && jedis.ping().equals("PONG");
            } catch (final Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
