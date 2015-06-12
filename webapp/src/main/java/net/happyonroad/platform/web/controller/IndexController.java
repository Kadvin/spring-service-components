/**
 * Developer: Kadvin Date: 14-10-21 下午3:16
 */
package net.happyonroad.platform.web.controller;

import org.apache.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * The default controller handle /, /index.*
 */
@Controller
@RequestMapping("/")
public class IndexController {

    @RequestMapping
    public void empty(HttpServletResponse response){
        sendRedirect(response);
    }

    private void sendRedirect(HttpServletResponse response) {
        response.setContentType("text/plain");
        String encodedRedirectURL = response.encodeRedirectURL("/index.html");
        response.setStatus(HttpStatus.SC_TEMPORARY_REDIRECT);
        response.setHeader("Location", encodedRedirectURL);
    }

}
