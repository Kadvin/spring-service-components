/**
 * Developer: Kadvin Date: 14-7-11 下午1:22
 */
package net.happyonroad.platform.web.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

/**
 * WEB层异常，异常源自客户端
 */
public class WebClientSideException extends HttpClientErrorException{

    private int code;

    public WebClientSideException(HttpStatus status, String message) {
        super(status, message);
    }

    public WebClientSideException(HttpStatus statusCode, String statusText, int code) {
        super(statusCode, statusText);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
