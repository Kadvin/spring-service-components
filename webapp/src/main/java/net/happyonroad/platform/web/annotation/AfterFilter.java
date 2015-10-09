/**
 * @author XiongJie, Date: 14-7-30
 */
package net.happyonroad.platform.web.annotation;

import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.annotation.*;

/**
 * <h1>用户标记特定方法需要在请求之后执行</h1>
 *
 * 特别注意：
 *   之后执行的方法，不能向http response里面写入任何东西，因为此时http response 流业已关闭
 */
@Documented
@Target( { ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterFilter {
    /** 过滤器顺序，越小越靠前执行 */
    int order() default 50;

    /**
     * 过滤器适用的http方法，默认为空，说明适用于任何http方法
     *
     * @return 过滤器适用的http方法
     */
    RequestMethod[] method() default {};

    /**
     * 过滤器适用的method名称，默认为空，说明适用于任何methods
     *
     * @return 过滤器适用的method名称, 不需要签名和返回值信息
     */
    String[] value() default {};

    /**
     * <h2>过滤器使用的返回值类型，默认为Object.class，说明<strong>不</strong>适用于任何类型</h2>
     *
     * @return 返回值类型
     */
    Class render() default Object.class;
}
