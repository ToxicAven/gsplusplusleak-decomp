// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.font.CFontRenderer;
import java.awt.Font;
import com.gamesense.client.GameSense;
import com.gamesense.client.command.Command;

@Declaration(name = "Font", syntax = "font [name] size (use _ for spaces)", alias = { "font", "setfont", "customfont", "fonts", "chatfont" })
public class FontCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        final String main = message[0].replace("_", " ");
        int value = Integer.parseInt(message[1]);
        if (value >= 21 || value <= 15) {
            value = 18;
        }
        (GameSense.INSTANCE.cFontRenderer = new CFontRenderer(new Font(main, 0, value), true, true)).setFontName(main);
        GameSense.INSTANCE.cFontRenderer.setFontSize(value);
        MessageBus.sendCommandMessage("Font set to: " + main.toUpperCase() + ", size " + value + "!", true);
    }
}
