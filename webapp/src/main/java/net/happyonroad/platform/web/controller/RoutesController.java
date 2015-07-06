/**
 * @author XiongJie, Date: 14-7-30
 */
package net.happyonroad.platform.web.controller;

import net.happyonroad.platform.web.annotation.Description;
import net.happyonroad.platform.web.model.RouteItem;
import net.happyonroad.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <h1>用于显示平台中所有已经映射的路由</h1>
 */
@RestController
@RequestMapping("/routes")
public class RoutesController extends ApplicationController{
    @Autowired RequestMappingHandlerMapping mappings;

    /**
     *
     * <h2>返回所有的路由信息</h2>
     *
     * GET /routes
     *
     * @param method  请求方法
     * @param pattern url特征
     * @param detail  是否显示映射的方法，默认不显示
     * @return 请求路由表 格式化好的字符串
     */
    @RequestMapping
    @Description("列出当前服务器所提供的所有API")
    public String list(@RequestParam(value = "method", required = false, defaultValue = "") String method,
                       @RequestParam(value = "pattern", required = false, defaultValue = "") String pattern,
                       @RequestParam(value = "detail", required = false, defaultValue = "false") boolean detail,
                       HttpServletResponse response
                       ) {
        response.setContentType(MediaType.TEXT_PLAIN_VALUE);
        response.setCharacterEncoding("UTF-8");
        Map<RequestMappingInfo, HandlerMethod> methods = mappings.getHandlerMethods();
        List<RouteItem> routeItems = new ArrayList<RouteItem>(methods.size());
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : methods.entrySet()) {
            RequestMappingInfo mapping = entry.getKey();
            HandlerMethod handler = entry.getValue();
            RouteItem item = RouteItem.fromMapping(mapping, handler);
            if( !StringUtils.containsIgnoreCase(item.getUrl(), pattern) ) continue;
            if( !StringUtils.containsIgnoreCase(item.getHttpMethod(), method) ) continue;
            item.showDetail(detail);
            routeItems.add(item);
        }
        Collections.sort(routeItems);
        return StringUtils.join(routeItems, "\r\n\r\n");
    }


}
