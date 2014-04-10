/**
 * @author XiongJie, Date: 13-12-31
 */
package dnt.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;

/**
 * The http adapter for message listener
 *
 * TODO:
 * <ul>
 *     <li>支持Keep-Alive</li>
 *     <li>支持Cache-Control</li>
 *     <li>支持认证等其他高级特性(需要在注册的时候说明)</li>
 * </ul>
 */
public class HttpCallback{
    static Logger logger = LoggerFactory.getLogger(HttpCallback.class);
    private final String endpoint;

    public HttpCallback(String endpoint) {
        if (endpoint.startsWith("http://") || endpoint.startsWith("https://")) {
            this.endpoint = endpoint;
        } else {
            throw new IllegalArgumentException("The endpoint [" + endpoint + "] is not a valid http endpoint, " +
                                               "it should starts with http(s)://");
        }
    }

    public HttpResponse send(Collection<String> events) throws RemoteException {
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContentType("text/plain");
        entity.setChunked(false);
        entity.setContentEncoding("UTF-8");
        StringBuilder event = new StringBuilder();
        for (String payload : events) {
            event.append(payload);
            event.append("\n\n");
        }
        byte[] bytes = event.toString().getBytes();
        entity.setContentLength(bytes.length);
        entity.setContent(new ByteArrayInputStream(bytes));
        HttpPost post = new HttpPost(this.endpoint);
        post.setEntity(entity);
        post.setEntity(entity);
        HttpResponse response = sendRequest(post);
        logger.info("Post string event to {} and got: {}", post.getURI(),
                    StringUtils.abbreviate(response.toString(), 50));
        return response;
    }


    private HttpResponse sendRequest(HttpPost post) throws RemoteException {
        CloseableHttpClient client = HttpClients.createDefault();
        try {
            CloseableHttpResponse response = client.execute(post);
            response.close();
            return response;
        } catch (IOException e) {
            throw new RemoteException("Can't post event to remote endpoint " + this.endpoint, e);
        } finally {
            try {
                client.close();
            } catch (IOException e) {
                logger.warn("Can't close http client", e);
            }
        }
    }
}
