package com.javabaas.shell.commands;

import com.javabaas.shell.common.CommandContext;
import com.javabaas.shell.util.PropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Staryet on 15/8/20.
 * <p/>
 * 应用状态命令
 */
@Component
public class ApiStatCommands implements CommandMarker {

    @Autowired
    private CommandContext context;
    @Autowired
    private PropertiesUtil propertiesUtil;
    @Resource(name = "MasterRestTemplate")
    private RestTemplate rest;

    @CliAvailabilityIndicator({"stat"})
    public boolean isAvailable() {
        return context.getCurrentApp() != null;
    }

    @CliCommand(value = "stat", help = "Show ApiStat.")
    public String stat() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = new GregorianCalendar();
        Date to = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date from = calendar.getTime();
        String fromString = simpleDateFormat.format(from);
        String toString = simpleDateFormat.format(to);
        //显示列表
        String result = rest.getForObject(propertiesUtil.getHost() + "master/apiStat?from={from}&to={to}", String.class, fromString, toString);
        System.out.println(result);
        return null;
    }

}
