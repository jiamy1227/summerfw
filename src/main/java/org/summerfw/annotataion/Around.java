package org.summerfw.annotataion;

import java.lang.annotation.*;

/**
 * @author: jiamy
 * @create: 2025/1/9 11:22
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Around {

    String value() default "";
}
