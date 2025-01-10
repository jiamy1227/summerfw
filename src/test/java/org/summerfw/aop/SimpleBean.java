package org.summerfw.aop;

import org.summerfw.annotataion.*;

@Component
@Around("aroundInvocationHandler")
@After("afterInvocationHandler")
public class SimpleBean {

    @Value("${app.name}")
    public String name;

    public String version;

    @Value("${app.version}")
    public void setVersion(String version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return this.version;
    }

    public String hello(){
        return "Hello," + name;
    }


    public String helloAfter(){
        System.out.println("helloAfter");
        return "Hello," + name;
    }
}