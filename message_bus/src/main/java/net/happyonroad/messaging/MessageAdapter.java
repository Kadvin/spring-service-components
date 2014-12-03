/**
 * @author XiongJie, Date: 13-11-18
 */
package net.happyonroad.messaging;

/** Abstract Message Listener's Adapter*/
public abstract class MessageAdapter implements MessageListener {
    @Override
    public boolean isBinary() {
        return false;
    }

    @Override
    public void onMessage(String channel, String message) {

    }

    @Override
    public void onMessage(String channel, byte[] message) {

    }
}
