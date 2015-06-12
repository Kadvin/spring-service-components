/**
 * xiongjie on 14-8-6.
 */
package net.happyonroad.platform.web.support;

import org.apache.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <h1>用于处理认证失败</h1>
 */
public class DefaultAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String error = "Authentication Failed: " + exception.getMessage();
        response.setStatus(HttpStatus.SC_UNAUTHORIZED);
        response.setContentLength(error.getBytes().length);
        response.getWriter().append(error);
    }
}
