// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.clickgui.components;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.settings.KeybindSetting;
import com.lukflug.panelstudio.theme.Renderer;
import com.lukflug.panelstudio.settings.KeybindComponent;

public class GameSenseKeybind extends KeybindComponent
{
    public GameSenseKeybind(final Renderer renderer, final KeybindSetting keybind) {
        super(renderer, keybind);
    }
    
    @Override
    public void handleKey(final Context context, final int scancode) {
        context.setHeight(this.renderer.getHeight(false));
        if (this.hasFocus(context) && (scancode == 211 || scancode == 14)) {
            this.keybind.setKey(0);
            this.releaseFocus();
            return;
        }
        super.handleKey(context, scancode);
    }
}
