// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import java.util.Iterator;
import org.lwjgl.input.Keyboard;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.command.Command;

@Declaration(name = "Bind", syntax = "bind [module] key", alias = { "bind", "b", "setbind", "key" })
public class BindCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        final String main = message[0];
        final String value = message[1].toUpperCase();
        for (final Module module : ModuleManager.getModules()) {
            if (module.getName().equalsIgnoreCase(main)) {
                if (value.equalsIgnoreCase("none")) {
                    module.setBind(0);
                    MessageBus.sendCommandMessage("Module " + module.getName() + " bind set to: " + value + "!", true);
                }
                else if (value.length() == 1) {
                    final int key = Keyboard.getKeyIndex(value);
                    module.setBind(key);
                    MessageBus.sendCommandMessage("Module " + module.getName() + " bind set to: " + value + "!", true);
                }
                else {
                    if (value.length() <= 1) {
                        continue;
                    }
                    MessageBus.sendCommandMessage(this.getSyntax(), true);
                }
            }
        }
    }
}
