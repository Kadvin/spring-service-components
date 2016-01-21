package net.happyonroad.bean;

import net.happyonroad.component.container.AppLauncher;
import net.happyonroad.messaging.MessageBus;
import net.happyonroad.messaging.MessageListener;
import net.happyonroad.spring.Bean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * <h1>执行进程间同步的抽象Bean</h1>
 *
 * @author Jay Xiong
 */
public abstract class Synchronizer extends Bean implements MessageListener {
    @Autowired
    MessageBus messageBus;

    @Override
    protected void performStart() {
        super.performStart();
        messageBus.subscribe(this.toString(), channels(), this);
    }

    @Override
    protected void performStop() {
        super.performStop();
        messageBus.unsubscribe(this.toString());
    }

    @Override
    public boolean isBinary() {
        return false;
    }

    @Override
    public void onMessage(String channel, byte[] message) {
        throw new UnsupportedOperationException("Not support binary broadcast event now");
    }

    @Override
    public String toString() {
        return super.toString() + "@" + AppLauncher.readSystemAppIndex();
    }

    protected abstract String[] channels();
}
