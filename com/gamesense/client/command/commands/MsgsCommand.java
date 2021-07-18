// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import com.gamesense.client.module.Module;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.command.Command;

@Declaration(name = "Msgs", syntax = "msgs [module]", alias = { "msgs", "togglemsgs", "showmsgs", "messages" })
public class MsgsCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        final String main = message[0];
        final Module module = ModuleManager.getModule(main);
        if (module == null) {
            MessageBus.sendCommandMessage(this.getSyntax(), true);
            return;
        }
        if (module.isToggleMsg()) {
            module.setToggleMsg(false);
            MessageBus.sendCommandMessage("Module " + module.getName() + " message toggle set to: FALSE!", true);
        }
        else if (!module.isToggleMsg()) {
            module.setToggleMsg(true);
            MessageBus.sendCommandMessage("Module " + module.getName() + " message toggle set to: TRUE!", true);
        }
    }
}
