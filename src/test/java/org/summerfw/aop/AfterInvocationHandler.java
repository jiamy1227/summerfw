package org.summerfw.aop;

import org.summerfw.annotataion.Component;

import java.lang.reflect.Method;

/**
 * @author: jiamy
 * @create: 2025/1/9 16:26
 **/
@Component
public class AfterInvocationHandler extends AfterInvocationHandlerAdapter {
    @Override
    public Object after(Object proxy, Object returnValue, Method method, Object[] args) {
        System.out.println("AfterInvocationHandler.....");
        return returnValue;
    }
}
