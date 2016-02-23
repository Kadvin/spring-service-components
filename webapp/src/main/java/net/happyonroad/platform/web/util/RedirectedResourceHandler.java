package net.happyonroad.platform.web.util;

import org.springframework.core.io.Resource;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;
import org.springframework.web.servlet.resource.ResourceResolver;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>Redirected Resource Handler</h1>
 *
 * @author Jay Xiong
 */
public class RedirectedResourceHandler extends ResourceHttpRequestHandler {

    private final PathResourceResolver resourceResolver;

    public RedirectedResourceHandler(List<Resource> resources) {
        setLocations(resources);
        resourceResolver = new PathResourceResolver() {
            @Override
            protected boolean checkResource(Resource resource, Resource location) throws IOException {
                //不用检查重定向的路径与实际返回的资源的相对关系
                return resource.exists();
            }

            @Override
            protected Resource getResource(String resourcePath, Resource location) throws IOException {
                // the location is the redirected resource
                if (location.exists() ){
                    return location;
                }else {
                    return null;
                }
            }
        };
        List<ResourceResolver> resolvers = new ArrayList<ResourceResolver>();
        resolvers.add(resourceResolver);
        setResourceResolvers(resolvers);
    }

    @Override
    protected Resource getResource(HttpServletRequest request) throws IOException {
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        Resource resource = resourceResolver.resolveResource(request, path, getLocations(), null);
        if (resource == null || getResourceTransformers().isEmpty()) {
            return resource;
        }
        return resource;
    }
}
