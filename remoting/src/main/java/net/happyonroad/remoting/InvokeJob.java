/**
 * Developer: Kadvin Date: 14-2-20 上午9:53
 */
package net.happyonroad.remoting;

import net.happyonroad.cache.ListChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.remoting.support.RemoteInvocationResult;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
* 某次调用
*/
class InvokeJob implements Runnable{
    private static Logger logger = LoggerFactory.getLogger(InvokeJob.class);


    private final InvokerServiceExporter exporter;
    private final byte[]                 message;

    public InvokeJob(InvokerServiceExporter exporter, byte[] message) {
        this.exporter = exporter;
        this.message = message;

    }

    @Override
    public void run() {
        Thread.currentThread().setContextClassLoader(this.exporter.getClassLoader());
        try {
            onMessage(message);
        } catch (Exception e) {
            logger.error("Failed to process received message\n" +
                        "Current class loader is:" + Thread.currentThread().getContextClassLoader(), e);

        }
    }


    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    public void onMessage(byte[] rawRequest) throws ClassNotFoundException, IOException {

       InvocationRequestMessage request = exporter.load(rawRequest);
       InvocationResponseMessage response = new InvocationResponseMessage();
       try {
           RemoteInvocationResult r = exporter.invokeIt(request.asInvocation());
           if (r.hasException()) {
               Throwable cause;
               if( r.getException() instanceof InvocationTargetException){
                   cause = r.getException().getCause();
               }else{
                   cause = r.getException();
               }
               response.setError(cause);
           } else {
               response.setValue(r.getValue());
           }
       } catch (Exception ex) {
           response.setError(ex);
       }
       ListChannel respondQueue = exporter.getChannel(request.getReplyTo());
       //收到调用的消息，现在消息服务的publish接口未定义
       byte[] rawResponse = exporter.dump(response);
       respondQueue.pushLeft(rawResponse);
   }

}
