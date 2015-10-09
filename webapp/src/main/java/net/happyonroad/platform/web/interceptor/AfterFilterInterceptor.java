/**
 * @author XiongJie, Date: 14-7-30
 */
package net.happyonroad.platform.web.interceptor;

import net.happyonroad.platform.web.annotation.AfterFilter;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 负责将 <code>@AfterFilter</code> 生效
 *
 * @see net.happyonroad.platform.web.annotation.AfterFilter
 */
public class AfterFilterInterceptor extends AbstractFilterInterceptor{
    public AfterFilterInterceptor(RequestMappingHandlerAdapter adapter) {
        super(adapter);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
        super.postHandle(request, response, handler, modelAndView);
        // 找到handler的class
        Class handlerClass = findControllerClass(handler);
        // 找到handler的方法
        Method handleMethod = findHandleMethod(handler);
        // 找到所有被after filter标记的法
        List<AnnotatedMethod<AfterFilter>> annotatedMethods = findMethods(handlerClass, AfterFilter.class);
        // 根据 after filter 的order进行排序
        Collections.sort(annotatedMethods);
        // 将当前请求情况与所有的标记方法进行比对，过滤掉不生效的过滤器方法
        Iterator<AnnotatedMethod<AfterFilter>> iterator = annotatedMethods.iterator();
        while (iterator.hasNext()) {
            AnnotatedMethod<AfterFilter> annotatedMethod = iterator.next();
            AfterFilter annotation = annotatedMethod.annotation;
            if( !matchesTo(request, handleMethod, annotation.method(), annotation.value(), annotation.render())) {
                iterator.remove();
            }
        }
        // 遍历剩余的过滤器，逐个进行调用
        iterator = annotatedMethods.iterator();
        while (iterator.hasNext()) {
            AnnotatedMethod<AfterFilter> annotatedMethod = iterator.next();
            invokeFilter(request, response, handler, annotatedMethod.method);
        }
        super.postHandle(request, response, handler,modelAndView);
    }
}
