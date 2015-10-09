/**
 * @author XiongJie, Date: 14-7-30
 */
package net.happyonroad.platform.web.interceptor;

import net.happyonroad.platform.web.annotation.BeforeFilter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 负责将 <code>@BeforeFilter</code> 生效
 *
 * @see net.happyonroad.platform.web.annotation.BeforeFilter
 */
public class BeforeFilterInterceptor extends AbstractFilterInterceptor{
    public BeforeFilterInterceptor(RequestMappingHandlerAdapter adapter) {
        super(adapter);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 找到handler的class
        Class handlerClass = findControllerClass(handler);
        // 找到handler的方法
        Method handleMethod = findHandleMethod(handler);
        // 找到所有被before filter标记的法
        List<AnnotatedMethod<BeforeFilter>> annotatedMethods = findMethods(handlerClass, BeforeFilter.class);
        // 根据 before filter 的order进行排序
        Collections.sort(annotatedMethods);
        // 将当前请求情况与所有的标记方法进行比对，过滤掉不生效的过滤器方法
        Iterator<AnnotatedMethod<BeforeFilter>> iterator = annotatedMethods.iterator();
        while (iterator.hasNext()) {
            AnnotatedMethod<BeforeFilter> annotatedMethod = iterator.next();
            BeforeFilter annotation = annotatedMethod.annotation;
            if( !matchesTo(request, handleMethod, annotation.method(), annotation.value(), annotation.render())) {
                iterator.remove();
            }
        }
        // 遍历剩余的过滤器，逐个进行调用
        iterator = annotatedMethods.iterator();
        while (iterator.hasNext()) {
            AnnotatedMethod<BeforeFilter> annotatedMethod = iterator.next();
            // 一个失败，全部失败
            // 这里没有用 preHandle这种boolean返回值机制
            invokeFilter(request, response, handler, annotatedMethod.method);
        }
        return super.preHandle(request, response, handler);

    }

}
