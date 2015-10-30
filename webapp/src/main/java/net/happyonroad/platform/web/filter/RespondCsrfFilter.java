package net.happyonroad.platform.web.filter;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * <h1>当用户的请求是POST,DELETE,PUT等方法时，将csrf token放到respond里面，以免前端再次发起新的csrf token的查询</h1>
 *
 * @author Jay Xiong
 */
public class RespondCsrfFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String method = request.getMethod();
        if("POST".equals(method) || "DELETE".equals(method) || "PUT".equals(method)) {
            String attr = HttpSessionCsrfTokenRepository.class.getName().concat(".CSRF_TOKEN");
            CsrfToken csrf = (CsrfToken) request.getSession(false).getAttribute(attr);
            if( csrf != null ) {
                response.setHeader(csrf.getHeaderName(), csrf.getToken());
            }
        }
        filterChain.doFilter(request, response);
    }
}
