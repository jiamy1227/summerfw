package org.summerfw.aop;

import org.summerfw.context.BeanDefinition;
import org.summerfw.context.BeanPostProcessor;
import org.summerfw.context.ConfigurableApplicationContext;
import org.summerfw.util.ApplicationContextUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: jiamy
 * @create: 2025/1/9 13:46
 **/
public class AnnotationProxyBeanPostProcessor<A extends Annotation> implements BeanPostProcessor {

    Map<String, Object> originBeans = new HashMap<>();

    // 修改为泛型类
    Class<A> annotationClass;


    /**
     * bean实例化之后，检测Around注解，根据注解配置的invocationHandler执行handler中的处理
     * @param bean
     * @param beanName
     * @return
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        Class<?> clazz = bean.getClass();
        annotationClass = getParameterizedType();
        A around = clazz.getAnnotation(annotationClass);
        if (around != null) {
            String handlerName;
            // 获取注解中Value的用户自定义的handler名称
            // handlerName = (String) around.annotationType().getMethod("value").invoke(around);
            try {
                handlerName = (String) around.annotationType().getMethod("value").invoke(around);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            // 代理前的bean存在BeanPostProcessor中
            originBeans.put(beanName, bean);
            // 根据bean+handler调用proxyResolver创建代理类
            ConfigurableApplicationContext context = (ConfigurableApplicationContext) ApplicationContextUtils.getApplicationContext();
            BeanDefinition beanDefinition = context.findBeanDefinition(handlerName);
            Object handlerBean = beanDefinition.getInstance();
            if (handlerBean == null) {
                handlerBean = context.createBeanAsEarlySingleton(beanDefinition);
            }
            if(handlerBean instanceof InvocationHandler handler){
                return  ProxyResolver.getInstance().createProxy(bean, handler);
            } else {
                throw new RuntimeException("beanName不是InvocationHandler类型");
            }
        } else {
            return bean;
        }
    }

    @Override
    public Object postProcessOnSetProperty(Object bean, String beanName) {
        Object origin = originBeans.get(beanName);
        if (origin != null) {
            // 存在原始Bean时,返回原始Bean:
            return origin;
        }
        return bean;
    }

    @SuppressWarnings("unchecked")
    private Class<A> getParameterizedType() {
        Type type = getClass().getGenericSuperclass();
        if (!(type instanceof ParameterizedType)) {
            throw new IllegalArgumentException("Class " + getClass().getName() + " does not have parameterized type.");
        }
        ParameterizedType pt = (ParameterizedType) type;
        Type[] types = pt.getActualTypeArguments();
        if (types.length != 1) {
            throw new IllegalArgumentException("Class " + getClass().getName() + " has more than 1 parameterized types.");
        }
        Type r = types[0];
        if (!(r instanceof Class<?>)) {
            throw new IllegalArgumentException("Class " + getClass().getName() + " does not have parameterized type of class.");
        }
        return (Class<A>) r;
    }
}
