package com.javabaas.shell.commands;

import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.entity.JBField;
import com.javabaas.shell.entity.JBFieldType;
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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Staryet on 15/8/20.
 * <p>
 * 字段命令
 */
@Component
public class FieldCommands implements CommandMarker {

    @Autowired
    private CommandContext context;
    @Autowired
    private PropertiesUtil properties;
    @Resource(name = "MasterRestTemplate")
    private RestTemplate rest;

    @CliAvailabilityIndicator({"fields", "field add", "field del", "field r", "field nr", "field s", "field ns", "field type"})
    public boolean isAvailable() {
        return context.getCurrentClass() != null;
    }

    /**
     * 显示指定类的属性列表
     */
    @CliCommand(value = "fields", help = "Show field list in class.")
    public void find() {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        JBField[] result = rest.getForObject(properties.getHost() + "master/clazz/" + className + "/field", JBField[].class);
        for (JBField baasField : result) {
            String typeString;
            switch (baasField.getType()) {
                case JBFieldType.STRING:
                    typeString = "<STRING>  ";
                    break;
                case JBFieldType.NUMBER:
                    typeString = "<NUMBER>  ";
                    break;
                case JBFieldType.BOOLEAN:
                    typeString = "<BOOLEAN> ";
                    break;
                case JBFieldType.DATE:
                    typeString = "<DATE>    ";
                    break;
                case JBFieldType.FILE:
                    typeString = "<FILE>    ";
                    break;
                case JBFieldType.OBJECT:
                    typeString = "<OBJECT>  ";
                    break;
                case JBFieldType.ARRAY:
                    typeString = "<ARRAY>   ";
                    break;
                case JBFieldType.POINTER:
                    typeString = "<POINTER> ";
                    break;
                case JBFieldType.GEOPOINT:
                    typeString = "<GEOPOINT>";
                    break;
                default:
                    continue;
            }
            String internalString = baasField.isInternal() ? "I" : " ";
            String securityString = baasField.isSecurity() ? "S" : " ";
            String requiredString = baasField.isRequired() ? "R" : " ";
            System.out.println(Ansi.ansi().fg(Ansi.Color.YELLOW).a(internalString).fg(Ansi.Color.RED).a(securityString).fg(Ansi.Color.GREEN).a(requiredString).fg(Ansi.Color.CYAN).a(typeString).reset().a(baasField.getName()));
        }
    }

    @CliCommand(value = "field del", help = "Delete field.")
    public void delete(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        //显示类信息
        try {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("Do you really want to delete? (Y/N)"));
            context.setDoubleCheck(new DoubleCheckListener() {
                @Override
                public void confirm() {
                    rest.delete(properties.getHost() + "master/clazz/" + className + "/field/" + fieldName, String.class);
                    System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Field deleted.").reset());
                }

                @Override
                public void cancel() {

                }
            });
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "field add", help = "Add field.")
    public void add(@CliOption(key = {""}, mandatory = true) final String fieldName,
                    @CliOption(key = {"type"}, mandatory = false, unspecifiedDefaultValue = "1") final String type) {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        try {
            Map<String, Object> field = new HashMap<>();
            field.put("name", fieldName);
            field.put("type", type);
            rest.postForObject(properties.getHost() + "master/clazz/" + className + "/field", field, String.class);
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Field added.").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "field r", help = "Add field.")
    public void setRequired(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        setRequired(fieldName, true);
    }

    @CliCommand(value = "field nr", help = "Add field.")
    public void setNoRequired(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        setRequired(fieldName, false);
    }

    @CliCommand(value = "field s", help = "Add field.")
    public void setSecurity(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        setSecurity(fieldName, true);
    }

    @CliCommand(value = "field ns", help = "Add field.")
    public void setNoSecurity(@CliOption(key = {""}, mandatory = true) final String fieldName) {
        setSecurity(fieldName, false);
    }

    private void setSecurity(String fieldName, boolean security) {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("security", security);
            rest.put(properties.getHost() + "master/clazz/" + className + "/field/" + fieldName + "/security?security={security}", null, params);
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Field security updated.").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    private void setRequired(String fieldName, boolean required) {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("required", required);
            rest.put(properties.getHost() + "master/clazz/" + className + "/field/" + fieldName + "/required?required={required}", null, params);
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Field required updated.").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "field type", help = "Show field Types.")
    public void fieldType() {
        context.cancelDoubleCheck();
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("1").fg(Ansi.Color.CYAN).a(" STRING").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("2").fg(Ansi.Color.CYAN).a(" NUMBER").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("3").fg(Ansi.Color.CYAN).a(" BOOLEAN").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("4").fg(Ansi.Color.CYAN).a(" DATE").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("5").fg(Ansi.Color.CYAN).a(" FILE").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("6").fg(Ansi.Color.CYAN).a(" OBJECT").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("7").fg(Ansi.Color.CYAN).a(" ARRAY").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("8").fg(Ansi.Color.CYAN).a(" POINTER").reset());
        System.out.println(Ansi.ansi().fg(Ansi.Color.WHITE).a("9").fg(Ansi.Color.CYAN).a(" GEOPOINT").reset());
    }

}
