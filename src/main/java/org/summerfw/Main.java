package org.summerfw;

import org.summerfw.annotataion.Component;
import org.summerfw.annotataion.Configuration;
import org.summerfw.io.PropertyResolver;
import org.summerfw.io.Resource;
import org.summerfw.io.ResourceResolver;
import org.summerfw.util.ClassUtils;
import org.summerfw.util.YamlUtil;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @description: ${description}
 * @author: jiamy
 * @create: 2024/11/25 13:55
 **/
@Configuration
public class Main {
    public static void main(String[] args) {
        // 读取
        Map<String, String> configs = YamlUtil.loadYamlAsPlainMap(Main.class.getClassLoader().getResourceAsStream("application.yml"));
        Properties props = new Properties();
        props.putAll(configs);
        PropertyResolver pr = new PropertyResolver(props);

        System.out.println(pr.getProperty("app.item.sub.prop"));

        Component component = ClassUtils.getAnnotation(Main.class, Component.class);
        if(component!=null){
            System.out.println(component.annotationType().getPackageName());
        }
    }
}