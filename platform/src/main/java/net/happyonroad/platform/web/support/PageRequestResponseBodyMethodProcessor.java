/**
 * Developer: Kadvin Date: 14-10-10 下午4:21
 */
package net.happyonroad.platform.web.support;

import net.happyonroad.platform.service.Page;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <h1>处理Page&lt;Record&gt;的输出</h1>
 *
 * 处理方式就是将分页信息通过http头输出，实际数据通过http body输出
 */
public class PageRequestResponseBodyMethodProcessor extends RequestResponseBodyMethodProcessor {

    public PageRequestResponseBodyMethodProcessor(
            List<HttpMessageConverter<?>> messageConverters,
            ContentNegotiationManager contentNegotiationManager) {
        super(messageConverters, contentNegotiationManager);
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return Page.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest) throws IOException, HttpMediaTypeNotAcceptableException {
        Page page = (Page)returnValue;
        ServletWebRequest request = (ServletWebRequest) webRequest;
        HttpServletResponse response = request.getResponse();
        response.setHeader(Page.TOTAL, String.valueOf(page.getTotalElements()));
        response.setHeader(Page.PAGES, String.valueOf(page.getTotalPages()));
        response.setHeader(Page.NUMBER, String.valueOf(page.getNumber() + 1));
        response.setHeader(Page.REAL, String.valueOf(page.getNumberOfElements()));
        response.setHeader(Page.SORT, String.valueOf(page.getSort()));
        response.setHeader(Page.COUNT, String.valueOf(page.getSize()));
        super.handleReturnValue(page.getContent(), returnType, mavContainer, webRequest);
    }
}
