/**
 * Developer: Kadvin Date: 14/12/8 下午7:57
 */
package net.happyonroad;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * The menu app config
 */
@Configuration
@Import({UtilUserConfig.class})
@ComponentScan("net.happyonroad.menu.support")
public class MenuAppConfig {

}
