// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.gui;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.Announcer;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "HudEditor", category = Category.GUI, bind = 25, drawn = false)
public class HUDEditor extends Module
{
    public void onEnable() {
        GameSense.INSTANCE.gameSenseGUI.enterHUDEditor();
        final Announcer announcer = ModuleManager.getModule(Announcer.class);
        if (announcer.clickGui.getValue() && announcer.isEnabled() && HUDEditor.mc.field_71439_g != null) {
            if (announcer.clientSide.getValue()) {
                MessageBus.sendClientPrefixMessage(Announcer.guiMessage);
            }
            else {
                MessageBus.sendServerMessage(Announcer.guiMessage);
            }
        }
        this.disable();
    }
}
