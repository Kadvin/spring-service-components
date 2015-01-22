/**
 * Developer: Kadvin Date: 14-6-26 上午10:58
 */
package net.happyonroad.platform.support;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;
import java.io.File;
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

public class SpringMvcLoader extends AbstractAnnotationConfigDispatcherServletInitializer {
    public static final String METHOD_FILTER_NAME = "ItsNow.httpMethodFilter";

    ApplicationContext applicationContext;
    WebApplicationContext webAppContext;

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
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        super.customizeRegistration(registration);
        registration.setAsyncSupported(true);
    }

    @Override
    protected WebApplicationContext createRootApplicationContext() {
        File securityXml = new File(System.getProperty("app.home"), "webapp/security.xml");
        if( securityXml.exists() ){
            XmlWebApplicationContext context = new XmlWebApplicationContext();
            // Load by Servlet Resource
            context.setConfigLocation("security.xml");
            context.setClassLoader(Thread.currentThread().getContextClassLoader());
            webAppContext = context;
            return context;
        }else{
            AnnotationConfigWebApplicationContext acc = (AnnotationConfigWebApplicationContext) super.createRootApplicationContext();
            //设置了class loader之后，就支持了定制化  spring_mvc.configuration
            acc.setClassLoader(Thread.currentThread().getContextClassLoader());
            return webAppContext = acc;
        }
    }

    @Override
    protected void registerContextLoaderListener(ServletContext servletContext) {
        super.registerContextLoaderListener(servletContext);
    }

    protected WebApplicationContext createServletApplicationContext() {
        AnnotationConfigWebApplicationContext servletAppContext =
                (AnnotationConfigWebApplicationContext) super.createServletApplicationContext();
        //设置了class loader之后，就支持了定制化  spring_mvc.configuration
        servletAppContext.setClassLoader(Thread.currentThread().getContextClassLoader());
        if( applicationContext != null){
            servletAppContext.setParent(applicationContext);
            PlatformEventForwarder forwarder = applicationContext.getBean(PlatformEventForwarder.class);
            forwarder.bind(servletAppContext);
        }

        return servletAppContext;
	}

    @Override
    protected Class<?>[] getRootConfigClasses() {
        String securityConfigClassName = System.getProperty("security.configuration",
                "net.happyonroad.platform.web.security.SpringSecurityConfig");
        return getConfigClasses(securityConfigClassName);
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        String securityConfigClassName = System.getProperty("spring_mvc.configuration",
                "net.happyonroad.platform.web.SpringMvcConfig");
        return getConfigClasses(securityConfigClassName);
    }

    private Class<?>[] getConfigClasses(String configClassName) {
        Class<?> securityConfigClass;
        try {
            securityConfigClass = applicationContext.getClassLoader().loadClass(configClassName);
        } catch (ClassNotFoundException e2) {
            try {
                securityConfigClass = Thread.currentThread().getContextClassLoader().loadClass(configClassName);
            } catch (ClassNotFoundException e3) {
                throw new ApplicationContextException("Can't load security configuration class " + configClassName);
            }
        }
        return new Class<?>[]{securityConfigClass};
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
        registration.setAsyncSupported(true);
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
