/**
 * Developer: Kadvin Date: 15/1/22 下午1:34
 */
package net.happyonroad.platform.web.support;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;

/**
 * A default admin details service implementations
 */
public class DefaultAdminDetailsManager implements UserDetailsService {

    private User admin = new User("admin",
            DefaultAuthenticationProvider.passwordEncoder.encode("secret"),
            Collections.EMPTY_LIST);

    @Override

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (username.equalsIgnoreCase("admin")) {
            return admin;
        }
        throw new UsernameNotFoundException("Support admin only, your username is: " + username);
    }

}
