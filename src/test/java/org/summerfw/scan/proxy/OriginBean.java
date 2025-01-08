package org.summerfw.scan.proxy;

import org.summerfw.annotataion.Component;
import org.summerfw.annotataion.Value;

@Component
public class OriginBean {

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
}