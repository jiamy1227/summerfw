package org.summerfw.aop;

import org.summerfw.annotataion.Transactional;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author: jiamy
 * @create: 2025/1/9 10:09
 **/
public class TransactionalInvocationHandler implements InvocationHandler {
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getAnnotation(Transactional.class) != null) {
            String ret = (String) method.invoke(proxy, args);
            return ret+ "!";
        }
        return method.invoke(proxy, args);
    }
}
