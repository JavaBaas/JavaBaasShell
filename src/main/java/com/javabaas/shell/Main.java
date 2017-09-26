package com.javabaas.shell;

import com.javabaas.javasdk.JBConfig;
import org.springframework.shell.Bootstrap;

import java.io.IOException;

/**
 * Created by Staryet on 15/8/16.
 */
public class Main {

    public static void main(String[] args) throws IOException {
        JBConfig.initAdmin("http://127.0.0.1:9000/api", "JavaBaas");
        Bootstrap.main(args);
    }

}