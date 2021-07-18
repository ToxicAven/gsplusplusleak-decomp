// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.clickgui.components;

import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.theme.Renderer;
import com.lukflug.panelstudio.FocusableComponent;
import com.lukflug.panelstudio.theme.ColorScheme;
import com.lukflug.panelstudio.Component;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import net.minecraft.util.text.TextFormatting;
import com.lukflug.panelstudio.Animation;
import com.lukflug.panelstudio.settings.Toggleable;
import com.gamesense.api.setting.values.ColorSetting;
import com.lukflug.panelstudio.theme.Theme;
import com.lukflug.panelstudio.settings.ColorComponent;

public class GameSenseColor extends ColorComponent
{
    public GameSenseColor(final Theme theme, final ColorSetting setting, final Toggleable colorToggle, final Animation animation) {
        super(TextFormatting.BOLD + setting.getName(), null, theme.getContainerRenderer(), animation, theme.getComponentRenderer(), setting, false, true, colorToggle);
        if (setting != ModuleManager.getModule(ClickGuiModule.class).enabledColor) {
            this.addComponent(new SyncButton(theme.getComponentRenderer()));
        }
    }
    
    private class SyncButton extends FocusableComponent
    {
        public SyncButton(final Renderer renderer) {
            super("Sync Color", null, renderer);
        }
        
        @Override
        public void render(final Context context) {
            super.render(context);
            this.renderer.overrideColorScheme(GameSenseColor.this.overrideScheme);
            this.renderer.renderTitle(context, this.title, this.hasFocus(context), false);
            this.renderer.restoreColorScheme();
        }
        
        @Override
        public void handleButton(final Context context, final int button) {
            super.handleButton(context, button);
            final ClickGuiModule clickGuiModule = ModuleManager.getModule(ClickGuiModule.class);
            if (button == 0 && context.isClicked()) {
                GameSenseColor.this.setting.setValue(clickGuiModule.enabledColor.getColor());
                GameSenseColor.this.setting.setRainbow(clickGuiModule.enabledColor.getRainbow());
            }
        }
    }
}
