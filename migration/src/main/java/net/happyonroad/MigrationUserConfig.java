package net.happyonroad;

import net.happyonroad.service.MigrateService;
import net.happyonroad.spring.config.AbstractUserConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <h1>Migrate User Config</h1>
 *
 * @author Jay Xiong
 */
@Configuration
public class MigrationUserConfig extends AbstractUserConfig{
    @Bean
    MigrateService migrateService(){
        return imports(MigrateService.class);
    }
}
