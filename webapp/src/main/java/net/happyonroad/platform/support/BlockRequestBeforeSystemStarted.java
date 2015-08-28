package net.happyonroad.platform.support;

import net.happyonroad.event.SystemStartedEvent;
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
 * <h1>当系统尚未启动成功时，Block外部请求</h1>
 *
 * 本对象实际有两个实例，一个是servlet容器根据filter/class构建的，用于接收doFilter方法
 * 另外一个是spring容器根据@Component构建的，用于接收SystemStartedEvent
 * 这两个实例之间，通过静态变量通讯
 *
 * @author Jay Xiong
 */
@Component
public class BlockRequestBeforeSystemStarted extends GenericFilterBean
        implements ApplicationListener<SystemStartedEvent>, PriorityOrdered {
    static boolean started;
    final static Object signal = new Object();

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (started) {
            chain.doFilter(request, response);
            return;
        }
        synchronized (signal) {
            //让当前线程停止/等待
            try {
                signal.wait();
            } catch (InterruptedException e) {
                //结束等待
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    public void onApplicationEvent(SystemStartedEvent event) {
        started = true;
        synchronized (signal) {
            //停止所有线程，结束等待
            signal.notifyAll();
        }
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;// The last one
    }
}
