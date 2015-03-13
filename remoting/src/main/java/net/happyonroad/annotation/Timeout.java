/**
 * Developer: Kadvin Date: 15/3/12 下午8:47
 */
package net.happyonroad.annotation;

import java.lang.annotation.*;

/**
 * <h1>设置某个代理接口调用时的超时时间</h1>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited
public @interface Timeout {
    String value() default "60s";
}
