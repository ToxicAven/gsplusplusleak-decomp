// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import java.io.IOException;
import com.gamesense.api.util.misc.MessageBus;
import java.io.File;
import java.awt.Desktop;
import com.gamesense.client.command.Command;

@Declaration(name = "OpenFolder", syntax = "openfolder", alias = { "openfolder", "config", "open", "folder" })
public class OpenFolderCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        try {
            Desktop.getDesktop().open(new File("gs++/".replace("/", "")));
            MessageBus.sendCommandMessage("Opened config folder!", true);
        }
        catch (IOException e) {
            MessageBus.sendCommandMessage("Could not open config folder!", true);
            e.printStackTrace();
        }
    }
}
