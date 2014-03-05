/**
 * Developer: Kadvin Date: 14-2-19 下午4:52
 */
package dnt.concurrent;

/**
 * Execute some task batchly
 */
public interface BatchExecutor<T> {


    void callbackWith(BatchCallback<T> callback);

    void submit(T task);

}
