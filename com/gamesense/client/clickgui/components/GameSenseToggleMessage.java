// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.clickgui.components;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.theme.Renderer;
import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.FocusableComponent;

public class GameSenseToggleMessage extends FocusableComponent
{
    protected final Module module;
    
    public GameSenseToggleMessage(final Renderer renderer, final Module module) {
        super("Toggle Msgs", null, renderer);
        this.module = module;
    }
    
    @Override
    public void render(final Context context) {
        super.render(context);
        this.renderer.renderTitle(context, this.title, this.hasFocus(context), this.module.isToggleMsg());
    }
    
    @Override
    public void handleButton(final Context context, final int button) {
        super.handleButton(context, button);
        if (button == 0 && context.isClicked()) {
            this.module.setToggleMsg(!this.module.isToggleMsg());
        }
    }
}
