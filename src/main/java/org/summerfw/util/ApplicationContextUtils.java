package org.summerfw.util;

import org.summerfw.context.ApplicationContext;

/**
 * @author: jiamy
 * @create: 2025/1/9 13:55
 **/
public class ApplicationContextUtils {

    private static ApplicationContext applicationContext = null;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext ctx) {
        applicationContext = ctx;
    }
}
