/**
 * Developer: Kadvin Date: 14-7-14 下午4:05
 */
package net.happyonroad.platform.support;

import net.happyonroad.extension.ExtensionAwareClassLoader;
import net.happyonroad.spring.Bean;
import org.apache.ibatis.io.Resources;
import org.apache.jasper.servlet.JspServlet;
import org.apache.tomcat.InstanceManager;
import org.apache.tomcat.SimpleInstanceManager;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.ServletContainerInitializersStarter;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.plus.annotation.ContainerInitializer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AllowSymLinkAliasChecker;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;

import javax.servlet.ServletContainerInitializer;
import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** The Jetty Server Instance */
public class JettyServer extends Bean {
    private static final String LOG_PATH = "logs/jetty_access.log";

    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private ExtensionAwareClassLoader classLoader;

    @Value("${app.host}")
    private String             host;
    @Value("${http.port}")
    private Integer            port;
    // the jetty server
    private Server             server;

    public void performStart() {
        //don't bind at local ip, unless you specify 127.0.0.1
        if ("localhost".equalsIgnoreCase(host)) {
            host = "0.0.0.0";
        }
        try {
            server = new Server(new InetSocketAddress(host, port));
            WebAppContext context = createWebContext();
            WebAppContext jspContext = createJspContext();
            server.setHandler(createHandlers(context, jspContext));
            server.setStopAtShutdown(true);
            server.start();
            logger.info("Jetty bind at {}:{}", host, port);
        } catch (Exception e) {
            throw new ApplicationContextException("Can't start the jetty server", e);
        }
    }

    public void performStop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new ApplicationContextException("Can't stop the jetty server", e);
        }
    }

    private WebAppContext createWebContext(){
        WebAppContext context = new WebAppContext();
        context.setContextPath("/");
        File webapp = new File(System.getProperty("app.home"), "webapp");
        context.setBaseResource(Resource.newResource(webapp));
        context.setClassLoader(classLoader);
        context.getServletContext().setAttribute("application", applicationContext);
        context.setConfigurations(new Configuration[]{new JettyAnnotationConfiguration()});
        context.addAliasCheck(new AllowSymLinkAliasChecker());
        // Set the global class loader
        Resources.setDefaultClassLoader(classLoader);
        return context;
    }

    private WebAppContext createJspContext() throws Exception {
        // Set JSP to use Standard JavaC always
        System.setProperty("org.apache.jasper.compiler.disablejsr199", "false");
        File jspRoot = getJspRootResourceUri();
        File scratchDir = getScratchDir();
        WebAppContext context = new WebAppContext();
        context.setContextPath("/legacy");
        context.setAttribute("javax.servlet.context.tempdir", scratchDir);
        context.setAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
          ".*/[^/]*servlet-api-[^/]*\\.jar$|.*/javax.servlet.jsp.jstl-.*\\.jar$|.*/.*taglibs.*\\.jar$");
        context.setResourceBase(jspRoot.getAbsolutePath());
        context.setAttribute("org.eclipse.jetty.containerInitializers", jspInitializers());
        context.setAttribute(InstanceManager.class.getName(), new SimpleInstanceManager());
        context.addBean(new ServletContainerInitializersStarter(context), true);
        context.setClassLoader(classLoader);
        context.addAliasCheck(new AllowSymLinkAliasChecker());

        context.addServlet(jspServletHolder(), "*.jsp");
        return context;
    }

    private File getJspRootResourceUri() throws IOException
    {
        File jspRootDir = new File(System.getProperty("app.home"), "webapp/jsp-root");
        if (!jspRootDir.exists() && !jspRootDir.mkdirs()){
            throw new IOException("Unable to create jsp root: " + jspRootDir);
        }
        return jspRootDir;
    }

    /**
     * Establish Scratch directory for the servlet context (used by JSP compilation)
     */
    private File getScratchDir() throws IOException {
        File tempDir = new File(System.getProperty("app.home"), "webapp/temp");

        if (!tempDir.exists() && !tempDir.mkdirs()){
            throw new IOException("Unable to create scratch directory: " + tempDir);
        }
        return tempDir;
    }

    /**
     * Ensure the jsp engine is initialized correctly
     */
    private List<ContainerInitializer> jspInitializers()
    {
        JettyJasperInitializer sci = new JettyJasperInitializer();
        ContainerInitializer initializer = new ContainerInitializer(sci, null);
        List<ContainerInitializer> initializers = new ArrayList<ContainerInitializer>();
        initializers.add(initializer);
        return initializers;
    }

    /**
     * Create JSP Servlet (must be named "jsp")
     */
    private ServletHolder jspServletHolder()
    {
        ServletHolder holderJsp = new ServletHolder("jsp", JspServlet.class);
        holderJsp.setInitOrder(0);
        holderJsp.setInitParameter("logVerbosityLevel", "DEBUG");
        holderJsp.setInitParameter("fork", "false");
        holderJsp.setInitParameter("xpoweredBy", "false");
        holderJsp.setInitParameter("compilerTargetVM", "1.7");
        holderJsp.setInitParameter("compilerSourceVM", "1.7");
        holderJsp.setInitParameter("keepgenerated", "true");
        return holderJsp;
    }

    private HandlerCollection createHandlers(WebAppContext... contexts) {

        List<Handler> handlers = new ArrayList<Handler>();
        Collections.addAll(handlers, contexts);

        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers(handlers.toArray(new Handler[handlers.size()]));

        RequestLogHandler log = new RequestLogHandler();
        log.setRequestLog(createRequestLog());

        HandlerCollection handlerCollection = new HandlerCollection();
        handlerCollection.setHandlers(new Handler[]{handlerList, log});

        return handlerCollection;
    }

    private RequestLog createRequestLog() {
        NCSARequestLog log = new NCSARequestLog();

        File logPath = new File(LOG_PATH);
        //noinspection ResultOfMethodCallIgnored
        logPath.getParentFile().mkdirs();

        log.setFilename(logPath.getPath());
        log.setRetainDays(30);
        log.setExtended(false);
        log.setAppend(true);
        log.setLogTimeZone("GMT");
        log.setLogLatency(true);
        return log;
    }

    static class JettyAnnotationConfiguration extends AnnotationConfiguration {
        @Override
        public void createServletContainerInitializerAnnotationHandlers(WebAppContext context,
                                                                        List<ServletContainerInitializer> scis)
                throws Exception {
            super.createServletContainerInitializerAnnotationHandlers(context, scis);
            //noinspection unchecked
            List<ContainerInitializer> initializers =
                    (List<ContainerInitializer>) context.getAttribute(AnnotationConfiguration.CONTAINER_INITIALIZERS);
            ContainerInitializer initializer = initializers.get(0);
            initializer.addApplicableTypeName(SpringMvcLoader.class.getName());
        }
    }
}