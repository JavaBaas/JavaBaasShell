package com.javabaas.shell.commands;

import com.javabaas.javasdk.JBClazz;
import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.util.PropertiesUtil;
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
import java.util.List;

/**
 * Created by Staryet on 15/8/20.
 * <p>
 * 类命令
 */
@Component
public class ClassCommands implements CommandMarker {

    @Autowired
    private CommandContext context;
    @Autowired
    private PropertiesUtil properties;
    @Resource(name = "MasterRestTemplate")
    private RestTemplate rest;

    @CliAvailabilityIndicator({"classes", "set", "class add", "class del", "class export", "class import"})
    public boolean isAvailable() {
        return context.getCurrentApp() != null;
    }

    @CliCommand(value = "classes", help = "Show class list.")
    public void find() {
        context.cancelDoubleCheck();
        //显示列表
        List<JBClazz> list = JBClazz.list();
        list.forEach(clazz -> System.out.println(clazz.getName() + "(" + clazz.getCount() + ")"));
    }

    @CliCommand(value = "set", help = "Set current class.")
    public void find(@CliOption(key = {""}, mandatory = false, help = "class name") final String name) {
        context.cancelDoubleCheck();
        if (name == null) {
            //重置当前类
            context.setCurrentClass(null);
        } else {
            try {
                JBClazz.get(name);
                System.out.println(Ansi.ansi().a("Set current class to ").fg(Ansi.Color.GREEN).a(name).reset());
                context.setCurrentClass(name);
            } catch (HttpClientErrorException e) {
                System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
            }
        }
    }

    @CliCommand(value = "class del", help = "Delete class.")
    public void delete(@CliOption(key = {""}, mandatory = true) final String name) {
        context.cancelDoubleCheck();
        //显示类信息
        try {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Do you really want to delete? (Y/N)"));
            context.setDoubleCheck(new DoubleCheckListener() {
                @Override
                public void confirm() {
                    JBClazz clazz = new JBClazz(name);
                    clazz.delete();
                    context.setCurrentClass(null);
                    System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Class deleted.").reset());
                }

                @Override
                public void cancel() {

                }
            });
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "class add", help = "Add field.")
    public void add(@CliOption(key = {""}, mandatory = true) final String name) {
        context.cancelDoubleCheck();
        try {
            JBClazz clazz = new JBClazz(name);
            clazz.save();
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Class added.").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "class export", help = "Export tha class.")
    public void export(@CliOption(key = {""}, mandatory = true) final String name) {
        context.cancelDoubleCheck();
        try {
            String appExport = rest.getForObject(properties.getHost() + "master/clazz/" + name + "/export", String.class);
            System.out.println(appExport);
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "class import", help = "Import tha class.")
    public void importData(@CliOption(key = {""}, help = "clazz", mandatory = true) final String clazz) {
        context.cancelDoubleCheck();
        try {
            rest.postForObject(properties.getHost() + "master/clazz/import", clazz, String.class);
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Clazz imported.").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

}
