package net.happyonroad;

import net.happyonroad.spring.config.AbstractUserConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * <h1>Mongo User Configuration</h1>
 *
 * @author Jay Xiong
 */
@Configuration
public class MongoUserConfig extends AbstractUserConfig {

    @Bean
    MongoTemplate mongoTemplate(){
        return imports(MongoTemplate.class);
    }
}
