/**
 * @author XiongJie, Date: 13-11-16
 */
package dnt.remoting;

/**
 *
 * 基于JSon文本的消息转换
 * TODO 这个转换机制现在还存在问题
 * */
public class JsonStringInvocationMessageConverter
        implements InvocationMessageConverter{
    @Override
    public byte[] dump(InvocationMessage message) {
        return message.toJson().getBytes();
    }

    @Override
    public InvocationMessage parse(byte[] rawMessage) {
        return InvocationMessage.parse(new String(rawMessage));
    }
}
