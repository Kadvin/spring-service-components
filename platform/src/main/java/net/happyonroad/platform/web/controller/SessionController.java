/**
 * xiongjie on 14-8-4.
 */
package net.happyonroad.platform.web.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1>将Spring Security的Session功能以控制器URL的方式显性化</h1>
 */

@RestController
@RequestMapping("/api/session")
public class SessionController extends ApplicationController {
    /**
     * <h2>登录</h2>
     * POST /api/session
     */
    @RequestMapping(method = RequestMethod.POST)
    public void login(){
        // Spring Security 已经做了所有的事情，这里暂时不需要做任何事情
    }


    /**
     * <h2>登出</h2>
     * DELETE /api/session
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public void logout(){
        // 不需要做任何事情
    }
}
