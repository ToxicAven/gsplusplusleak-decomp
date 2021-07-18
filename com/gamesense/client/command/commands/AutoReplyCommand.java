// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.modules.misc.AutoReply;
import com.gamesense.client.command.Command;

@Declaration(name = "AutoReply", syntax = "autoreply set [message] (use _ for spaces)", alias = { "autoreply", "reply" })
public class AutoReplyCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        final String main = message[0];
        final String value = message[1].replace("_", " ");
        if (main.equalsIgnoreCase("set")) {
            AutoReply.setReply(value);
            MessageBus.sendCommandMessage("Set AutoReply message: " + value + "!", true);
        }
    }
}
