/**
 * Developer: Kadvin Date: 14-1-26 下午3:44
 */
package dnt.concurrent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * 在特定时间内执行，检查某个任务是否已经完成，如果没完成，则取消之
 */
public class ZombieKiller implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(ZombieKiller.class);

    private final Future<?>       future;
    private final String          target;
    private       boolean         cancelled;

    public ZombieKiller(Future<?> future, String target) {
        this.future = future;
        this.target = target;
        this.cancelled = false;
    }

    @Override
    public void run() {
        if (future.isDone() || future.isCancelled()) return;
        cancelled = future.cancel(true);
        if (cancelled) {
            logger.info("Zombie Killer found {} is timeout and cancel it", target);
        } else {
            logger.warn("The {} is timeout, but can't be cancelled", target);
        }
    }

    protected boolean isCancelled(){
        return cancelled;
    }
}
