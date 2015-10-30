/**
 * xiongjie on 14-8-4.
 */
package net.happyonroad.platform.web.controller;

import net.happyonroad.platform.web.annotation.Description;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * <h1>将Spring Security的Session功能以控制器URL的方式显性化</h1>
 */

@RestController
@RequestMapping("/api/session")
public class SessionController extends ApplicationController {
    /**
     * <h2>登录</h2>
     * POST /api/session?username={string}&password={string}
     */
    @RequestMapping(method = RequestMethod.POST)
    @Description("登录，需要传入username,password参数")
    public Principal login(HttpServletRequest request){
        // Spring Security 已经做了所有的事情，这里暂时不需要做任何事情
        return show(request);
    }

    /**
     * <h2>查看当前会话</h2>
     * POST /api/session
     */
    @RequestMapping
    @Description("查看当前会话")
    public Principal show(HttpServletRequest request){
        return initCurrentUser(request);
    }


    /**
     * <h2>登出</h2>
     * DELETE /api/session
     */
    @RequestMapping(method = RequestMethod.DELETE)
    @Description("登出")
    public void logout(){
        this.currentUser = null;
    }

    /**
     * <h2>登出</h2>
     * GET /api/logout
     */
    @RequestMapping("logout")
    @Description("登出")
    public void logout2(){
        // 不需要做任何事情
    }
}
