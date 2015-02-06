/**
 * Developer: Kadvin Date: 14/12/8 下午7:57
 */
package net.happyonroad;

import net.happyonroad.menu.support.MenuItemManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The menu app config
 */
@Configuration
@Import({UtilUserConfig.class})
public class MenuAppConfig {
    @Bean
    MenuItemManager menuItemManager(){
        return new MenuItemManager();
    }

}
