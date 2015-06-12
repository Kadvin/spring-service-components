/**
 * Developer: Kadvin Date: 14-10-16 上午11:34
 */
package net.happyonroad.platform.web.support;

import org.apache.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 当用户登录后再访问login页面时，不直接403，而是重定向
 */
public class LoginPageDeniedHandler extends AccessDeniedHandlerImpl {

    private String[] loginPagePrefixes = new String[]{"/login.html"};

    private String redirectUrl = "/index.html";

    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    private String errorPage;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if (!response.isCommitted()) {
            if (errorPage != null) {
                // Put exception into request scope (perhaps of use to a view)
                request.setAttribute(WebAttributes.ACCESS_DENIED_403, accessDeniedException);

                // Set the 403 status code.
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);

                // forward to error page.
                RequestDispatcher dispatcher = request.getRequestDispatcher(errorPage);
                dispatcher.forward(request, response);
            } else {
                if (isInLoginPaths(this.urlPathHelper.getLookupPathForRequest(request)) && isAuthenticated()) {
                    response.setContentType("text/plain");
                    sendRedirect(request, response);
                } else {
                    response.setCharacterEncoding("UTF-8");
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
                }
            }
        }
    }

    @Override
    public void setErrorPage(String errorPage) {
        super.setErrorPage(errorPage);
        this.errorPage = errorPage;
    }

    private boolean isAuthenticated() {
        Authentication authentication =
                        SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        if (authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        return authentication.isAuthenticated();
    }

    private void sendRedirect(HttpServletRequest request,
                              HttpServletResponse response) {

        String encodedRedirectURL = response.encodeRedirectURL(
                                 request.getContextPath() + this.redirectUrl);
        response.setStatus(HttpStatus.SC_TEMPORARY_REDIRECT);
        response.setHeader("Location", encodedRedirectURL);
    }

    private boolean isInLoginPaths(final String requestUrl) {
        for (String login : this.loginPagePrefixes) {
            if (requestUrl.startsWith(login)) {
                return true;
            }
        }
        return false;
    }
}
