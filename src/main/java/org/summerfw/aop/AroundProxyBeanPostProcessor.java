package org.summerfw.aop;

import org.summerfw.annotataion.Around;
import org.summerfw.context.BeanDefinition;
import org.summerfw.context.BeanPostProcessor;
import org.summerfw.context.ConfigurableApplicationContext;
import org.summerfw.util.ApplicationContextUtils;

import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: jiamy
 * @create: 2025/1/9 13:46
 **/
public class AroundProxyBeanPostProcessor implements BeanPostProcessor {

    Map<String, Object> originBeans = new HashMap<>();


    /**
     * bean实例化之后，检测Around注解，根据注解配置的invocationHandler执行handler中的处理
     * @param bean
     * @param beanName
     * @return
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        Class<?> clazz = bean.getClass();
        Around around = clazz.getAnnotation(Around.class);
        if (around != null) {
            String handlerName;
            // 获取注解中Value的用户自定义的handler名称
            // handlerName = (String) around.annotationType().getMethod("value").invoke(around);
            handlerName = around.value();
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
}
