/**
 * Developer: Kadvin Date: 14-1-26 下午3:43
 */
package dnt.messaging.support;

import dnt.messaging.MessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
* 执行消息派发的任务
*/
class DispatchJob implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(DispatchJob.class);
    private final MessageListener listener;
    private final String          channel;
    private       String          string;
    private       byte[]          bytes;

    public DispatchJob(MessageListener listener, String channel, String event) {
        this.listener = listener;
        this.channel = channel;
        this.string = event;
    }

    public DispatchJob(MessageListener listener, String channel, byte[] event) {
        this.listener = listener;
        this.channel = channel;
        this.bytes = event;
    }

    @Override
    public void run() {
        try {
            if(this.string != null){
                listener.onMessage(channel, string);
            }else{
                listener.onMessage(channel, bytes);
            }
        } catch (Throwable e) {
            logger.warn("Skip listener exception at channel " + channel + " for listener " + listener, e);
        }
    }
}
