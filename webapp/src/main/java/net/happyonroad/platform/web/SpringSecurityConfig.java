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
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
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
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(defaultAuthenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        configureBasic(http);
        configureCsrf(http);
        configureExceptionHandling(http);
        configureRememberMe(http);
        configureAnonymous(http);
        configureLogout(http);
        configureSessionManagement(http);
        configureLoginForm(http);
        //noinspection unchecked
        ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authenticated =
                (ExpressionUrlAuthorizationConfigurer.ExpressionInterceptUrlRegistry) http.authorizeRequests();
        configureAuthorizeRequests(authenticated);
    }

    protected void configureBasic(HttpSecurity http) throws Exception {
        //http.httpBasic().realmName(realmName());
    }

    /* 配置CSRF */
    protected void configureCsrf(HttpSecurity http) throws Exception {
        ConfigurableRequestMatcher customizedCsrfMatcher = new ConfigurableRequestMatcher();
        customizedCsrfMatcher.setExcludedUrls(csrfExcludeUrls());
        http.csrf().requireCsrfProtectionMatcher(customizedCsrfMatcher);
    }

    /* 配置Exception Handler */
    protected void configureExceptionHandling(HttpSecurity http) throws Exception {
        http.exceptionHandling().accessDeniedHandler(new LoginPageDeniedHandler());
    }

    /* 配置匿名服务 */
    protected void configureAnonymous(HttpSecurity http) throws Exception {
        http.anonymous().authorities("ROLE_ANONYMOUS").principal("ANONYMOUS");
    }

    /* 配置自动登录服务 */
    protected void configureRememberMe(HttpSecurity http) throws Exception {
        http.rememberMe()
            .authenticationSuccessHandler(new SavedRequestAwareAuthenticationSuccessHandler())
            .tokenRepository(defaultTokenRepository())
                .tokenValiditySeconds(60 * 60 * 24 * 30)//一个月
                .useSecureCookie(true);
    }

    /* 配置登出服务 */
    protected void configureLogout(HttpSecurity http) throws Exception {
        http.logout().invalidateHttpSession(true).logoutSuccessUrl(loginUrl())
            .logoutRequestMatcher(logoutRequestMatcher())
            .logoutSuccessHandler(logoutSuccessHandler());
    }

    /* 配置会话服务 */
    protected void configureSessionManagement(HttpSecurity http) throws Exception {
        http.sessionManagement().enableSessionUrlRewriting(true).sessionFixation().migrateSession();
    }

    /* 配置请求限制 */
    protected void configureAuthorizeRequests(
            ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry authorizeRequests)
            throws Exception {
        // 若以后支持手机客户端访问，那个时候可能就需要基于Digest-Authentication
        //谁在前，谁优先级高
        authorizeRequests.antMatchers(HttpMethod.POST, urlPath("/api/session")).anonymous()
                         .antMatchers(loginUrl()).anonymous() // use spring interceptor
                .antMatchers(urlPath("/api/routes")).permitAll()
                .antMatchers(urlPath("/api/csrf")).permitAll()
                .antMatchers(urlPath("/api/**")).not().anonymous()
                .antMatchers(homeUrl()).not().anonymous()
                .antMatchers("/**").permitAll();
    }

    protected String loginUrl() {
        return "/login.html";
    }

    protected String homeUrl() {
        return "/index.html";
    }

    protected void configureLoginForm(HttpSecurity http) throws Exception {
        FormLoginConfigurer<HttpSecurity> form = http.formLogin();
        form.loginPage(loginUrl())
            .loginProcessingUrl(urlPath("/api/session"))
            .successHandler(successHandler())
            .failureHandler(authenticationFailureHandler());
//        Method method = AbstractAuthenticationFilterConfigurer.class.getDeclaredMethod("getAuthenticationFilter");
//        method.setAccessible(true);
//        AbstractAuthenticationProcessingFilter filter = (AbstractAuthenticationProcessingFilter) method.invoke(form);
//        filter.setContinueChainBeforeSuccessfulAuthentication(true);
    }

    protected String[] csrfExcludeUrls() {
        return new String[]{urlPath("/south"), urlPath("/north")};
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

    protected AuthenticationFailureHandler authenticationFailureHandler() {
        return new DefaultAuthenticationFailureHandler();
    }

    protected AuthenticationSuccessHandler successHandler() {
        return new DefaultAuthenticationSuccessHandler();
    }

    protected LogoutSuccessHandler logoutSuccessHandler() {
        return new DefaultLogoutSuccessHandler();
    }

    protected AntPathRequestMatcher logoutRequestMatcher() {
        return new AntPathRequestMatcher(urlPath("/api/session"), "DELETE");
    }

    protected String urlPath(String origin) {
        return origin;
    }


}
