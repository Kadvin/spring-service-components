/**
 * @author XiongJie, Date: 13-11-12
 */

package dnt.model;

/**
 * 应用地址，与 java.net.SocketAddress意义一致，但定义了接口
 */
public interface SocketAddress extends HostAddress {

    int getPort();
}
