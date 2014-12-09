/**
 * Developer: Kadvin Date: 14-2-19 下午4:54
 */
package net.happyonroad.concurrent;

import java.util.Collection;

/**
 * A batch callback used by batch executor
 */
public interface BatchCallback<T> {
    void batchPerform(Collection<T> batches);
}
