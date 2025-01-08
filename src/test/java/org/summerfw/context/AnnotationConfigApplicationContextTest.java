package org.summerfw.context;

import org.junit.jupiter.api.Test;
import org.summerfw.io.PropertyResolver;
import org.summerfw.scan.proxy.InjectProxyOnConstructorBean;
import org.summerfw.scan.proxy.OriginBean;
import org.summerfw.scan.ScanApplication;
import org.summerfw.util.YamlUtil;

import java.util.Map;
import java.util.Properties;

/**
 * @author: jiamy
 * @create: 2025/1/8 11:21
 **/
public class AnnotationConfigApplicationContextTest {
    @Test
    public void testProxy(){
        Map<String, String> configs = YamlUtil.loadYamlAsPlainMap(ScanApplication.class.getClassLoader().getResourceAsStream("application.yml"));
        Properties props = new Properties();
        props.putAll(configs);
        PropertyResolver pr = new PropertyResolver(props);
        ApplicationContext context = new AnnotationConfigApplicationContext(ScanApplication.class, pr);

        InjectProxyOnConstructorBean injectProxyOnConstructorBean = context.getBean(InjectProxyOnConstructorBean.class);
        System.out.println(injectProxyOnConstructorBean.injected.name);
    }
}
