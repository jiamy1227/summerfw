package org.summerfw;

import org.summerfw.io.PropertyResolver;
import org.summerfw.io.Resource;
import org.summerfw.io.ResourceResolver;
import org.summerfw.util.YamlUtil;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @description: ${description}
 * @author: jiamy
 * @create: 2024/11/25 13:55
 **/
public class Main {
    public static void main(String[] args) {

        Map<String, String> configs = YamlUtil.loadYamlAsPlainMap(Main.class.getClassLoader().getResourceAsStream("application.yml"));
        Properties props = new Properties();
        props.putAll(configs);
        PropertyResolver pr = new PropertyResolver(props);

        System.out.println(pr.getProperty("app.name"));


    }
}