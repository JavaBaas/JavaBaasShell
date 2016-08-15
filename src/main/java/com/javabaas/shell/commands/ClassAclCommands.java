package com.javabaas.shell.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.entity.JBClass;
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

/**
 * Created by Staryet on 15/8/20.
 * <p>
 * 类信息命令
 */
@Component
public class ClassAclCommands implements CommandMarker {

    @Autowired
    private CommandContext context;
    @Autowired
    private PropertiesUtil properties;
    @Resource(name = "MasterRestTemplate")
    private RestTemplate rest;

    @CliAvailabilityIndicator({"acls", "acl set"})
    public boolean isAvailable() {
        return context.getCurrentClass() != null;
    }

    @CliCommand(value = "acls", help = "get acl")
    public void getACL() throws JsonProcessingException {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        ObjectMapper mapper = new ObjectMapper();
        //显示类信息
        try {
            JBClass baasClass = rest.getForObject(properties.getHost() + "master/clazz/" + className, JBClass.class);
            System.out.println(mapper.writeValueAsString(baasClass.getAcl()));
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
            rest.postForLocation(properties.getHost() + "master/clazz/" + className + "/acl", acl);
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("ACL updated.").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

}
