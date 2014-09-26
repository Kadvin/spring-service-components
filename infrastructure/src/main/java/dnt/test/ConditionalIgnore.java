/**
 * Developer: Kadvin Date: 14-9-25 下午12:14
 */
package dnt.test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* Description here
*/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface ConditionalIgnore {
  Class<? extends IgnoreCondition> condition();
}
