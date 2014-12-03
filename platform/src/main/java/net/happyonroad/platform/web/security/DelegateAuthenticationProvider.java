/**
 * @author XiongJie, Date: 14-7-24
 */
package net.happyonroad.platform.web.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

/**
 * <h1>提供给服务模块扩展的适配对象</h1>
 */
public class DelegateAuthenticationProvider implements AuthenticationProvider {
    //由服务模块扩展注册过来的实际的Authentication Provider
    AuthenticationProvider delegate;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.notNull(delegate);
        return delegate.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        Assert.notNull(delegate);
        return delegate.supports(authentication);
    }

    public void setDelegate(AuthenticationProvider delegate) {
        Assert.notNull(delegate);
        this.delegate = delegate;
    }
}
