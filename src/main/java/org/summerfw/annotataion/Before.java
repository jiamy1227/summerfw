package org.summerfw.annotataion;

import java.lang.annotation.*;

/**
 * @author: jiamy
 * @create: 2025/1/9 11:23
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited  // 此注解表示会继承给子类，针对代理增强原始类的情况，第一个以后得postProcessor处理时，拿到都是代理对象，如不加这个注解，从代理类中获取不到原始类是否有这个注解
public @interface Before {

    String value() default "";
}
