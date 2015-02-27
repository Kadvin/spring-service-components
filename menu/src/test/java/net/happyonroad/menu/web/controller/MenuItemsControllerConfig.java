package net.happyonroad.menu.web.controller;

import net.happyonroad.menu.service.MenuItemService;
import net.happyonroad.test.config.ApplicationControllerConfig;
import net.happyonroad.menu.web.controller.MenuItemsController;
import org.easymock.EasyMock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MenuItemsControllerConfig extends ApplicationControllerConfig {

    // Mocked service beans

    @Bean
    public MenuItemService commonMenuItemService(){
        return EasyMock.createMock(MenuItemService.class);
    }

    @Bean
    public MenuItemsController commonMenuItemController(){
        return new MenuItemsController();
    }

}
