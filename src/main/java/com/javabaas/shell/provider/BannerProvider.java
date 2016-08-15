package com.javabaas.shell.provider;

import com.javabaas.shell.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.shell.plugin.support.DefaultBannerProvider;
import org.springframework.shell.support.util.OsUtils;
import org.springframework.stereotype.Component;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class BannerProvider extends DefaultBannerProvider {

    @Autowired
    private PropertiesUtil properties;

    public String getBanner() {
        return "   ___                     ______                    " + OsUtils.LINE_SEPARATOR +
                "  |_  |                    | ___ \\                   " + OsUtils.LINE_SEPARATOR +
                "    | |  __ _ __   __ __ _ | |_/ /  __ _   __ _  ___ " + OsUtils.LINE_SEPARATOR +
                "    | | / _` |\\ \\ / // _` || ___ \\ / _` | / _` |/ __|" + OsUtils.LINE_SEPARATOR +
                "/\\__/ /| (_| | \\ V /| (_| || |_/ /| (_| || (_| |\\__ \\" + OsUtils.LINE_SEPARATOR +
                "\\____/  \\__,_|  \\_/  \\__,_|\\____/  \\__,_| \\__,_||___/" + OsUtils.LINE_SEPARATOR +
                "Version:" + this.getVersion() + OsUtils.LINE_SEPARATOR +
                "Host:" + properties.getHost() + OsUtils.LINE_SEPARATOR +
                "AdminKey:" + properties.getKey();
    }

    public String getVersion() {
        return "1.0.0";
    }

    public String getWelcomeMessage() {
        return null;
    }

    @Override
    public String getProviderName() {
        return "JavaBaas";
    }

}