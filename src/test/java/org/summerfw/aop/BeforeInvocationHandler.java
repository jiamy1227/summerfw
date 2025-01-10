package org.summerfw.aop;

import org.summerfw.annotataion.Component;

import java.lang.reflect.Method;

/**
 * @author: jiamy
 * @create: 2025/1/9 16:26
 **/
@Component
public class BeforeInvocationHandler extends BeforeInvocationHandlerAdapter {

    @Override
    public void before(Object proxy, Method method, Object[] args) {
        System.out.println("BeforeInvocationHandler.....");
    }
}
