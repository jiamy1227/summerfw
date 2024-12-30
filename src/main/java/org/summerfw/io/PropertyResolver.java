package org.summerfw.io;

import java.time.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.Function;

/**
 * @author: jiamy
 * @create: 2024/12/30 16:29
 **/
public class PropertyResolver {

    /**
     * 存储String类型的key Value
     */
    Map<String ,String> properties = new HashMap<>();

    // 存储Class -> Function:
    Map<Class<?>, Function<String, Object>> converters = new HashMap<>();

    public PropertyResolver(Properties props) {
        this.properties.putAll(System.getenv());
        Set<String> propNames = props.stringPropertyNames();
        for (String prop : propNames) {
            properties.put(prop, props.getProperty(prop));
        }
        // String类型:
        converters.put(String.class, s -> s);
        // boolean类型:
        converters.put(boolean.class, s -> Boolean.parseBoolean(s));
        converters.put(Boolean.class, s -> Boolean.valueOf(s));
        // int类型:
        converters.put(int.class, s -> Integer.parseInt(s));
        converters.put(Integer.class, s -> Integer.valueOf(s));
        // 其他基本类型...
        // Date/Time类型:
        converters.put(LocalDate.class, s -> LocalDate.parse(s));
        converters.put(LocalTime.class, s -> LocalTime.parse(s));
        converters.put(LocalDateTime.class, s -> LocalDateTime.parse(s));
        converters.put(ZonedDateTime.class, s -> ZonedDateTime.parse(s));
        converters.put(Duration.class, s -> Duration.parse(s));
        converters.put(ZoneId.class, s -> ZoneId.of(s));
    }

    public String getProperty(String key) {
        return this.properties.get(key);
    }

    public <T> T getProperty(String key, Class<T> targetType) {
        String s = properties.get(key);
        if(s ==null){
            return null;
        }
        Function<String, Object> fn = this.converters.get(targetType);
        return (T) fn.apply(s);
    }
}
