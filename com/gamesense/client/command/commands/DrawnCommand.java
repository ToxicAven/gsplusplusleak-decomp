// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import com.gamesense.client.module.Module;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.command.Command;

@Declaration(name = "Drawn", syntax = "drawn [module]", alias = { "drawn", "shown" })
public class DrawnCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        final String main = message[0];
        final Module module = ModuleManager.getModule(main);
        if (module == null) {
            MessageBus.sendCommandMessage(this.getSyntax(), true);
            return;
        }
        if (module.isDrawn()) {
            module.setDrawn(false);
            MessageBus.sendCommandMessage("Module " + module.getName() + " drawn set to: FALSE!", true);
        }
        else if (!module.isDrawn()) {
            module.setDrawn(true);
            MessageBus.sendCommandMessage("Module " + module.getName() + " drawn set to: TRUE!", true);
        }
    }
}
