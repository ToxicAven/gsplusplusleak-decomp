// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.config.SaveConfig;
import com.gamesense.client.command.Command;

@Declaration(name = "SaveConfig", syntax = "saveconfig", alias = { "saveconfig", "reloadconfig", "config", "saveconfiguration" })
public class SaveConfigCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        SaveConfig.init();
        MessageBus.sendCommandMessage("Config saved!", true);
    }
}
