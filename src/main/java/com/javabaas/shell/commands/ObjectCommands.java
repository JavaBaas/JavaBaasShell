package com.javabaas.shell.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.javabaas.javasdk.JBField;
import com.javabaas.javasdk.JBObject;
import com.javabaas.javasdk.JBQuery;
import com.javabaas.javasdk.JBUtils;
import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.util.DateUtil;
import com.javabaas.shell.util.FieldUtil;
import com.javabaas.shell.util.PropertiesUtil;
import com.javabaas.shell.util.table.AsciiTableRenderer;
import de.vandermeer.asciitable.v2.V2_AsciiTable;
import de.vandermeer.asciitable.v2.render.WidthFixedColumns;
import de.vandermeer.asciitable.v2.themes.V2_E_TableThemes;
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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Staryet on 15/8/21.
 * <p>
 * 对象命令
 */
@Component
public class ObjectCommands implements CommandMarker {

    @Autowired
    private CommandContext context;
    @Autowired
    private PropertiesUtil properties;
    @Resource(name = "MasterRestTemplate")
    private RestTemplate rest;

    @CliAvailabilityIndicator({"add", "del", "update", "list", "table", "url", "count", "sort"})
    public boolean isAvailable() {
        return context.getCurrentClass() != null;
    }

    @CliCommand(value = "add", help = "Add object.")
    public void add(@CliOption(key = {""}, mandatory = true, help = "Object by json.") final String string)
            throws JsonProcessingException {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        try {
            Map<String, Object> map = JBUtils.readValue(string, Map.class);
            JBObject object = new JBObject(className);
            JBUtils.copyPropertiesFromMapToJBObject(object, map);
            object.save();
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Object added.").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "update", help = "Update object.")
    public void update(@CliOption(key = {""}, mandatory = true, help = "Input") final String input)
            throws JsonProcessingException {
        context.cancelDoubleCheck();
        String[] inputs = input.split(" ");
        if (inputs.length < 2) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a("No object!").reset());
            return;
        }
        try {
            String id = inputs[0];
            String className = context.getCurrentClass();
            JBObject object = JBObject.createWithOutData(className, id);
            JBUtils.copyPropertiesFromMapToJBObject(object, JBUtils.readValue(inputs[1], Map.class));
            object.save();
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Object updated.").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "get", help = "Show object in class.")
    public void get(@CliOption(key = {""}, mandatory = true, help = "Object id.") final String id)
            throws JsonProcessingException {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        JBQuery query = new JBQuery(className);
        JBObject object = query.get(id);
        System.out.println(object);
    }

    @CliCommand(value = "list", help = "Show objects in class.")
    public void list(@CliOption(key = {""}, mandatory = false, help = "Query condition.") final String where,
                     @CliOption(key = {"skip"}, mandatory = false, unspecifiedDefaultValue = "0", specifiedDefaultValue = "0") final String skip)
            throws JsonProcessingException {
        context.cancelDoubleCheck();
        try {
            String className = context.getCurrentClass();
            JBQuery query = new JBQuery(className);
            if (!JBUtils.isEmpty(skip)) {
                query.setSkip(Integer.parseInt(skip));
            }
            if (!JBUtils.isEmpty(where)) {
                query.setWhereSting(where);
            }
            List<JBObject> list = query.find();
            list.forEach(object -> System.out.println(object));
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "table", help = "Show objects table")
    public void table(@CliOption(key = {""}, mandatory = false, help = "Query condition.") final String where,
                      @CliOption(key = {"skip"}, mandatory = false, unspecifiedDefaultValue = "0", specifiedDefaultValue = "0") final String skip,
                      @CliOption(key = {"t"}, mandatory = false, unspecifiedDefaultValue = "0", specifiedDefaultValue = "1") final String time,
                      @CliOption(key = {"p"}, mandatory = false, unspecifiedDefaultValue = "0", specifiedDefaultValue = "1") final String plat,
                      @CliOption(key = {"a"}, mandatory = false, unspecifiedDefaultValue = "0", specifiedDefaultValue = "1") final String acl,
                      @CliOption(key = {"s"}, mandatory = false, unspecifiedDefaultValue = "0", specifiedDefaultValue = "1") final String single)
            throws JsonProcessingException {
        context.cancelDoubleCheck();
        try {
            String className = context.getCurrentClass();
            JBQuery query = new JBQuery(className);
            if (!JBUtils.isEmpty(skip)) {
                query.setSkip(Integer.parseInt(skip));
            }
            if (!JBUtils.isEmpty(where)) {
                query.setWhereSting(where);
            }
            List<JBObject> list = query.find();

            //创建表格
            V2_AsciiTable at = new V2_AsciiTable();
            at.addRule();
            //控制列宽度
            WidthFixedColumns width = new WidthFixedColumns();
            List<JBField> fields = JBField.list(className);
            //整理表头
            List<Object> headers = new LinkedList<>();
            List<Object> types = new LinkedList<>();
            headers.add("id");
            types.add("<STRING>");
            width.add(34);
            if (time.equals("1")) {
                //显示时间
                headers.add("createdAt");
                types.add("<DATE>");
                width.add(21);
//                headers.add("updatedAt");
//                types.add("<DATE>");
//                width.add(21);
            }
//            if (plat.equals("1")) {
//                //显示平台
//                headers.add("createdPlat");
//                types.add("<STRING>");
//                width.add(13);
//                headers.add("updatedPlat");
//                types.add("<STRING>");
//                width.add(13);
//            }
//            if (acl.equals("1")) {
//                headers.add("ACL");
//                types.add("<ACL>");
//                width.add(20);
//            }
            //自定义字段
            fields.forEach(field -> {
                headers.add(field.getName());
                types.add(FieldUtil.getFieldType(field.getType()));
                width.add(20);
            });
            at.addRow(headers.toArray());
            at.addRow(types.toArray());
            at.addStrongRule();
            list.forEach(baasObject -> {
                List<Object> cols = new LinkedList<>();
                cols.add(baasObject.getObjectId());
                if (time.equals("1")) {
                    cols.add(DateUtil.format(Long.valueOf(baasObject.getCreatedAt())));
                }
                if (plat.equals("1")) {
                    cols.add("");
                }
//                if (acl.equals("1")) {
//                    cols.add(baasObject.getAcl());
//                }

                fields.forEach(field -> {
                    Object value = baasObject.get(field.getName());
                    if (value == null) {
                        cols.add("");
                    } else {
                        cols.add(value);
                    }
                });
                at.addRow(cols.toArray());
                at.addRule();
            });
            AsciiTableRenderer rend = new AsciiTableRenderer();
            rend.setTheme(V2_E_TableThemes.UTF_LIGHT.get());
            rend.setWidth(width);
            if (single.equals("0")) {
                System.out.println(rend.render(at));
            } else {
                //单行显示
                System.out.println(rend.render(at, true));
            }

        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "sort", help = "Add object.")
    public void sort() {

    }


    @CliCommand(value = "del", help = "Delete object.")
    public void delete(@CliOption(key = {""}, mandatory = true, help = "Object id.") final String id) {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        try {
            JBObject object = JBObject.createWithOutData(className, id);
            object.delete();
            System.out.println(Ansi.ansi().fg(Ansi.Color.GREEN).a("Object deleted.").reset());
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

    @CliCommand(value = "url", help = "Show object url.")
    public void url() {
        context.cancelDoubleCheck();
        String className = context.getCurrentClass();
        System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).a("GET    ").reset().a(properties.getHost() + "object/" + className));
        System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).a("GET    ").reset().a(properties.getHost() + "object/" + className + "/{id}"));
        System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).a("POST    ").reset().a(properties.getHost() + "object/" + className));
        System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).a("PUT    ").reset().a(properties.getHost() + "object/" + className + "/{id}"));
        System.out.println(Ansi.ansi().fg(Ansi.Color.CYAN).a("DELETE    ").reset().a(properties.getHost() + "object/" + className + "/{id}"));
    }

    @CliCommand(value = "count", help = "Count objects in class.")
    public void count(@CliOption(key = {""}, mandatory = false, help = "Query condition.") final String where)
            throws JsonProcessingException {
        context.cancelDoubleCheck();
        try {
            String className = context.getCurrentClass();
            JBQuery query = new JBQuery(className);
            query.setWhereSting(where);
            int count = query.count();
            System.out.println(count);
        } catch (HttpClientErrorException e) {
            System.out.println(Ansi.ansi().fg(Ansi.Color.RED).a(e.getResponseBodyAsString()).reset());
        }
    }

}
