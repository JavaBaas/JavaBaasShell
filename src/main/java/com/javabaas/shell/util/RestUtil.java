package com.javabaas.shell.util;

import com.javabaas.shell.common.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.support.HttpRequestWrapper;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by Staryet on 15/8/23.
 */
@Component
public class RestUtil {

    @Autowired
    private SignUtil signUtil;
    @Autowired
    private CommandContext commandContext;

    @Bean(name = "AdminRestTemplate")
    public RestTemplate getAdminRestTemplate() {
        RestTemplate rest = new RestTemplate();
        setUTF8MessageConverter(rest);
        ClientHttpRequestInterceptor i = new ClientHttpRequestInterceptor() {
            public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
                HttpRequestWrapper requestWrapper = new HttpRequestWrapper(httpRequest);
                long timestamp = new Date().getTime();
                String timestampStr = String.valueOf(timestamp);
                String nonce = UUID.randomUUID().toString().replace("-", "");
                requestWrapper.getHeaders().remove("Content-Type");
                requestWrapper.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
                requestWrapper.getHeaders().add("JB-Timestamp", timestampStr);
                requestWrapper.getHeaders().add("JB-Plat", "admin");
                requestWrapper.getHeaders().add("JB-AdminSign", signUtil.getAdminSign(timestampStr, nonce));
                requestWrapper.getHeaders().add("JB-Nonce", nonce);
                return clientHttpRequestExecution.execute(httpRequest, bytes);
            }
        };
        rest.setInterceptors(Collections.singletonList(i));
        return rest;
    }

    @Bean(name = "MasterRestTemplate")
    public RestTemplate getMasterRestTemplate() {
        RestTemplate rest = new RestTemplate();
        setUTF8MessageConverter(rest);
        ClientHttpRequestInterceptor i = new ClientHttpRequestInterceptor() {
            public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
                HttpRequestWrapper requestWrapper = new HttpRequestWrapper(httpRequest);
                long timestamp = new Date().getTime();
                String timestampStr = String.valueOf(timestamp);
                String nonce = UUID.randomUUID().toString().replace("-", "");
                requestWrapper.getHeaders().remove("Content-Type");
                requestWrapper.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
                requestWrapper.getHeaders().add("JB-Timestamp", timestampStr);
                requestWrapper.getHeaders().add("JB-Plat", "admin");
                requestWrapper.getHeaders().add("JB-AppId", commandContext.getCurrentApp().getId());
                requestWrapper.getHeaders().add("JB-MasterSign", signUtil.getMasterSign(timestampStr, nonce));
                requestWrapper.getHeaders().add("JB-Nonce", nonce);
                return clientHttpRequestExecution.execute(httpRequest, bytes);
            }
        };
        rest.setInterceptors(Collections.singletonList(i));
        return rest;
    }

    private void setUTF8MessageConverter(RestTemplate rest) {
        List<HttpMessageConverter<?>> converters = rest.getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            if (converter.getClass() == StringHttpMessageConverter.class) {
                converters.remove(converter);
                break;
            }
        }
        converters.add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
    }

}
