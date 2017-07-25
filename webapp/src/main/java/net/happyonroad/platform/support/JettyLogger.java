package net.happyonroad.platform.support;

import org.eclipse.jetty.server.NCSARequestLog;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;

import java.io.IOException;

/**
 * <h1>Extended Jetty Logger for thread name</h1>
 *
 * @author jay on 2017/7/25.
 */
public class JettyLogger extends NCSARequestLog {
    @Override
    protected void logExtended(Request request, Response response, StringBuilder sb) throws IOException {
        sb.append("[").append(Thread.currentThread().getName()).append("] ");
        super.logExtended(request, response, sb);
    }
}
