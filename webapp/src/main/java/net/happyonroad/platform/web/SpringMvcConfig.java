/**
 * Developer: Kadvin Date: 14-6-26 上午11:04
 */
package net.happyonroad.platform.web;

import net.happyonroad.platform.web.interceptor.AfterFilterInterceptor;
import net.happyonroad.platform.web.interceptor.BeforeFilterInterceptor;
import net.happyonroad.platform.web.support.ExtendedRequestMappingHandlerMapping;
import net.happyonroad.platform.web.support.PageRequestResponseBodyMethodProcessor;
import org.apache.commons.lang.reflect.FieldUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import java.util.ArrayList;
import java.util.List;

/**
 * 代替一般WebApp中的web-context-xml 以Annotation方式定义Spring MVC
 */
@Configuration
//@EnableWebMvc
@EnableTransactionManagement
/*这句只能保证这里的web context扫描出来的对象有transaction*/
@ComponentScan("net.happyonroad.platform.web.controller")
public class SpringMvcConfig extends WebMvcConfigurationSupport
        implements InitializingBean {
    public static final Log logger = LogFactory.getLog(SpringMvcConfig.class);

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        super.configureContentNegotiation(configurer);
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }

    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        PathMatchConfigurer configurer = new PathMatchConfigurer();
        configurePathMatch(configurer);
        ExtendedRequestMappingHandlerMapping handlerMapping = new ExtendedRequestMappingHandlerMapping();
        handlerMapping.setOrder(0);
        handlerMapping.setInterceptors(getInterceptors());
        handlerMapping.setContentNegotiationManager(mvcContentNegotiationManager());
        if (configurer.isUseSuffixPatternMatch() != null) {
            handlerMapping.setUseSuffixPatternMatch(configurer.isUseSuffixPatternMatch());
        }
        if (configurer.isUseRegisteredSuffixPatternMatch() != null) {
            handlerMapping.setUseRegisteredSuffixPatternMatch(configurer.isUseRegisteredSuffixPatternMatch());
        }
        if (configurer.isUseTrailingSlashMatch() != null) {
            handlerMapping.setUseTrailingSlashMatch(configurer.isUseTrailingSlashMatch());
        }
        if (configurer.getPathMatcher() != null) {
            handlerMapping.setPathMatcher(configurer.getPathMatcher());
        }
        if (configurer.getUrlPathHelper() != null) {
            handlerMapping.setUrlPathHelper(configurer.getUrlPathHelper());
        }
        logger.debug("Customize Spring MVC with ExtendedRequestMappingHandlerMapping");
        return handlerMapping;
    }

    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("utf-8");
        resolver.setMaxUploadSize(1048576L); //TODO tobe configurable
        resolver.setMaxInMemorySize(10240);
        return resolver;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RequestMappingHandlerAdapter adapter = this.requestMappingHandlerAdapter();
        //hacking the adapter returnValueHandlers
        // replace RequestResponseBodyMethodProcessor with Delayed RequestResponseBodyMethodProcessor
        // property path: adapter#returnValueHandlers#returnValueHandlers
        Object composite = FieldUtils.readField(adapter, "returnValueHandlers", true);
        //noinspection unchecked
        List<HandlerMethodReturnValueHandler> actual =
                (List<HandlerMethodReturnValueHandler>) FieldUtils.readField(composite, "returnValueHandlers", true);
        int index = -1;
        for (int i = 0; i < actual.size(); i++) {
            Object o = actual.get(i);
            if (o instanceof RequestResponseBodyMethodProcessor) {
                index = i;
                break;
            }
        }
        if (index == -1)
            throw new IllegalStateException(
                    "Can't find any RequestResponseBodyMethodProcessor in requestMappingHandlerAdapter#handlers");
        List<HttpMessageConverter<?>> converters = getMessageConverters();
        PageRequestResponseBodyMethodProcessor pageRenderer =
                new PageRequestResponseBodyMethodProcessor(converters, mvcContentNegotiationManager());
        actual.add(index, pageRenderer);
        logger.debug("Insert Page RequestResponseBodyMethodProcessor before origin");
        AbstractJackson2HttpMessageConverter jacksonConverter = null;
        for (HttpMessageConverter<?> converter : converters) {
            if (converter instanceof AbstractJackson2HttpMessageConverter) {
                jacksonConverter = (AbstractJackson2HttpMessageConverter) converter;//只支持对第一个这种converter进行renew
                break;
            }
        }
        if (jacksonConverter != null) {
            // jackson convert will cause memory leak by its fixed ObjectMapper, we need renew it periodically
            new Thread(new JacksonObjectMapperRenew(jacksonConverter), "Jackson-Renew").start();
        }
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        super.addInterceptors(registry);
        RequestMappingHandlerAdapter adapter = this.requestMappingHandlerAdapter();
        registry.addInterceptor(new BeforeFilterInterceptor(adapter));
        registry.addInterceptor(new AfterFilterInterceptor(adapter));
        logger.debug("Add Before/After Filter Interceptors");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //前端的静态资源映射
        ResourceHandlerRegistration registration = registry.addResourceHandler("/**");
        List<String> locations = staticResourceLocations();
        registration.addResourceLocations(locations.toArray(new String[locations.size()]));
        int oneYear = (int) (DateUtils.MILLIS_PER_DAY / 1000 * 365);
        registration.setCachePeriod(oneYear);
        //favicon映射
        registration = registry.addResourceHandler("/favicon.ico");
        registration.addResourceLocations("/build/assets/favicon.ico",
                                          "/deploy/images/favicon.ico",
                                          "/public/images/favicon.ico");
        registration.setCachePeriod(oneYear);
    }

    protected List<String> staticResourceLocations() {
        List<String> locations = new ArrayList<String>();
        locations.add("/build/");
        locations.add("/deploy/");
        locations.add("/public/");
        return locations;
    }

    static class JacksonObjectMapperRenew implements Runnable {
        private final AbstractJackson2HttpMessageConverter jacksonConverter;

        public JacksonObjectMapperRenew(AbstractJackson2HttpMessageConverter jacksonConverter) {
            this.jacksonConverter = jacksonConverter;
        }

        @Override
        public void run() {
            //noinspection InfiniteLoopStatement
            while (true) {
                try {
                    Thread.sleep(DateUtils.MILLIS_PER_MINUTE);
                } catch (InterruptedException e) {
                    //skip any sleep exception
                }
                jacksonConverter.setObjectMapper(Jackson2ObjectMapperBuilder.json().build());
            }
        }
    }
}
