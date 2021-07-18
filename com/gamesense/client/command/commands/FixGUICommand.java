// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.client.GameSense;
import com.gamesense.client.command.Command;

@Declaration(name = "FixGUI", syntax = "fixgui", alias = { "fixgui", "gui", "resetgui" })
public class FixGUICommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        GameSense.INSTANCE.gameSenseGUI = new GameSenseGUI();
        MessageBus.sendCommandMessage("ClickGUI positions reset!", true);
    }
}
