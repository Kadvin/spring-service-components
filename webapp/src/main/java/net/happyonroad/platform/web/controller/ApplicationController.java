/**
 * Developer: Kadvin Date: 14-6-26 下午1:57
 */
package net.happyonroad.platform.web.controller;

import net.happyonroad.model.Page;
import net.happyonroad.model.PageRequest;
import net.happyonroad.model.Record;
import net.happyonroad.model.Sort;
import net.happyonroad.platform.web.annotation.BeforeFilter;
import net.happyonroad.platform.web.exception.WebClientSideException;
import net.happyonroad.platform.web.exception.WebServerSideException;
import net.happyonroad.util.MiscUtils;
import net.happyonroad.util.StringUtils;
import org.apache.http.auth.BasicUserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * The Rest Controller
 */
@Scope(WebApplicationContext.SCOPE_REQUEST)
@Transactional(noRollbackFor = HttpMediaTypeNotAcceptableException.class)
public class ApplicationController<T extends Record> {
    // 父类内部使用的headers
    private static HttpHeaders headers = new HttpHeaders();
    // 提供给子类使用的通用的日志
    protected      Logger      logger  = LoggerFactory.getLogger(getClass());

    // 通过 Before Filter 自动创建的page request对象
    protected PageRequest pageRequest;
    protected Page<T>     indexPage;
    protected Principal   currentUser;

    public ApplicationController() {
        logger.trace("A debug point");
    }

    @ExceptionHandler(WebClientSideException.class)
    public ResponseEntity<Object> handleWebClientSideException(WebClientSideException ex, WebRequest request) {
        return handleExceptionInternal(ex, ex.getMessage(), headers, ex.getStatusCode(), request);
    }

    @ExceptionHandler(WebServerSideException.class)
    public ResponseEntity<Object> handleWebServerSideException(WebServerSideException ex, WebRequest request) {
        logger.warn("Caught server side exception {}", MiscUtils.describeException(ex));
        return handleExceptionInternal(ex, ex.getMessage(), headers, ex.getStatusCode(), request);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleWebServerSideException(Throwable ex, WebRequest request) {
        String msg = MiscUtils.describeException(ex);
        logger.error("Caught unhandled exception {}", msg);
        return handleExceptionInternal(ex, msg, headers, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    protected ResponseEntity<Object> handleExceptionInternal(Throwable ex, String body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            request.setAttribute("javax.servlet.error.exception", ex, WebRequest.SCOPE_REQUEST);
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("error", body);
        return new ResponseEntity<Object>(map, headers, status);
    }

    /**
     * <h2>初始化默认的分页请求</h2>
     * <p/>
     * 注意：这个请求仅仅针对 GET类型动作，实现方法名称为index的生效
     *
     * @param page  第几页, Start from 1, not zero, to work with ngTable
     * @param count 分页参数
     *              即便这个值被放到用户profile,或者session里面
     *              那也是前端程序读取到这个值，而后传递过来，而不是这里去读取
     * @param sort  排序参数
     */
    @BeforeFilter(render = Page.class)
    public void initDefaultPageRequest(@RequestParam(required = false, value = "page", defaultValue = "1") int page,
                                       @RequestParam(required = false, value = "count", defaultValue = "40") int count,
                                       @RequestParam(required = false, value = "sort", defaultValue = "") String sort) {
        Sort theSort = parseSort(sort);
        pageRequest = new PageRequest(page - 1, count, theSort);
    }

    @BeforeFilter(order = 0)
    protected Principal initCurrentUser(HttpServletRequest request) {
        Principal principal = null;
        if( request.getUserPrincipal() != null ){
            principal = request.getUserPrincipal();
        }else{
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user"))
                {
                    principal = new BasicUserPrincipal(cookie.getValue());
                }
            }
        }
        currentUser = principal;
        return principal;
    }

    protected String currentUserName() {
        if (currentUser == null) return null;
        return currentUser.getName();
    }


    //AfterFilter现在无法生效
    // 所以，将分页对象输出就采用了特殊的模式
    // 控制器方法返回 Page<Record>，SpringMvcConfig通过 PageRequestResponseBodyMethodProcessor
    // 将page信息分为header + body两个部分输出
    //@AfterFilter(method =  RequestMethod.GET, value = "index")
//    protected void renderPageToHeader(HttpServletResponse response) {
//        if (indexPage == null) return;
//        response.setHeader(Page.TOTAL, String.valueOf(indexPage.getTotalElements()));
//        response.setHeader(Page.PAGES, String.valueOf(indexPage.getTotalPages()));
//        response.setHeader(Page.NUMBER, String.valueOf(indexPage.getNumber()));
//        response.setHeader(Page.REAL, String.valueOf(indexPage.getNumberOfElements()));
//        response.setHeader(Page.SORT, String.valueOf(indexPage.getSort()));
//    }

    // sort = "name desc, age (asc)"
    private Sort parseSort(String sort) {
        if (StringUtils.isBlank(sort)) return null;
        String[] segments = sort.split(",");
        Sort.Order[] orders = new Sort.Order[segments.length];
        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            String[] propAndDir = segment.split("\\s+");
            String prop;
            Sort.Direction dir;
            prop = propAndDir[0];
            if (propAndDir.length == 2) {
                dir = Sort.Direction.fromString(propAndDir[1]);
            } else {
                dir = Sort.Direction.ASC;
            }
            orders[i] = new Sort.Order(dir, prop);
        }
        return new Sort(orders);
    }
}
