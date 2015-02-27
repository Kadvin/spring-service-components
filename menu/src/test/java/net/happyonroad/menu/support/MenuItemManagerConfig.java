package net.happyonroad.menu.support;

import net.happyonroad.menu.config.MenuItemRepositoryConfig;
import net.happyonroad.menu.service.MenuItemService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MenuItemManagerConfig extends MenuItemRepositoryConfig {

    @Bean
    public MenuItemService commonMenuItemService(){
        return new MenuItemManager();
    }

}
