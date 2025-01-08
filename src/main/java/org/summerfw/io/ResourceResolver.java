package org.summerfw.io;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Function;

/**
 *
 *
 * @author: jiamy
 * @create: 2024/12/30 13:37
 **/
public class ResourceResolver {

    private String basePackage;

    private String basePackagePath;

    public ResourceResolver(String basePackage) {
        this.basePackage = basePackage;
        this.basePackagePath = this.basePackage.replace('.', File.separatorChar);
    }

    public <R> List<R> scan(Function<Resource, R> mapper) {
        List<R> collector  = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Enumeration<URL> resources = null;
        try {
            resources = classLoader.getResources(this.basePackage.replace('.', '/'));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            File file = new File(resource.getFile());
            processDirectory(file, collector, mapper);
        }

        return collector;
    }

    private <R> void processDirectory(File directory, List<R> collector, Function<Resource, R> mapper) {
        // 获取当前目录下的所有文件和子目录
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 如果是子目录，递归处理
                    processDirectory(file, collector, mapper);
                } else {
                    // 如果是文件，执行Function，Resource转换为目标类型
                    Resource res = new Resource(file.getName().substring(0,file.getName().length()-6),
                            file.getPath().substring(file.getPath().indexOf(this.basePackagePath), file.getPath().indexOf(File.separatorChar + file.getName()))
                                    .replace(File.separatorChar, '.'));
                    R r = mapper.apply(res);
                    collector.add(r);
                }
            }
        }
    }
}
