/**
 * @author XiongJie, Date: 13-11-16
 */
package net.happyonroad.remoting;

import net.happyonroad.util.CustomizedObjectInputStream;
import org.apache.commons.io.IOUtils;

import java.io.*;

/** Description */
public class BinaryInvocationMessageConverter
        implements InvocationMessageConverter {
    @Override
    public byte[] dump(InvocationMessage message) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(message);
            return bos.toByteArray();
        }finally {
            IOUtils.closeQuietly(bos);
            if(out != null)out.close();
        }
    }

    @Override
    public InvocationMessage parse(byte[] rawResponse) throws IOException{
        ByteArrayInputStream bis = new ByteArrayInputStream(rawResponse);
        ObjectInput in = null;
        try {
            in = new CustomizedObjectInputStream(bis);
            try {
                //noinspection unchecked
                return (InvocationMessage) in.readObject();
            } catch (ClassNotFoundException e) {
                throw new IOException("Can't find some class: " + e.getMessage(), e);
            }
        } finally {
            bis.close();
            if(in != null) in.close();
        }
    }
}
