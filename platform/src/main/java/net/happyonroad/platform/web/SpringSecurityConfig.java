/**
 * Developer: Kadvin Date: 14-7-16 下午9:53
 */
package net.happyonroad.platform.web;

import net.happyonroad.platform.web.support.*;
import net.happyonroad.platform.web.util.ConfigurableRequestMatcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * Work as parent of SpringMvcConfig
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        ConfigurableRequestMatcher customizedCsrfMatcher = new ConfigurableRequestMatcher();
        customizedCsrfMatcher.setExcludedUrls(csrfExcludeUrls());
        http.csrf().requireCsrfProtectionMatcher(customizedCsrfMatcher);
        http.exceptionHandling().accessDeniedHandler(new LoginPageDeniedHandler());
        http.userDetailsService(defaultUserDetailsService());
        http.authenticationProvider(defaultAuthenticationProvider())
                // 配置了 authentication provider 之后， 不需要配置 user details service
                //.userDetailsService(delegateUserDetailsService())
                /* 配置 remember me*/
                .rememberMe()
                .authenticationSuccessHandler(new SavedRequestAwareAuthenticationSuccessHandler())
                .tokenRepository(defaultTokenRepository())
                .tokenValiditySeconds(60 * 60 * 24 * 30)//一个月
                .useSecureCookie(true)
                /* 配置匿名服务 */
                .and().anonymous().authorities("ROLE_ANONYMOUS").principal("ANONYMOUS")
                /* 配置登出服务 */
                .and().logout().invalidateHttpSession(true).logoutSuccessUrl("/login.html")
                .logoutRequestMatcher(new AntPathRequestMatcher("/api/session", "DELETE"))
                .logoutSuccessHandler(new DefaultLogoutSuccessHandler())
                /* 配置会话服务 */
                .and().sessionManagement().enableSessionUrlRewriting(true).sessionFixation().migrateSession();
        // 若以后支持手机客户端访问，那个时候可能就需要基于Digest-Authentication
        //noinspection unchecked
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authenticated =
                (ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry) http.authorizeRequests();
        //谁在前，谁优先级高
        authenticated.antMatchers(HttpMethod.POST, "api/session").anonymous()
                     .antMatchers("/login.html").anonymous() // use spring interceptor
                .antMatchers("/api/**").authenticated()
                .antMatchers("/index.html").not().anonymous()
                .antMatchers("/routes").hasRole("ANONYMOUS")
                .antMatchers("/**").permitAll();
        authenticated.and().formLogin()
                .loginPage("/login.html")
                .loginProcessingUrl("/api/session")
                .successHandler(new DefaultAuthenticationSuccessHandler())
                .failureHandler(new DefaultAuthenticationFailureHandler())
                .and().httpBasic().realmName("ItsNow Platform");
    }

    protected String[] csrfExcludeUrls(){
        return new String[]{"/south", "/north"};
    }

    @Bean
    protected UserDetailsService defaultUserDetailsService() {
        return new DefaultAdminDetailsManager();
    }

    @Bean
    protected AuthenticationProvider defaultAuthenticationProvider() {
        return new DefaultAuthenticationProvider(defaultUserDetailsService());
    }

    @Bean
    protected PersistentTokenRepository defaultTokenRepository() {
        return new InMemoryTokenRepositoryImpl();
    }

}
