/**
 * @author XiongJie, Date: 14-7-24
 */
package net.happyonroad.platform.web.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

/**
 * <h1>提供给服务模块扩展的适配对象</h1>
 */
public class DelegateUserDetailsService implements UserDetailsService {
    UserDetailsService delegate;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Assert.notNull(delegate);
        return delegate.loadUserByUsername(username);
    }

    public void setDelegate(UserDetailsService delegate) {
        this.delegate = delegate;
    }
}
