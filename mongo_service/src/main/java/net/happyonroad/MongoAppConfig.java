package net.happyonroad;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import net.happyonroad.spring.config.AbstractAppConfig;
import net.happyonroad.type.TimeInterval;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.CustomConversions;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <h1>Mongo App Configuration</h1>
 *
 * @author Jay Xiong
 */
@Configuration
public class MongoAppConfig extends AbstractAppConfig {

    @Bean
    MongoClientOptions mongoClientOptions() {
        MongoClientOptions.Builder builder = new MongoClientOptions.Builder();
        builder.minConnectionsPerHost(parseInt("mongo.minConnectionsPerHost"));
        builder.connectionsPerHost(parseInt("mongo.connectionsPerHost", 100));
        builder.maxWaitTime(parseTime("mongo.maxWaitTime", "2m"));
        builder.threadsAllowedToBlockForConnectionMultiplier(
                parseInt("mongo.threadsAllowedToBlockForConnectionMultiplier", 5));
        builder.maxConnectionIdleTime(parseTime("mongo.maxConnectionIdleTime", "0s"));
        builder.maxConnectionLifeTime(parseTime("mongo.maxConnectionLifeTime", "0s"));
        builder.connectTimeout(parseTime("mongo.connectTimeout", "10s"));
        builder.socketTimeout(parseTime("mongo.socketTimeout", "0s"));
        builder.socketKeepAlive(parseBoolean("mongo.socketKeepAlive", false));
        builder.cursorFinalizerEnabled(parseBoolean("mongo.cursorFinalizerEnabled", true));
        builder.alwaysUseMBeans(parseBoolean("mongo.alwaysUseMBeans", false));
        builder.heartbeatFrequency(parseTime("mongo.heartbeatFrequency", "5s"));
        builder.minHeartbeatFrequency(parseTime("mongo.minHeartbeatFrequency", "500ms"));
        builder.heartbeatConnectTimeout(parseTime("mongo.heartbeatConnectTimeout", "20s"));
        builder.heartbeatSocketTimeout(parseTime("mongo.heartbeatSocketTimeout", "20s"));
        return builder.build();
    }

    @Bean
    MongoClient mongo() throws Exception {
        String host = System.getProperty("mongo.host", "localhost");
        int port = parseInt("mongo.port", 27017);
        ServerAddress address = new ServerAddress(host, port);
        List<MongoCredential> credentials = new ArrayList<MongoCredential>(1);
        String user = System.getProperty("mongo.user");
        if (user != null) {
            String password = System.getProperty("mongo.password");
            String database = System.getProperty("mongo.database");
            MongoCredential credential = MongoCredential.createCredential(user, database, password.toCharArray());
            credentials.add(credential);
        }
        return new MongoClient(address, credentials, mongoClientOptions());
    }

    @Bean
    MongoTemplate mongoTemplate() throws Exception {
        String database = System.getProperty("mongo.database");
        MongoTemplate template = new MongoTemplate(mongo(), database);
        MappingMongoConverter converter = (MappingMongoConverter) template.getConverter();
        List<Converter> conversions = new LinkedList<Converter>();
        CustomConversions cc = new CustomConversions(conversions);
        converter.setCustomConversions(cc);
        return template;
    }

    @Override
    protected void doExports() {
        super.doExports();
        // Only export mongo template
        exports(MongoTemplate.class);
    }

    int parseTime(String sysProperty, String defaultValue) {
        String value = System.getProperty(sysProperty, defaultValue);
        return TimeInterval.parseInt(value);
    }

    int parseInt(String sysProperty) {
        return parseInt(sysProperty, 0);
    }

    int parseInt(String sysProperty, int defaultValue) {
        String string = System.getProperty(sysProperty);
        if (string == null) return defaultValue;
        return Integer.parseInt(string);
    }

    boolean parseBoolean(String sysProperty, boolean defaultValue) {
        String string = System.getProperty(sysProperty);
        if (string == null) return defaultValue;
        return Boolean.valueOf(string);
    }

}
