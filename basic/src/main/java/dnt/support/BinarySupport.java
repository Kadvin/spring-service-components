/**
 * @author XiongJie, Date: 13-11-18
 */
package dnt.support;

import dnt.model.Binable;
import dnt.util.CustomizedObjectInputStream;

import java.io.*;

/** toBinary, parseBinary */
public class BinarySupport implements Serializable, Cloneable, Binable {
    private static final long serialVersionUID = -4654269599121002776L;

    public static <T> T parseBinary(byte[] content) {
        return parseBinary(content, null);
    }

        /**
         * Parse binary object stream as target type
         *
         * @param content  binary stream
         * @param theClass target class
         * @param <T>      target type
         * @return target instance
         */
    public static <T> T parseBinary(byte[] content, Class<T> theClass) {
        String className = theClass == null ? "Unknown type" : theClass.getSimpleName();
        try {
            if(content == null )
                throw new IllegalArgumentException("Can't parse binary from null byte array for " + className);
            ByteArrayInputStream bis = new ByteArrayInputStream(content);
            ObjectInput in = null;
            try {
                in = new CustomizedObjectInputStream(bis);
                try {
                    //noinspection unchecked
                    return (T) in.readObject();
                } catch (ClassNotFoundException e) {
                    throw new IOException("Can't find some class: " + e.getMessage(), e);
                }
            } finally {
                bis.close();
                if (in != null) in.close();
            }
        } catch (IOException ex) {
            throw new IllegalArgumentException("Can't parse binary to " + className, ex);
        }
    }

    public byte[] toBinary() {
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;
            try {
                out = new ObjectOutputStream(bos);
                out.writeObject(this);
                return bos.toByteArray();
            } finally {
                bos.close();
                if (out != null) out.close();
            }
        }catch (IOException ex){
            throw  new IllegalStateException("Can't serialize " + this + " as binary stream");
        }
    }
}
