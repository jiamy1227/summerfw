package org.summerfw.scan.proxy;

public class FirstProxyBean extends OriginBean {
    final OriginBean target;

    public FirstProxyBean(OriginBean target) {
        this.target = target;
    }
}