package com.javabaas.shell.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javabaas.javasdk.JBClazz;
import com.javabaas.javasdk.JBUtils;
import com.javabaas.shell.common.CommandContext;
import org.fusesource.jansi.Ansi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Created by Staryet on 15/8/20.
 * <p>
 * 类信息命令
 */
@Component
public class ClassAclCommands implements CommandMarker {

    @Autowired
    private CommandContext context;

    @CliAvailabilityIndicator({"acls", "acl set"})
    public boolean isAvailable() {
        return context.getCurrentClass() != null;
    }

    @CliCommand(value = "acls", help = "get acl")
    public void getACL() throws JsonProcessingException {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        //显示类信息
        try {
            JBClazz clazz = JBClazz.get(className);
            System.out.println(clazz.getAcl());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "acl set", help = "class acl")
    public void setACL(@CliOption(key = {""}, mandatory = true, help = "Object by json.") final String acl)
            throws JsonProcessingException {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        try {
            JBClazz clazz = new JBClazz(className);
            JBClazz.JBClazzAcl clazzAcl = JBUtils.readValue(acl, JBClazz.JBClazzAcl.class);
            clazz.setAcl(clazzAcl);
            clazz.updateClazzAcl();
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("ACL updated.").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

}
