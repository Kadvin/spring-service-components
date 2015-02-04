/**
 * Developer: Kadvin Date: 15/1/28 下午2:54
 */
package net.happyonroad.platform.web.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <h1>在实际delegate对象未被设置时，会阻塞调用者的对象</h1>
 */
public class BlockingDelegator<T>  {
    protected T delegate;
    Lock lock = new ReentrantLock(false);

    public BlockingDelegator() {
        lock.lock();
    }

    public void setDelegate(T delegate) {
        if( delegate == this )
            throw new IllegalArgumentException("Can't set delegate as itself");
        this.delegate = delegate;
        if( this.delegate != null ) lock.unlock();
    }

    protected void waitDelegateIfNeed() {
        if( this.delegate != null ) return;
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            //unlocked
        }
    }
}
