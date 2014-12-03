/**
 * @author XiongJie, Date: 13-11-16
 */
package net.happyonroad.remoting;

import java.io.Serializable;

/** 调用传输的消息 */
public abstract class InvocationMessage implements Serializable {
    private static final long serialVersionUID = -3737026285419987577L;

    public abstract String toJson();

    public static InvocationMessage parse(String message){
        try{
            return InvocationRequestMessage.parse(message);
        }catch (Exception ex){
            //try another
            return InvocationResponseMessage.parse(message);
        }
    }
}
