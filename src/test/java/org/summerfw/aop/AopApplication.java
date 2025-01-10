package org.summerfw.aop;

import org.summerfw.annotataion.Bean;
import org.summerfw.annotataion.ComponentScan;
import org.summerfw.annotataion.Configuration;

@Configuration
@ComponentScan
public class AopApplication {

    @Bean("aroundProxyBeanPostProcessor")
    AroundProxyBeanPostProcessor createAroundProxyBeanPostProcessor(){
        return new AroundProxyBeanPostProcessor();
    }

    @Bean("afterProxyBeanPostProcessor")
    AfterProxyBeanPostProcessor createAfterProxyBeanPostProcessor(){
        return new AfterProxyBeanPostProcessor();
    }

    @Bean("beforeProxyBeanPostProcessor")
    BeforeProxyBeanPostProcessor createBeforeProxyBeanPostProcessor(){
        return new BeforeProxyBeanPostProcessor();
    }

}