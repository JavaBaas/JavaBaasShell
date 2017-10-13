package com.javabaas.shell.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javabaas.javasdk.JBApp;
import com.javabaas.javasdk.JBUtils;
import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.util.ASKUtil;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Codi on 15/9/22.
 * <p>
 * 应用命令
 */
@Component
public class AppCommands implements CommandMarker {

    @Autowired
    private CommandContext context;
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
            List<JBApp> list = JBApp.list();
            list.forEach(o -> System.out.println(o.getName()));
//            JBApp.listInBackground(new JBAppListCallback() {
//                @Override
//                public void done(boolean success, List<JBApp> list, JBException e) {
//                    if (success) {
//                        list.forEach(o -> System.out.println(o.getName()));
//                    } else {
//                        System.out.println(e.getMessage());
//                    }
//                }
//            });
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "app add", help = "Add app.")
    public void add(@CliOption(key = {""}, mandatory = true) final String name) {
        context.cancelDoubleCheck();
        try {
            JBApp app = new JBApp();
            app.setName(name);
            app.save();
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("App added.").reset());
            set(name);
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "app del", help = "Delete class.")
    public void delete(@CliOption(key = {""}, mandatory = true) final String name) {
        context.cancelDoubleCheck();
        //显示类信息
        try {
            List<JBApp> list = JBApp.list();
            final boolean[] flag = {false};
            list.forEach(app -> {
                if (app.getName().equals(name)) {
                    flag[0] = true;
                    System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Do you really want to delete? (Y/N)"));
                    context.setDoubleCheck(new DoubleCheckListener() {
                        @Override
                        public void confirm() {
                            app.delete();
                            context.setCurrentApp(null);
                            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("App deleted.").reset());
                        }
                        @Override
                        public void cancel() {

                        }
                    });
                }
            });
            //未找到应用
            if (!flag[0]) {

                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("App not found!").reset());
            }
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
                List<JBApp> list = JBApp.list();
                final boolean[] flag = {false};
                list.forEach(app -> {
                    if (app.getName().equals(name)) {
                        JBApp jbApp = JBApp.get(app.getId());
                        flag[0] = true;
                        System.out.println("Set current app to " + Ansi.ansi().fg(Ansi.Color.GREEN).a(jbApp.getName()).reset());
                        context.setCurrentApp(jbApp);
                        return;
                    }
                });
                //未找到应用
                if (!flag[0]) {
                    System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("App not found!").reset());
                }
            } catch (HttpClientErrorException e) {
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
            }
        }
    }

    @CliCommand(value = "export", help = "Export tha app.")
    public void export() {
        context.cancelDoubleCheck();
        JBApp app = context.getCurrentApp();
        JBApp.JBAppExport appExport = JBApp.export(app.getId());
        System.out.println(JBUtils.writeValueAsString(appExport));
    }

    @CliCommand(value = "info", help = "Get app info")
    public void appInfo() {
        context.cancelDoubleCheck();
        JBApp app = context.getCurrentApp();
        JBApp jbApp = JBApp.get(app.getId());
        System.out.println(jbApp);
    }

    @CliCommand(value = "import", help = "Import tha app.")
    public void importData(@CliOption(key = {""}, mandatory = true, help = "app name") final String app) {
        context.cancelDoubleCheck();
        try {
            JBApp.importData(app);
//            rest.postForObject(properties.getHost() + "admin/app/import", app, String.class);
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
        String nonce = UUID.randomUUID().toString().replace("-", "");
        System.out.println("Timestamp:  " + timestamp);
        System.out.println("Nonce:  " + nonce);
        System.out.println("AdminSign:  " + signUtil.getAdminSign(timestamp, nonce));
        if (context.getCurrentApp() != null) {
            System.out.println("AppId:  " + signUtil.getAppId());
            System.out.println("Key:  " + context.getCurrentApp().getKey());
            System.out.println("MasterKey:  " + context.getCurrentApp().getMasterKey());
            System.out.println("Sign:  " + signUtil.getSign(timestamp, nonce));
            System.out.println("MasterSign:  " + signUtil.getMasterSign(timestamp, nonce));
        }
    }

    @CliCommand(value = "account", help = "Set Account.")
    public void setAccount() throws JsonProcessingException {
        context.cancelDoubleCheck();
        try {
            List<String> accountTypes = getAccountTypes();
            int accountType = ASKUtil.askNumber(accountTypes, "请选择Account Type， 默认为push", 1);
            String key = ASKUtil.askString("请输入key值");
            if (JBUtils.isEmpty(key)) {
                System.out.println("Set Account End!");
                return;
            }
            String secret = ASKUtil.askString("请输入secret值");
            if (JBUtils.isEmpty(secret)) {
                System.out.println("Set Account End!");
                return;
            }

            JBApp.Account account = new JBApp.Account();
            account.setKey(key);
            account.setSecret(secret);

            JBApp.setAccount(JBApp.AccountType.getType(accountType), account);
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Object updated.").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    private List<String> getAccountTypes() {
        List<String> list = new ArrayList<>();
        for (JBApp.AccountType type : JBApp.AccountType.values()) {
            list.add(type.getValue());
        }
        return list;
    }

}
