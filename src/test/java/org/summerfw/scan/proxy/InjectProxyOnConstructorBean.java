package org.summerfw.scan.proxy;

import org.summerfw.annotataion.Autowired;
import org.summerfw.annotataion.Component;

@Component
public class InjectProxyOnConstructorBean {
    public final OriginBean injected;

    public InjectProxyOnConstructorBean(@Autowired OriginBean injected) {
        this.injected = injected;
    }
}