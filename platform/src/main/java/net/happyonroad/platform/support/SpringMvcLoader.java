/**
 * Developer: Kadvin Date: 14-6-26 上午10:58
 */
package net.happyonroad.platform.support;

import net.happyonroad.platform.web.SpringMvcConfig;
import net.happyonroad.platform.web.security.DelegateSecurityConfigurer;
import net.happyonroad.platform.web.security.SpringSecurityConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;
import java.util.EnumSet;
import java.util.Set;

/**
 * <h1>代替WEB-INF/web.xml以程序方式初始化WEB APP的对象</h1>
 *
 * <ul>
 * <li> /*        ->  springSecurityFilterChain
 * <li> /*        ->  ItsNow.httpMethodFilter
 * <li> /*        ->  ItsNow.Dispatcher
 * <li> /views/*  ->  ItsNow.ScalateView
 * </ul>
 */

public class SpringMvcLoader extends AbstractAnnotationConfigDispatcherServletInitializer
        implements ServletContextListener{
    public static final String METHOD_FILTER_NAME = "ItsNow.httpMethodFilter";

    ApplicationContext applicationContext;
    private AnnotationConfigWebApplicationContext webAppContext;

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // 本函数是在 web container start the servlet context 的 lifecycle里面执行的
        // 但 root web context 的构建却是在 context listener 在listen到 context initialized 事件之后
        applicationContext = (ApplicationContext) servletContext.getAttribute("application");

        super.onStartup(servletContext);
        registerSecurityFilter(servletContext);
        registerHttpMethodFilter(servletContext);
    }

    @Override
    protected WebApplicationContext createRootApplicationContext() {
        return webAppContext = (AnnotationConfigWebApplicationContext) super.createRootApplicationContext();
    }

    @Override
    protected void registerContextLoaderListener(ServletContext servletContext) {
        super.registerContextLoaderListener(servletContext);
        //这里也监听 Servlet Context 事件
        servletContext.addListener(this);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //将Root　Web Context 里面的 DelegateSecurityConfigurer 注册到 对外的applicationContext里面
        // 以便将其以服务的形式暴露出来
        DelegateSecurityConfigurer configurer = webAppContext.getBean(DelegateSecurityConfigurer.class);
        ConfigurableApplicationContext cac = (ConfigurableApplicationContext) applicationContext;
        cac.getBeanFactory().registerSingleton("publicDelegateSecurityConfigurer", configurer);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // need do nothing
    }

    protected WebApplicationContext createServletApplicationContext() {
        AnnotationConfigWebApplicationContext servletAppContext =
                (AnnotationConfigWebApplicationContext) super.createServletApplicationContext();
        if( applicationContext != null ){
            servletAppContext.setParent(applicationContext);
            ServicePackageEventForwarder forwarder = applicationContext.getBean(ServicePackageEventForwarder.class);
            forwarder.bind(servletAppContext);
        }

        return servletAppContext;
	}

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{SpringSecurityConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{SpringMvcConfig.class};
    }

    @Override
    protected String getServletName() {
        return "ItsNow.Dispatcher";
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    private void registerHttpMethodFilter(ServletContext servletContext) {
        FilterRegistration.Dynamic registration = servletContext.addFilter(METHOD_FILTER_NAME, HiddenHttpMethodFilter.class);
        registration.addMappingForServletNames(getDispatcherTypes(), false, getServletName());
    }

    EnumSet<DispatcherType> getDispatcherTypes() {
   		return isAsyncSupported() ?
   			EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE, DispatcherType.ASYNC) :
   			EnumSet.of(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.INCLUDE);
   	}

    ////////////////////////////////////////////////////////////////////////////////////
    // Copied from AbstractSecurityWebApplicationInitializer
    ////////////////////////////////////////////////////////////////////////////////////

    protected void registerSecurityFilter(ServletContext servletContext) {
        //Below copied from AbstractSecurityWebApplicationInitializer
        if(enableHttpSessionEventPublisher()) {
            servletContext.addListener("org.springframework.security.web.session.HttpSessionEventPublisher");
        }
        servletContext.setSessionTrackingModes(getSessionTrackingModes());
        insertSpringSecurityFilterChain(servletContext);
    }

    protected boolean enableHttpSessionEventPublisher() {
        return false;
    }

    protected Set<SessionTrackingMode> getSessionTrackingModes() {
        return EnumSet.of(SessionTrackingMode.COOKIE);
    }

    /**
     * Registers the springSecurityFilterChain
     * @param servletContext the {@link ServletContext}
     */
    private void insertSpringSecurityFilterChain(ServletContext servletContext) {
        String filterName = "springSecurityFilterChain";
        DelegatingFilterProxy springSecurityFilterChain = new DelegatingFilterProxy(filterName);
        registerFilter(servletContext, true, filterName, springSecurityFilterChain);
    }

    /**
     * Registers the provided filter using the {@link #isAsyncSupported()} and {@link #getSecurityDispatcherTypes()}.
     *
     * @param servletContext the {@link ServletContext}
     * @param prepend should this Filter be inserted before or after other {@link Filter}
     * @param filterName the filter name
     * @param filter the filter
     */
    private void registerFilter(ServletContext servletContext, boolean prepend, String filterName, Filter filter) {
        FilterRegistration.Dynamic registration = servletContext.addFilter(filterName, filter);
        if(registration == null) {
            throw new IllegalStateException("Duplicate Filter registration for '" + filterName
                    + "'. Check to ensure the Filter is only configured once.");
        }
        registration.setAsyncSupported(isAsyncSecuritySupported());
        EnumSet<DispatcherType> dispatcherTypes = getSecurityDispatcherTypes();
        registration.addMappingForUrlPatterns(dispatcherTypes, !prepend, "/*");
    }

    /**
     * Get the {@link DispatcherType} for the springSecurityFilterChain.
     * @return  dispatcher types
     */
    protected EnumSet<DispatcherType> getSecurityDispatcherTypes() {
        return EnumSet.of(DispatcherType.REQUEST, DispatcherType.ERROR, DispatcherType.ASYNC);
    }

    /**
     * Determine if the springSecurityFilterChain should be marked as supporting
     * asynchronous. Default is true.
     *
     * @return true if springSecurityFilterChain should be marked as supporting asynchronous
     */
    protected boolean isAsyncSecuritySupported() {
        return true;
    }



}
