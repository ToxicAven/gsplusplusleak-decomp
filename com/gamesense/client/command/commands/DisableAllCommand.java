// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import java.util.Iterator;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.command.Command;

@Declaration(name = "DisableAll", syntax = "disableall", alias = { "disableall", "stop" })
public class DisableAllCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        int count = 0;
        for (final Module module : ModuleManager.getModules()) {
            if (module.isEnabled()) {
                module.disable();
                ++count;
            }
        }
        MessageBus.sendCommandMessage("Disabled " + count + " modules!", true);
    }
}
