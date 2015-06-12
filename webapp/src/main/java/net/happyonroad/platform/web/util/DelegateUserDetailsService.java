/**
 * Developer: Kadvin Date: 15/1/28 下午2:53
 */
package net.happyonroad.platform.web.util;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * <h1>能够将 User Details Service 任务委托出去</h1>
 */
public class DelegateUserDetailsService extends BlockingDelegator<UserDetailsService> implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        waitDelegateIfNeed();
        return delegate.loadUserByUsername(username);
    }
}
