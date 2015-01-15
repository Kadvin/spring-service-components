/**
 * Developer: Kadvin Date: 15/1/15 下午8:47
 */
package net.happyonroad.platform.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

/**
 * <h1>处理来自前端的请求</h1>
 */
public class NorthHandler extends TextWebSocketHandler {
    Logger logger = LoggerFactory.getLogger(getClass());

    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        super.afterConnectionEstablished(session);
        logger.info("A web socket connection established:  {}/{}", session.getRemoteAddress(), session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        super.afterConnectionClosed(session, status);
        logger.warn("A web socket connection disconnected: {}/{}", session.getRemoteAddress(), session.getId());
    }

    @Override
    protected void handleTextMessage(final WebSocketSession session, final TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
        logger.info("Got message: {}", message.getPayload());
        //测试程序： 10秒钟之后，会在另外一个线程中把消息一个字符一个字符的发回来
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    for(byte c : message.getPayload().getBytes()){
                        session.sendMessage(new TextMessage(Character.toString((char) c)));
                    }
                } catch (InterruptedException e) {
                    logger.warn("Failed to sleep one second");
                } catch (IOException e) {
                    logger.error("Failed to send message back", e);
                }
            }
        }.start();
    }
}
