package org.summerfw.context;

import jakarta.annotation.Nullable;

/**
 * @author: jiamy
 * @create: 2025/1/8 16:04
 **/
public interface ConfigurableApplicationContext extends ApplicationContext {
    // 根据Name查找BeanDefinition，如果Name不存在，返回null
    @Nullable
    BeanDefinition findBeanDefinition(String name);

    @Nullable
    BeanDefinition findBeanDefinition(Class<?> type);

    Object createBeanAsEarlySingleton(BeanDefinition def);
}
