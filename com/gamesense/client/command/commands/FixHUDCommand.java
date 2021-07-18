// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import java.util.Iterator;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.command.Command;

@Declaration(name = "FixHUD", syntax = "fixhud", alias = { "fixhud", "hud", "resethud" })
public class FixHUDCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        for (final Module module : ModuleManager.getModules()) {
            if (module instanceof HUDModule) {
                ((HUDModule)module).resetPosition();
            }
        }
        MessageBus.sendCommandMessage("HUD positions reset!", true);
    }
}
