package net.happyonroad.menu.config;

import net.happyonroad.menu.service.MenuItemService;
import net.happyonroad.menu.support.MenuItemManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MenuItemManagerConfig extends MenuItemRepositoryConfig {

    @Bean
    public MenuItemService commonMenuItemService(){
        return new MenuItemManager();
    }

}
