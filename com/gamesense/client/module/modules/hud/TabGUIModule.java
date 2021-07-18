// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.hud;

import java.util.Iterator;
import com.lukflug.panelstudio.tabgui.TabGUIRenderer;
import com.lukflug.panelstudio.settings.Toggleable;
import com.lukflug.panelstudio.tabgui.TabGUIItem;
import com.lukflug.panelstudio.tabgui.TabGUIComponent;
import com.lukflug.panelstudio.tabgui.TabGUIContainer;
import com.lukflug.panelstudio.SettingsAnimation;
import com.lukflug.panelstudio.tabgui.TabGUI;
import com.lukflug.panelstudio.Animation;
import com.lukflug.panelstudio.theme.ColorScheme;
import com.lukflug.panelstudio.tabgui.DefaultRenderer;
import com.lukflug.panelstudio.settings.NumberSetting;
import com.lukflug.panelstudio.settings.ColorSetting;
import com.lukflug.panelstudio.theme.SettingsColorScheme;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.lukflug.panelstudio.theme.Theme;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.HUDModule;

@Module.Declaration(name = "TabGUI", category = Category.HUD)
@Declaration(posX = 10, posZ = 10)
public class TabGUIModule extends HUDModule
{
    @Override
    public void populate(final Theme theme) {
        final ClickGuiModule clickGuiModule = ModuleManager.getModule(ClickGuiModule.class);
        final TabGUIRenderer renderer = new DefaultRenderer(new SettingsColorScheme(clickGuiModule.enabledColor, clickGuiModule.backgroundColor, clickGuiModule.settingBackgroundColor, clickGuiModule.backgroundColor, clickGuiModule.fontColor, clickGuiModule.opacity), 12, 5, 200, 208, 203, 205, 28);
        final TabGUI component = new TabGUI("TabGUI", renderer, new Animation() {
            @Override
            protected int getSpeed() {
                return clickGuiModule.animationSpeed.getValue();
            }
        }, this.position, 75);
        for (final Category category : Category.values()) {
            final TabGUIContainer tab = new TabGUIContainer(category.name(), renderer, new SettingsAnimation(clickGuiModule.animationSpeed));
            component.addComponent(tab);
            for (final Module module : ModuleManager.getModulesInCategory(category)) {
                tab.addComponent(new TabGUIItem(module.getName(), module));
            }
        }
        this.component = component;
    }
}
