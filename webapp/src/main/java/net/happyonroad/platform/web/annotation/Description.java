package net.happyonroad.platform.web.annotation;

import java.lang.annotation.*;

/**
 * <h1>API Description</h1>
 *
 * @author Jay Xiong
 */
@Documented
@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
    String value() ;
}
