/**
 * Developer: Kadvin Date: 14-7-18 上午10:11
 */
package net.happyonroad.platform.web.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>返回安全全局信息</h1>
 */
@RestController
@RequestMapping("/security")
public class SecurityController extends ApplicationController {

    /**
     *
     * <h2>返回CSRF信息</h2>
     *
     * GET /security/csrf
     *
     * @param request  请求
     * @return CSRF的header/parameter name, token
     */
    @RequestMapping("csrf")
    public Map<String,String> csrf(HttpServletRequest request){
        String attr = CsrfToken.class.getName();
        CsrfToken token = (CsrfToken) request.getAttribute(attr);
        Map<String, String> csrfMap = new HashMap<String, String>();
        csrfMap.put("headerName", token.getHeaderName());
        csrfMap.put("parameterName", token.getParameterName());
        csrfMap.put("token", token.getToken());
        return csrfMap;
    }
}
