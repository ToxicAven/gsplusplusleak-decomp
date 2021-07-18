// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.gamesense.client.command.Command;

@Declaration(name = "AutoGG", syntax = "autogg add/del [message] (use _ for spaces)", alias = { "autogg", "gg" })
public class AutoGGCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        final String main = message[0];
        final String value = message[1].replace("_", " ");
        if (main.equalsIgnoreCase("add") && !AutoGG.getAutoGgMessages().contains(value)) {
            AutoGG.addAutoGgMessage(value);
            MessageBus.sendCommandMessage("Added AutoGG message: " + value + "!", true);
        }
        else if (main.equalsIgnoreCase("del") && AutoGG.getAutoGgMessages().contains(value)) {
            AutoGG.getAutoGgMessages().remove(value);
            MessageBus.sendCommandMessage("Deleted AutoGG message: " + value + "!", true);
        }
    }
}
