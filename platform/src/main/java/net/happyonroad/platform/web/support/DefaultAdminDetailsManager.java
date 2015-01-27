/**
 * Developer: Kadvin Date: 15/1/22 下午1:34
 */
package net.happyonroad.platform.web.support;

import net.happyonroad.platform.web.model.DefaultUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * A default admin details service implementations
 */
public class DefaultAdminDetailsManager implements UserDetailsService {

    private DefaultUser admin = new DefaultUser("admin","secret");

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.equalsIgnoreCase("admin")) {
            return admin;
        }
        throw new UsernameNotFoundException("Support admin only, your username is: " + username);
    }

}
