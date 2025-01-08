package org.summerfw.context;

/**
 * @author: jiamy
 * @create: 2025/1/8 16:00
 **/
public interface ApplicationContext extends AutoCloseable {
    <T> T getBean(String name);

    <T> T getBean(Class<T> clazz);

    @Override
    void close() throws Exception;
}
