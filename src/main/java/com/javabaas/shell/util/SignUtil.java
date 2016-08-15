package com.javabaas.shell.util;

import com.javabaas.shell.common.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.util.Date;

/**
 * Created by Staryet on 15/9/11.
 */
@Component
public class SignUtil {

    @Autowired
    private CommandContext commandContext;
    @Autowired
    private PropertiesUtil propertiesUtil;

    public String getTimestamp() {
        return String.valueOf(new Date().getTime());
    }

    public String getSign(String timestamp) {
        return DigestUtils.md5DigestAsHex((commandContext.getCurrentApp().getKey() + ":" + timestamp).getBytes());
    }

    public String getMasterSign(String timestamp) {
        return DigestUtils.md5DigestAsHex((commandContext.getCurrentApp().getMasterKey() + ":" + timestamp).getBytes());
    }

    public String getAdminSign(String timestamp) {
        return DigestUtils.md5DigestAsHex((propertiesUtil.getKey() + ":" + timestamp).getBytes());
    }

    public String getAppId() {
        return commandContext.getCurrentApp().getId();
    }

}
