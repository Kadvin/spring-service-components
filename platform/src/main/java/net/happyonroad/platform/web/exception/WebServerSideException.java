/**
 * Developer: Kadvin Date: 14-7-11 下午1:22
 */
package net.happyonroad.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

/**
 * WEB层异常，异常源自服务端
 */
public class WebServerSideException extends HttpServerErrorException {
    private int code;

    public WebServerSideException(HttpStatus status, String message) {
        super(status, message);
    }

    public WebServerSideException(HttpStatus statusCode, String statusText, int code) {
        super(statusCode, statusText);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
