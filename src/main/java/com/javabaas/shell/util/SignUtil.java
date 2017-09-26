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

    public String getSign(String timestamp, String nonce) {
        return DigestUtils.md5DigestAsHex((commandContext.getCurrentApp().getKey() + ":" + timestamp + ":" + nonce).getBytes());
    }

    public String getMasterSign(String timestamp, String nonce) {
        return DigestUtils.md5DigestAsHex((commandContext.getCurrentApp().getMasterKey() + ":" + timestamp + ":" + nonce).getBytes());
    }

    public String getAdminSign(String timestamp, String nonce) {
        return DigestUtils.md5DigestAsHex((propertiesUtil.getKey() + ":" + timestamp + ":" + nonce).getBytes());
    }

    public String getAppId() {
        return commandContext.getCurrentApp().getId();
    }

}
