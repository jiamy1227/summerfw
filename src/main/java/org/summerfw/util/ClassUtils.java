package org.summerfw.util;

import org.summerfw.annotataion.Bean;
import org.summerfw.annotataion.Order;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * 反射工具类
 *
 * @author: jiamy
 * @create: 2025/1/3 10:55
 **/
public class ClassUtils {

    /**
     * 查找class是否有特定注解，包括元注解中
     * @param clazz
     * @param componentClass
     * @return
     */
    public static <A extends Annotation> A getAnnotation(Class<?> clazz, Class<A> componentClass) {
        A a = clazz.getAnnotation(componentClass);
        if (a == null) {
            Annotation[] annotations = clazz.getAnnotations();
            for (Annotation annotation : annotations) {
                // annotation.annotationType() 返回当前注解实例对应的 注解类型 的 Class 对象。 换句话说，它提供了运行时获取注解类型信息的功能
                if (!"java.lang.annotation".equals(annotation.annotationType().getPackageName())) {
                    A subA = getAnnotation(annotation.annotationType(), componentClass);
                    if (subA != null) {
                        return subA;
                    }
                }
            }
        }
        return a;
    }

    public static String getBeanName(Class<?> clazz) {
        String clazzName = clazz.getName();
        return clazzName.substring(0, 1).toLowerCase() + clazzName.substring(1);
    }

    public static String getFactoryBeanName(Method method) {
        Bean bean = method.getAnnotation(Bean.class);
        String beanName = bean.value();
        if (beanName == null || beanName.isEmpty()) {
            beanName = method.getName();
        }
        return beanName;
    }

    public static Constructor<?> getConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getConstructors();
        if (constructors.length == 0) {
            constructors = clazz.getDeclaredConstructors();
        }
        if (constructors.length != 1) {
            throw new RuntimeException("构造方法不止一个，bean:" + clazz.getName());
        }
        return constructors[0];
    }

    /**
     * 获取类上指定注解的value值
     */
    public static int getOrderValueFromClass(Class<?> clazz) {
        Order order = clazz.getAnnotation(Order.class);
        return order == null ? Integer.MIN_VALUE : order.value();
    }

    /**
     * 获取工厂方法上指定注解的value值
     */
    public static int getOrderValueFromFactoryMethod(Method method) {
        Order order = method.getAnnotation(Order.class);
        return order == null ? Integer.MIN_VALUE : order.value();
    }

    /**
     * class中查找有指定注解的方法
     */
    public static Method findAnnotationMethod(Class<?> clazz, Class<? extends Annotation> annoClass) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            if (method.getAnnotation(annoClass) != null) {
                return method;
            }
        }
        return null;
    }
}
