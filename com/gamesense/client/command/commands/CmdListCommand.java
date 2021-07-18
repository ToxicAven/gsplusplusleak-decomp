// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import java.util.Iterator;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.CommandManager;
import com.gamesense.client.command.Command;

@Declaration(name = "Commands", syntax = "commands", alias = { "commands", "cmd", "command", "commandlist", "help" })
public class CmdListCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        for (final Command command2 : CommandManager.getCommands()) {
            MessageBus.sendCommandMessage(command2.getName() + ": \"" + command2.getSyntax() + "\"!", true);
        }
    }
}
