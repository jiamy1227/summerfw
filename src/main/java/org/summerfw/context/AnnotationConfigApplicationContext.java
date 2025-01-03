package org.summerfw.context;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.summerfw.annotataion.Component;
import org.summerfw.annotataion.ComponentScan;
import org.summerfw.annotataion.Configuration;
import org.summerfw.annotataion.Primary;
import org.summerfw.io.PropertyResolver;
import org.summerfw.io.Resource;
import org.summerfw.io.ResourceResolver;
import org.summerfw.util.ClassUtils;

import java.util.*;

/**
 * @author: jiamy
 * @create: 2025/1/2 13:58
 **/
public class AnnotationConfigApplicationContext {

    /**
     * key: beanName
     */
    Map<String, BeanDefinition> beans;

    public AnnotationConfigApplicationContext(Class<?> configClass, PropertyResolver propertyResolver) {
        // 扫描获取所有Bean的Class类型:
        Set<String> beanClassNames = scanForClassNames(configClass);

        // 创建Bean的定义:
        this.beans = createBeanDefinitions(beanClassNames);
    }


    /**
     * 从配置类中的scan范围扫描所有组件
     *
     * @param configClass
     * @return
     */
    private Set<String> scanForClassNames(Class<?> configClass) {
        Set<String> classSet = new HashSet<>();
        // 配置类上的包扫描注解
        ComponentScan componentScan = configClass.getAnnotation(ComponentScan.class);
        // 包扫描注解上配置的扫描路径
        String[] scanPaths = componentScan.value();
        for (String path : scanPaths) {
            ResourceResolver resourceResolver = new ResourceResolver(path);
            List<String> classList = resourceResolver.scan(Resource::getName);
            classSet.addAll(classList);
        }
        return classSet;
    }

    /**
     * 为组件创建对应的BeanDefinition
     *
     * @param beanClassNames
     * @return
     */
    private Map<String, BeanDefinition> createBeanDefinitions(Set<String> beanClassNames) {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        for (String beanClassName : beanClassNames) {
            Class<?> clazz = null;
            try {
                clazz = Class.forName(beanClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            // 判断clazz是否有component注解
            Component component = ClassUtils.getAnnotation(clazz, Component.class);
            if (component != null) {
                BeanDefinition beanDefinition = new BeanDefinition(
                        ClassUtils.getBeanName(clazz),
                        clazz,
                        ClassUtils.getConstructor(clazz),
                        ClassUtils.getOrderValue(clazz),
                        clazz.isAnnotationPresent(Primary.class),
                        null,
                        null,
                        ClassUtils.findAnnotationMethod(clazz, PostConstruct.class),
                        // 查找@PreDestroy方法:
                        ClassUtils.findAnnotationMethod(clazz, PreDestroy.class)
                );
                if (beanDefinitionMap.get(beanDefinition.getName()) != null) {
                    beanDefinitionMap.put(beanDefinition.getName(), beanDefinition);
                } else {
                    throw new RuntimeException("bean定义重复"+ beanDefinition.getName());
                }

                // 查找是否有@Configuration:
                Configuration configuration = ClassUtils.getAnnotation(clazz, Configuration.class);
                if (configuration != null) {
                    // 从工厂方法@Bean中获取bean定义
                }
            }
        }
        return beanDefinitionMap;
    }
}
