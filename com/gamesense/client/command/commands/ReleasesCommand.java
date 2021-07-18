// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.io.IOException;
import com.gamesense.api.util.misc.MessageBus;
import java.awt.Desktop;
import java.net.URL;
import com.gamesense.client.command.Command;

@Declaration(name = "Releases", syntax = "releases", alias = { "releases", "release", "updateversion" })
public class ReleasesCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        try {
            final URL url = new URL("https://github.com/IUDevman/gamesense-client/releases");
            try {
                Desktop.getDesktop().browse(url.toURI());
                MessageBus.sendCommandMessage("Opened a link to the releases page!", true);
            }
            catch (IOException | URISyntaxException ex2) {
                final Exception ex;
                final Exception e = ex;
                e.printStackTrace();
                MessageBus.sendCommandMessage("Failed to open a link to the releases page!", true);
            }
        }
        catch (MalformedURLException e2) {
            e2.printStackTrace();
            MessageBus.sendCommandMessage("Failed to open a link to the releases page!", true);
        }
    }
}
