package org.summerfw.context;

import jakarta.annotation.Nullable;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.summerfw.annotataion.*;
import org.summerfw.io.PropertyResolver;
import org.summerfw.io.Resource;
import org.summerfw.io.ResourceResolver;
import org.summerfw.util.ClassUtils;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: jiamy
 * @create: 2025/1/2 13:58
 **/
public class AnnotationConfigApplicationContext {

    /**
     * key: beanName
     */
    Map<String, BeanDefinition> beans;

    PropertyResolver propertyResolver;

    // 正在创建的bean
    Set<String> creatingBeanNames;

    public AnnotationConfigApplicationContext(Class<?> configClass, PropertyResolver propertyResolver) {

        this.propertyResolver = propertyResolver;

        // 扫描获取所有Bean的Class类型:
        Set<String> beanClassNames = scanForClassNames(configClass);

        // 创建Bean的定义:
        this.beans = createBeanDefinitions(beanClassNames);

        // 先创建@Configuration类型的Bean:
        this.beans.values().stream()
                .filter(def -> def.getBeanClass().getAnnotation(Configuration.class) != null)
                .forEach(this::createBeanAsEarlySingleton);


        // 创建其他普通Bean:
        this.beans.values().stream()
                .filter(def -> def.getInstance() == null)
                .forEach(this::createBeanAsEarlySingleton);

        // 通过字段和set方法注入依赖:
        this.beans.values().forEach(this::injectBean);

        // 调用init方法:
        this.beans.values().forEach(this::initBean);
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
                        ClassUtils.getOrderValueFromClass(clazz),
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
                    getFactoryBeans(clazz, beanDefinitionMap);
                }
            }
        }
        return beanDefinitionMap;
    }

    /**
     * 获取工程方法定义的bean信息
     *
     * @param clazz
     * @param beanDefinitionMap
     */
    private void getFactoryBeans(Class<?> clazz, Map<String, BeanDefinition> beanDefinitionMap) {
        Method[] methods = clazz.getMethods();
        for (Method method : methods) {
            Bean bean = method.getAnnotation(Bean.class);
            if (bean != null) {
                BeanDefinition beanDefinition = new BeanDefinition(
                        ClassUtils.getFactoryBeanName(method),
                        method.getReturnType(),
                        method.getName(),
                        method,
                        ClassUtils.getOrderValueFromFactoryMethod(method),
                        method.isAnnotationPresent(Primary.class),
                        bean.initMethod() == null ? null : bean.initMethod(),
                        bean.destroyMethod() == null ? null : bean.destroyMethod(),
                        null,
                        null
                );
                if (beanDefinitionMap.get(beanDefinition.getName()) != null) {
                    beanDefinitionMap.put(beanDefinition.getName(), beanDefinition);
                } else {
                    throw new RuntimeException("bean定义重复"+ beanDefinition.getName());
                }
            }
        }
    }

    // 根据Name查找BeanDefinition，如果Name不存在，返回null
    @Nullable
    public BeanDefinition findBeanDefinition(String name) {
        return this.beans.get(name);
    }

    /**
     * 根据Type查找某个BeanDefinition，如果不存在返回null，如果存在多个返回@Primary标注的一个:
     * 1.查询type类型及父类类型的bean定义
     * 2.查询中的lise中筛选primary
     *
     * @param type
     * @return
     */
    @Nullable
    public BeanDefinition findBeanDefinition(Class<?> type) {
        List<BeanDefinition> beanDefinitions = this.beans.values().stream().filter(e -> type.isAssignableFrom(e.getBeanClass())).toList();
        if (beanDefinitions.size() == 1) {
            return beanDefinitions.get(0);
        } else if (beanDefinitions.size() == 0) {
            throw new RuntimeException("没有找到此类型的bean：" + type);
        } else {
            List<BeanDefinition> duplicateBeans = beanDefinitions.stream().filter(BeanDefinition::isPrimary).toList();
            if (duplicateBeans.size() == 1) {
                return duplicateBeans.get(0);
            } else if (duplicateBeans.size() == 0) {
                throw new RuntimeException("重复的bean定义：" + type);
            } else {
                throw new RuntimeException("重复的primary bean定义：" + type);
            }
        }
    }

    /**
     * 创建一个Bean，但不进行字段和方法级别的注入。如果创建的Bean不是Configuration，则在构造方法/工厂方法中注入的依赖Bean会自动创建
     * @param def
     * @return
     */
    public Object createBeanAsEarlySingleton(BeanDefinition def) {
        // 检测循环依赖:
        if (!this.creatingBeanNames.add(def.getName())) {
            throw new RuntimeException("bean创建失败，存在循环依赖beanName：" + def.getName());
        }
        Executable executable = def.getFactoryName() == null ? def.getConstructor() : def.getFactoryMethod();
        Parameter[] parameters = executable.getParameters();
        Object[] args = new Object[parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            Value valueAnno = parameters[i].getAnnotation(Value.class);
            if(valueAnno!=null){
                args[i] = propertyResolver.getProperty(valueAnno.value());
            } else {
                Autowired autowiredAnno = parameters[i].getAnnotation(Autowired.class);
                String beanName = autowiredAnno.name();
                BeanDefinition dependsOnDef = beanName.isEmpty()? findBeanDefinition(parameters[i].getType()) : findBeanDefinition(beanName);
                Object autowiredBeanInstance = dependsOnDef.getInstance();
                if (autowiredBeanInstance == null) {
                    autowiredBeanInstance = createBeanAsEarlySingleton(dependsOnDef);
                }
                args[i] = autowiredBeanInstance;
            }
        }
        Object instance = null;
        if(def.getFactoryName() == null){
            try {
                // 通过构造方法实例化
                instance = def.getConstructor().newInstance(args);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                // 通过工厂方法实例化:通过工厂方法获取configurationClass,在获取configurationBean
                Object configInstance = findBeanDefinition(ClassUtils.getBeanName(def.getFactoryMethod().getDeclaringClass()));
                instance = def.getFactoryMethod().invoke(configInstance, args);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    /**
     * 字段注入和set方法注入
     * @param beanDefinition
     */
    private void injectBean(BeanDefinition beanDefinition) {
        injectProperties(beanDefinition, beanDefinition.getBeanClass(), beanDefinition.getInstance());
    }

    // 在当前类及父类进行字段和方法注入:
    void injectProperties(BeanDefinition def, Class<?> clazz, Object bean) {
        for (Field field : clazz.getDeclaredFields()) {
            tryInjectProperties(def, clazz, bean, field);
        }
        for(Method method:clazz.getMethods()) {
            tryInjectProperties(def, clazz, bean, method);
        }
        // 父类中的属性注入
        Class<?> superClazz = clazz.getSuperclass();
        if (superClazz != null) {
            injectProperties(def, superClazz, bean);
        }
    }

    // 注入单个属性
    void tryInjectProperties(BeanDefinition def, Class<?> clazz, Object bean, AccessibleObject acc) {
        // 注入前check访问修饰符等
        Value value = acc.getAnnotation(Value.class);
        Autowired autowired = acc.getAnnotation(Autowired.class);
        if(value ==null && autowired==null){
            return;
        }

        Field field = null;
        Method method = null;
        // 判断是字段还是方法注入
        if(acc instanceof Field f){
            checkFieldOrMethod(f);
            f.setAccessible(true);
            field = f;
        }
        if (acc instanceof Method) {
            Method m = (Method) acc;
            checkFieldOrMethod(m);
            m.setAccessible(true);
            method = m;
        }

        // Value注入
        if (value != null) {
            Object propertyValue = this.propertyResolver.getProperty(value.value(), field.getType());
            // 字段注入
            if (field != null) {
                try {
                    field.set(bean, propertyValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else if (method !=null){
                try {
                    method.invoke(bean, propertyValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        // AutoWired注入
        if (autowired != null) {
            String name = autowired.name();
            // 字段注入
            if (field != null) {
                Object depends = name.isEmpty() ? findBeanDefinition(field.getType()) : findBeanDefinition(name);
                try {
                    field.set(bean, depends);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else if (method != null) {
                Object depends = name.isEmpty() ? findBeanDefinition(method.getReturnType()) : findBeanDefinition(name);
                try {
                    method.invoke(bean, depends);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    void checkFieldOrMethod(Member m) {
        int mod = m.getModifiers();
        if (Modifier.isFinal(mod)) {
            throw new RuntimeException("final属性不可注入");
        }
        if (Modifier.isStatic(mod)) {
            throw new RuntimeException("静态属性不可注入");
        }
    }

    /**
     * 调用bean的初始化方法
     * @param def
     */
    private void initBean(BeanDefinition def) {
        try {
            def.getInitMethod().invoke(def.getInstance());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
