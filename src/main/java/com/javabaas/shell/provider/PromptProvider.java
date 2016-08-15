package com.javabaas.shell.provider;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultPromptProvider;
import org.springframework.stereotype.Component;


@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PromptProvider extends DefaultPromptProvider {

    public static final String DEFAULT_PROMPT = "BAAS>";
    private String prompt = DEFAULT_PROMPT;

    @Override
    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void defaultPrompt() {
        this.prompt = DEFAULT_PROMPT;
    }

    @Override
    public String getProviderName() {
        return "My prompt provider";
    }

}
