/**
 * @author XiongJie, Date: 14-7-30
 */
package net.happyonroad.platform.web.model;

import net.happyonroad.util.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.HashMap;
import java.util.Map;

/** <h1>HTTP 路由项目</h1> */
public class RouteItem implements Comparable<RouteItem> {
    private final Map<String, String> params;
    private final String                    httpMethod;
    private final String                    url;
    private final String                    handler;
    private boolean showDetail = false;

    public RouteItem(String httpMethod, String url, String handler, Map<String, String> requestParams) {
        this.httpMethod = httpMethod;
        this.url = url;
        this.handler = handler;
        this.params = requestParams;
    }

    @Override
    public String toString() {
        return showDetail ?
               String.format("%6s %-50s : %s", httpMethod, urlWithParams(), handler) :
               String.format("%6s %-50s", httpMethod, urlWithParams());
    }

    private String urlWithParams() {
        if (this.params == null || this.params.isEmpty()) return url;
        StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String param = entry.getKey();
            sb.append(param).append("=")
              .append("{").append(entry.getValue())
              .append("}").append("&");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public int compareTo(@SuppressWarnings("NullableProblems") RouteItem other) {
        int urlResult = this.url.compareTo(other.url);
        if (urlResult != 0) return urlResult;
        return this.httpMethod.compareTo(other.httpMethod);
    }

    public void showDetail(boolean detail) {
        this.showDetail = detail;
    }


    public static RouteItem fromMapping(RequestMappingInfo mapping, HandlerMethod handler ){
        String httpMethods = mapping.getMethodsCondition().toString();
        httpMethods = StringUtils.substringBetween(httpMethods, "[", "]");
        if( httpMethods.equals("") ) httpMethods = "GET";
        String url = mapping.getPatternsCondition().toString();
        url = StringUtils.substringBetween(url, "[", "]");
        Map<String,String> requestParams = new HashMap<String,String>();
        for (MethodParameter parameter : handler.getMethodParameters()) {
            RequestParam requestParam = parameter.getParameterAnnotation(RequestParam.class);
            if(requestParam != null)
                requestParams.put(requestParam.value(), parameter.getParameterType().getSimpleName());
        }
        //固定的把 Application Controller 对 index 的 before filter增强加入到路由表达里面
        // 照理来说，应该根据每个handler的method，找到其所有before/after filter，将相关filter的request params加入展示
        // 现在先采用这个权宜之计
        if( httpMethods.contains("GET") && handler.toString().contains("index") ){
            requestParams.put("page", "int");
            requestParams.put("count", "int");
            requestParams.put("sort", "string");
        }
        return new RouteItem(httpMethods, url, handler.toString(), requestParams);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }
}
