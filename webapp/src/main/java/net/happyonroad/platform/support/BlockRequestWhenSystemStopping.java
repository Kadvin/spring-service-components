package net.happyonroad.platform.support;

import net.happyonroad.event.SystemStoppingEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.PriorityOrdered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * <h1>当系统开始停止时，Block外部请求</h1>
 *
 * 本对象实际有两个实例，一个是servlet容器根据filter/class构建的，用于接收doFilter方法
 * 另外一个是spring容器根据@Component构建的，用于接收SystemStoppingEvent
 * 这两个实例之间，通过静态变量通讯
 *
 * @author Jay Xiong
 */
@Component
public class BlockRequestWhenSystemStopping extends GenericFilterBean
        implements ApplicationListener<SystemStoppingEvent>, PriorityOrdered {
    static boolean stopping;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (stopping)
            throw new ServletException("The server is stopping");
        chain.doFilter(request, response);
    }

    @Override
    public void onApplicationEvent(SystemStoppingEvent event) {
        stopping = true;
    }

    @Override
    public int getOrder() {
        return 0;// The first one
    }
}
