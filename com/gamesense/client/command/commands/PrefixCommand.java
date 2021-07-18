// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.command.CommandManager;
import com.gamesense.client.command.Command;

@Declaration(name = "Prefix", syntax = "prefix value (no letters or numbers)", alias = { "prefix", "setprefix", "cmdprefix", "commandprefix" })
public class PrefixCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        final String main = message[0].toUpperCase().replaceAll("[a-zA-Z0-9]", null);
        final int size = message[0].length();
        if (main != null && size == 1) {
            CommandManager.setCommandPrefix(main);
            MessageBus.sendCommandMessage("Prefix set: \"" + main + "\"!", true);
        }
        else if (size != 1) {
            MessageBus.sendCommandMessage(this.getSyntax(), true);
        }
    }
}
