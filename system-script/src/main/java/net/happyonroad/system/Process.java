/**
 * Developer: Kadvin Date: 14-9-20 上午11:23
 */
package net.happyonroad.system;

import net.happyonroad.exception.SystemInvokeException;

import java.util.List;
import java.util.concurrent.Future;

/**
 * The system shell
 */
public interface Process {

    /**
     * 运行一段程序，并等待其结束
     *
     * @param command 命令
     * @param args    参数
     * @return 退出码
     * @throws SystemInvokeException
     */
    int run(String command, Object... args) throws SystemInvokeException;

    /**
     * 在后台运行一段程序，返回future
     *
     * @param command 命令
     * @param args    参数
     * @return future
     * @throws SystemInvokeException
     */
    Future background(String command, Object... args) throws SystemInvokeException;

    /**
     * 获取命令信息
     *
     * @return 命令信息
     */
    List<String> getCommand();

    /**
     * 这个命令执行的输出
     *
     * @return 命令输出
     */
    String getContent();
}
