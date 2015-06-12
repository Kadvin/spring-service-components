/**
 * xiongjie on 14-8-11.
 */
package net.happyonroad.platform.web.support;

import org.apache.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <h1>登出成功之后，返回 200 code，以及 location在body中</h1>
 */
public class DefaultLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response);
        response.setStatus(HttpStatus.SC_OK);
        response.setHeader("Location", targetUrl);
    }
}
