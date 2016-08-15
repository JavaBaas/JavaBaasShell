package com.javabaas.shell.commands;

import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.entity.JBApp;
import com.javabaas.shell.util.PropertiesUtil;
import com.javabaas.shell.util.SignUtil;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Codi on 15/9/22.
 * <p>
 * 应用命令
 */
@Component
public class AppCommands implements CommandMarker {

    @Autowired
    private CommandContext context;
    @Resource(name = "AdminRestTemplate")
    private RestTemplate rest;
    @Autowired
    private SignUtil signUtil;
    @Autowired
    private PropertiesUtil properties;

    @CliAvailabilityIndicator({"export", "info"})
    public boolean isAvailable() {
        return context.getCurrentApp() != null;
    }

    @CliCommand(value = "apps", help = "Show app list.")
    public void list() {
        context.cancelDoubleCheck();
        try {
            JBApp[] result = rest.getForObject(properties.getHost() + "admin/app/", JBApp[].class);
            for (JBApp app : result) {
                System.out.println(app.getName());
            }
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "app add", help = "Add app.")
    public void add(@CliOption(key = {""}, mandatory = true) final String name) {
        context.cancelDoubleCheck();
        try {
            Map<String, Object> field = new HashMap<>();
            field.put("name", name);
            rest.postForObject(properties.getHost() + "admin/app/", field, String.class);
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("App added.").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "app del", help = "Delete class.")
    public void delete(@CliOption(key = {""}, mandatory = true) final String name) {
        context.cancelDoubleCheck();
        //显示类信息
        try {
            JBApp[] apps = rest.getForObject(properties.getHost() + "admin/app/", JBApp[].class);
            for (JBApp app : apps) {
                if (app.getName().equals(name)) {
                    System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Do you really want to delete? (Y/N)"));
                    context.setDoubleCheck(new DoubleCheckListener() {
                        @Override
                        public void confirm() {
                            rest.delete(properties.getHost() + "admin/app/" + app.getId(), String.class);
                            context.setCurrentApp(null);
                            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("App deleted.").reset());
                        }

                        @Override
                        public void cancel() {

                        }
                    });
                    return;
                }
            }
            //未找到应用
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("App not found!").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "use", help = "Set current app.")
    public void set(@CliOption(key = {""}, mandatory = false, help = "app name") final String name) {
        context.cancelDoubleCheck();
        if (name == null) {
            //重置当前应用
            context.setCurrentApp(null);
        } else {
            try {
                JBApp[] apps = rest.getForObject(properties.getHost() + "admin/app/", JBApp[].class);
                for (JBApp app : apps) {
                    if (app.getName().equals(name)) {
                        rest.getForObject(properties.getHost() + "admin/app/" + app.getId(), String.class);
                        System.out.println("Set current app to " + Ansi.ansi().fg(Ansi.Color.GREEN).a(app.getName()).reset());
                        context.setCurrentApp(app);
                        return;
                    }
                }
                //未找到应用
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("App not found!").reset());
            } catch (HttpClientErrorException e) {
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
            }
        }
    }

    @CliCommand(value = "export", help = "Export tha app.")
    public void export() {
        context.cancelDoubleCheck();
        JBApp app = context.getCurrentApp();
        String appExport = rest.getForObject(properties.getHost() + "admin/app/" + app.getId() + "/export", String.class);
        System.out.println(appExport);
    }

    @CliCommand(value = "info", help = "Get app info")
    public void appInfo() {
        context.cancelDoubleCheck();
        JBApp app = context.getCurrentApp();
        String appInfo = rest.getForObject(properties.getHost() + "admin/app/" + app.getId(), String.class);
        System.out.println(appInfo);
    }

    @CliCommand(value = "import", help = "Import tha app.")
    public void importData(@CliOption(key = {""}, mandatory = true, help = "app name") final String app) {
        context.cancelDoubleCheck();
        try {
            rest.postForObject(properties.getHost() + "admin/app/import", app, String.class);
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("App imported.").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "token", help = "Get Token.")
    public void token() {
        context.cancelDoubleCheck();
        //获取令牌
        String timestamp = signUtil.getTimestamp();
        System.out.println("Timestamp:  " + timestamp);
        System.out.println("AdminSign:  " + signUtil.getAdminSign(timestamp));
        if (context.getCurrentApp() != null) {
            System.out.println("AppId:  " + signUtil.getAppId());
            System.out.println("Key:  " + context.getCurrentApp().getKey());
            System.out.println("MasterKey:  " + context.getCurrentApp().getMasterKey());
            System.out.println("Sign:  " + signUtil.getSign(timestamp));
            System.out.println("MasterSign:  " + signUtil.getMasterSign(timestamp));
        }
    }

}
