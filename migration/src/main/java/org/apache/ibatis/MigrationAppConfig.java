package org.apache.ibatis;

import net.happyonroad.spring.config.AbstractAppConfig;
import org.apache.ibatis.migration.CommandLine;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * <h1>Migration App Config</h1>
 *
 * @author Jay Xiong
 */
@Configuration
public class MigrationAppConfig extends AbstractAppConfig{

    @SuppressWarnings("UnusedDeclaration")
    @PostConstruct
    public void doMigrate() {
        new CommandLine(new String[]{"up", "--path=db/migrate"}).execute();
    }
}
