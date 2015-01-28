/**
 * Developer: Kadvin Date: 15/1/28 下午2:15
 */
package net.happyonroad.platform.web.util;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * <h1>可以将安全认证委托给其他对象的封装类</h1>
 * 如果其他类还没有设置过来，则调用者将会被阻塞
 */
public class DelegateAuthenticationProvider extends BlockingDelegator<AuthenticationProvider>
        implements AuthenticationProvider{

    public AuthenticationProvider getDelegate() {
        return delegate;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        waitDelegateIfNeed();
        return delegate.authenticate(authentication);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        waitDelegateIfNeed();
        return delegate.supports(authentication);
    }
}
