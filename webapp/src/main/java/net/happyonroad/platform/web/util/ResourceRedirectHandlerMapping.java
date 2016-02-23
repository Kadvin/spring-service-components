package net.happyonroad.platform.web.util;

import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.handler.AbstractUrlHandlerMapping;

import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Resource Redirect Handler Mapping</h1>
 *
 * @author Jay Xiong
 */
public class ResourceRedirectHandlerMapping extends AbstractUrlHandlerMapping {

    public ResourceRedirectHandlerMapping(ApplicationContext applicationCtx, ServletContext servletCtx) {
        super.setApplicationContext(applicationCtx);
        super.setServletContext(servletCtx);
        setOrder(Integer.MAX_VALUE - 100);//before the default resourceHandleMapping(Integer.MAX_VALUE-1)
    }

    public void tryFiles(String pathPattern, String... candidates) {
        ApplicationContext context = getApplicationContext();
        List<Resource> resources = new ArrayList<Resource>(candidates.length);
        for (String candidate : candidates) {
            Resource resource = context.getResource(candidate);
            resources.add(resource);
        }
        RedirectedResourceHandler handler = new RedirectedResourceHandler(resources);
        handler.setApplicationContext(this.getApplicationContext());
        handler.setServletContext(this.getServletContext());
        registerHandler(pathPattern, handler);
    }
}
