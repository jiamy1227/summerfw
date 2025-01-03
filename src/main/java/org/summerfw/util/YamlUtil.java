package org.summerfw.util;

import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * yaml文件读取为Map<String, String>并对嵌套的值扁平化处理
 *
 * @author: jiamy
 * @create: 2024/12/30 17:02
 **/
public class YamlUtil {
    public static Map<String, String> loadYamlAsPlainMap(InputStream file){
        Yaml yaml = new Yaml();
        Map<String, Object> configs = yaml.load(file);
        Map<String,String> results = new HashMap<>();
        flatten(null, configs, results);
        return results;
    }

    private static void flatten(String preKey, Map<String, Object> configs, Map<String,String> results) {
        for(Map.Entry<String, Object> entry : configs.entrySet()) {
            String key = entry.getKey();
            if (preKey != null && !"".equals(preKey)) {
                key = preKey + '.' + key;
            }
            Object value = entry.getValue();
            if (value instanceof String) {
                results.put(key, (String) value);
            } else if (value instanceof Map){
                flatten(key, (Map<String, Object>) value, results);
            }
        }
    }
}
