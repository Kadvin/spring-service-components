/**
 * Developer: Kadvin Date: 15/1/22 上午10:09
 */
package net.happyonroad.platform.web.util;

import net.happyonroad.util.StringUtils;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * 可配置的http 请求匹配器
 */
public class ConfigurableRequestMatcher implements RequestMatcher {
    private String[] excludedUrls;
    private Pattern allowedMethodPattern;

    public ConfigurableRequestMatcher() {
        setAllowMethods("GET", "HEAD", "TRACE", "OPTIONS");
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        for (String url : excludedUrls) {
            if ( request.getServletPath().startsWith(url) )
               return false;
        }
        return !allowedMethodPattern.matcher(request.getMethod()).matches();
    }

    public void setAllowMethods(String... allowMethods) {
        allowedMethodPattern = Pattern.compile("^(" + StringUtils.join(allowMethods, "|") + ")$");
    }

    public void setExcludedUrls(String... excludedUrls) {
        this.excludedUrls = excludedUrls;
    }
}
