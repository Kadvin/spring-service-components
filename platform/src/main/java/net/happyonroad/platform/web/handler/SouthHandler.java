/**
 * Developer: Kadvin Date: 15/1/15 下午8:47
 */
package net.happyonroad.platform.web.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;

/**
 * <h1>处理来自下端(例如：监控引擎)的请求</h1>
 *
 * 这些代码与Sprint Security的代码逻辑，都是应用层代码，暂时放在平台层，以后要迁出的
 */
public class SouthHandler extends BinaryWebSocketHandler {
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
    protected void handleBinaryMessage(final WebSocketSession session, final BinaryMessage message) throws Exception {
        super.handleBinaryMessage(session, message);
        logger.info("Got message: {}", message);
        //测试程序： 10秒钟之后，会在另外一个线程中把消息一个byte一个byte的发回来
        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(10000);
                    for(byte c : message.getPayload().array()){
                        session.sendMessage(new BinaryMessage(new byte[]{c}));
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
