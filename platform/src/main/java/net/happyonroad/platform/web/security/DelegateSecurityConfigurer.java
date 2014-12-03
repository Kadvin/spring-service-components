/**
 * @author XiongJie, Date: 14-7-25
 */
package net.happyonroad.platform.web.security;

import org.springframework.security.authentication.AuthenticationProvider;
//import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

/**
 * 在Spring Security已经配置好的情况下
 * （不破坏/重新配置Spring Security）
 * 将实际的服务实例挂钩过来
 */
public interface DelegateSecurityConfigurer {
    DelegateSecurityConfigurer delegate(AuthenticationProvider delegate);

    DelegateSecurityConfigurer delegate(PersistentTokenRepository delegate);

    DelegateSecurityConfigurer delegate(UserDetailsService delegate);
}
