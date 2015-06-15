package net.happyonroad;

import net.happyonroad.service.MigrateService;
import net.happyonroad.spring.config.AbstractAppConfig;
import net.happyonroad.support.MigrateManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * <h1>Migration App Config</h1>
 *
 * @author Jay Xiong
 */
@Configuration
public class MigrationAppConfig extends AbstractAppConfig{

    @Bean
    MigrateService migrateManager(){
        return new MigrateManager("db/migrate");
    }


    /**
     * 系统安装时，自动migrate数据库，这些数据库是由动态模型解析出来的
     */
    @SuppressWarnings("UnusedDeclaration")
    @PostConstruct
    public void doMigrate() {
        migrateManager().up();
    }

    @Override
    protected void doExports() {
        super.doExports();
        exports(MigrateService.class);
    }
}
