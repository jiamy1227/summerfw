package org.summerfw;

import org.junit.jupiter.api.Test;
import org.summerfw.aop.AopApplication;
import org.summerfw.aop.ProxyResolver;
import org.summerfw.aop.SimpleBean;
import org.summerfw.context.AnnotationConfigApplicationContext;
import org.summerfw.context.ApplicationContext;
import org.summerfw.io.PropertyResolver;
import org.summerfw.aop.TransactionalInvocationHandler;
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

        OriginBean originBean = context.getBean(InjectProxyOnConstructorBean.class).injected;
        System.out.println(originBean.name);
    }

    @Test
    public void testAop(){
        Map<String, String> configs = YamlUtil.loadYamlAsPlainMap(ScanApplication.class.getClassLoader().getResourceAsStream("application.yml"));
        Properties props = new Properties();
        props.putAll(configs);
        PropertyResolver pr = new PropertyResolver(props);
        ApplicationContext context = new AnnotationConfigApplicationContext(AopApplication.class, pr);
        SimpleBean simpleBean = context.getBean(SimpleBean.class);
        System.out.println(simpleBean.hello());

        System.out.println(simpleBean.helloAfter());
    }
}
