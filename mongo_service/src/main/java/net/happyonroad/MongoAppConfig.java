package net.happyonroad;

import net.happyonroad.mongo.MongoDbConfig;
import net.happyonroad.spring.config.AbstractAppConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * <h1>Mongo App Configuration</h1>
 *
 * @author Jay Xiong
 */
@Configuration
@Import(MongoDbConfig.class)
public class MongoAppConfig extends AbstractAppConfig {

    @Override
    protected void doExports() {
        super.doExports();
        // Only export mongo template
        exports(MongoTemplate.class);
    }

}
