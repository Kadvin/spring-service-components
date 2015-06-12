/**
 * @author XiongJie, Date: 14-7-30
 */
package net.happyonroad.platform.web.interceptor;

import org.apache.commons.lang.reflect.MethodUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ModelFactory;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1>抽象的Filter Interceptor</h1>
 *
 * 参考 Ruby on Rails 里面的控制器设计思路
 */
public class AbstractFilterInterceptor extends HandlerInterceptorAdapter {
    RequestMappingHandlerAdapter adapter;

    public AbstractFilterInterceptor(RequestMappingHandlerAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * 调用 before/after filter标记的方法
     *
     * @param request  HTTP请求
     * @param response HTTP 应答
     * @param handler  filter 执行之前/之后 执行的真实映射控制器对象+方法
     * @param method   被 before, after filter 标记的方法
     * @see org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#invokeHandleMethod(
     *javax.servlet.http.HttpServletRequest,
     *      javax.servlet.http.HttpServletResponse,
     *      org.springframework.web.method.HandlerMethod) 参考RequestMappingHandlerAdapter#invokeHandleMethod实现
     */
    protected void invokeFilter(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler, Method method) throws Exception{
        // filter 执行之前/之后 执行的真实映射控制器方法
        HandlerMethod originHandlerMethod = (HandlerMethod) handler;
        // 被标记的方法，封装得和实际方法一样，就是为了让 @RequestBody, @PathVariable, @RequestParam等生效
        HandlerMethod filterMethod = new HandlerMethod(originHandlerMethod.getBean(), method);

        // 以下均 参考/借助RequestMappingHandlerAdapter#invokeHandleMethod的实现
        ServletWebRequest webRequest = new ServletWebRequest(request, response);
        WebDataBinderFactory binderFactory = invokeAdapterMethod("getDataBinderFactory",
                                                                 new Class[]{HandlerMethod.class}, filterMethod);
        Class[] parameterTypes = {HandlerMethod.class, WebDataBinderFactory.class};
        ModelFactory modelFactory = invokeAdapterMethod("getModelFactory", parameterTypes, filterMethod, binderFactory);
        ServletInvocableHandlerMethod requestMappingMethod = invokeAdapterMethod("createRequestMappingMethod",
                                                                                 parameterTypes,
                                                                                 filterMethod, binderFactory);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        mavContainer.addAllAttributes(RequestContextUtils.getInputFlashMap(request));
        modelFactory.initModel(webRequest, mavContainer, requestMappingMethod);
        requestMappingMethod.invokeAndHandle(webRequest, mavContainer);
    }

    protected <T> T invokeAdapterMethod(String methodName, Class[] parameterTypes, Object... args) throws Exception{
        Method method = adapter.getClass().getDeclaredMethod(methodName, parameterTypes );
        method.setAccessible(true);
        //noinspection unchecked
        return (T) method.invoke(adapter, args);
    }

    protected Method findHandleMethod(Object handler) {
        if (handler instanceof HandlerMethod) {
            return ((HandlerMethod) handler).getMethod();
        } else {
            throw new UnsupportedOperationException("Can't find handler method from instance of " + handler.getClass());
        }
    }

    protected boolean matchesTo(HttpServletRequest request, Method handleMethod, RequestMethod[] method, String[] value) {
        if (method.length > 0) {
            String httpMethod = request.getMethod();
            boolean accept = false;
            for (RequestMethod requestMethod : method) {
                if (requestMethod.name().equalsIgnoreCase(httpMethod)) {
                    accept = true;
                    break;
                }
            }
            if (!accept) return false;
        }
        if (value.length > 0) {
            String handleMethodName = handleMethod.getName().toLowerCase();
            boolean accept = false;
            for (String methodName : value) {
                if (handleMethodName.equals(methodName.toLowerCase())) {
                    accept = true;
                    break;
                }
            }
            if (!accept) return false;
        }
        return true;
    }

    protected Class findControllerClass(Object handler) {
        //实际上，在spring-mvc-test的情况下，其将会变成 HandlerMethod 的实例
        if (handler instanceof HandlerMethod) {
            return ((HandlerMethod) handler).getBeanType();
        }
        // 这个情况其实不应该的
        throw new UnsupportedOperationException("Can't find handler method from instance of " + handler.getClass());
    }

    protected <T extends Annotation> List<AnnotatedMethod<T>> findMethods(Class controllerClass, Class<T> annotationClass) {
        Method[] candidates = controllerClass.getMethods();
        List<AnnotatedMethod<T>> founds = new ArrayList<AnnotatedMethod<T>>();
        for (Method candidate : candidates) {
            T annotation = AnnotationUtils.findAnnotation(candidate, annotationClass);
            if (annotation != null) founds.add(new AnnotatedMethod<T>(candidate, annotation));
        }
        return founds;
    }

    protected static class AnnotatedMethod<T extends Annotation> implements Comparable<AnnotatedMethod<T>> {
        protected Method method;
        protected T      annotation;

        AnnotatedMethod(Method method, T annotation) {
            this.method = method;
            this.annotation = annotation;
        }

        @Override
        public int compareTo(@SuppressWarnings("NullableProblems") AnnotatedMethod<T> other) {
            return getOrder(annotation) - getOrder(other.annotation);
        }

        private int getOrder(T annotation) {
            try {
                return (Integer) MethodUtils.invokeMethod(annotation, "order", new Object[0]);
            } catch (Exception ex) {
                return 0;
            }
        }

        @Override
        public String toString() {
            return "@" + annotation.getClass().getSimpleName() + " -> " + method.getName();
        }
    }
}
