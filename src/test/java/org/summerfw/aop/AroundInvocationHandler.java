package org.summerfw.aop;

import org.summerfw.annotataion.Around;
import org.summerfw.annotataion.Component;
import org.summerfw.annotataion.Transactional;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author: jiamy
 * @create: 2025/1/9 10:09
 **/
@Component
public class AroundInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass().getAnnotation(Around.class) != null) {
            System.out.println("AroundInvocationHandler....");
            return method.invoke(proxy, args);
        }
        return method.invoke(proxy, args);
    }
}
