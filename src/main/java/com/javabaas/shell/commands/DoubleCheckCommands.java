package com.javabaas.shell.commands;

import com.javabaas.shell.common.CommandContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliAvailabilityIndicator;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.stereotype.Component;

/**
 * Created by Codi on 16/7/28.
 */
@Component
public class DoubleCheckCommands implements CommandMarker {

    @Autowired
    private CommandContext context;

    @CliAvailabilityIndicator({"yes", "no"})
    public boolean isAvailable() {
        return context.isDoubleCheck();
    }

    @CliCommand(value = "yes", help = "Delete class.")
    public void yes() {
        context.confirmDoubleCheck();
    }

    @CliCommand(value = "no", help = "Delete class.")
    public void no() {
        context.cancelDoubleCheck();
    }


}
