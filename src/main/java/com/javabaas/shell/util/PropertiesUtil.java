package com.javabaas.shell.util;

import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Codi on 16/7/21.
 */
@Component
public class PropertiesUtil {

    private static final String DEFAULT_HOST = "http://127.0.0.1:8080/api/";
    private static final String DEFAULT_KEY = "JavaBaas";
    private static final int DEFAULT_COL = 200;
    private String host;
    private String key;
    private String limit;
    private int col;

    public PropertiesUtil() throws IOException {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            //首先从同目录下加载
            inputStream = new FileInputStream("config.properties");
        } catch (FileNotFoundException ignored) {
        }
        if (inputStream == null) {
            //文件加载失败 从资源文件处加载
            inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
        }
        if (inputStream != null) {
            properties.load(inputStream);
            key = properties.getProperty("key");
            host = properties.getProperty("host");
            try {
                col = Integer.valueOf(properties.getProperty("col"));
            } catch (NumberFormatException ignored) {
            }
        }
    }

    public String getHost() {
        return host != null ? host : DEFAULT_HOST;
    }

    public String getKey() {
        return key != null ? key : DEFAULT_KEY;
    }
}
