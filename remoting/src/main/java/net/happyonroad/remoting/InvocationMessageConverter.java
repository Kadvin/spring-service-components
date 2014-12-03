/**
 * @author XiongJie, Date: 13-11-16
 */

package net.happyonroad.remoting;

import java.io.IOException;

/** 转换消息 */
public interface InvocationMessageConverter {
    /**
     * 将消息转换为目标数据
     *
     *
     * @param message 消息
     * @return 目标数据
     */
    byte[] dump(InvocationMessage message) throws IOException;

    /**
     * 将原始数据转换为响应消息
     *
     * @param message 原始数据
     * @return 响应消息
     */
    InvocationMessage parse(byte[] message) throws IOException;

}
