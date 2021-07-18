// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.util.Comparator;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.ModuleManager;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.HUDModule;

@Module.Declaration(name = "ArrayList", category = Category.HUD)
@Declaration(posX = 0, posZ = 200)
public class ArrayListModule extends HUDModule
{
    BooleanSetting sortUp;
    BooleanSetting sortRight;
    ColorSetting color;
    private final ModuleList list;
    
    public ArrayListModule() {
        this.sortUp = this.registerBoolean("Sort Up", true);
        this.sortRight = this.registerBoolean("Sort Right", false);
        this.color = this.registerColor("Color", new GSColor(255, 0, 0, 255));
        this.list = new ModuleList();
    }
    
    @Override
    public void populate(final Theme theme) {
        this.component = new ListComponent(this.getName(), theme.getPanelRenderer(), this.position, this.list);
    }
    
    @Override
    public void onRender() {
        this.list.activeModules.clear();
        for (final Module module2 : ModuleManager.getModules()) {
            if (module2.isEnabled() && module2.isDrawn()) {
                this.list.activeModules.add(module2);
            }
        }
        this.list.activeModules.sort(Comparator.comparing(module -> -GameSense.INSTANCE.gameSenseGUI.guiInterface.getFontWidth(module.getName() + ChatFormatting.GRAY + " " + module.getHudInfo())));
    }
    
    private class ModuleList implements HUDList
    {
        public List<Module> activeModules;
        
        private ModuleList() {
            this.activeModules = new ArrayList<Module>();
        }
        
        @Override
        public int getSize() {
            return this.activeModules.size();
        }
        
        @Override
        public String getItem(final int index) {
            final Module module = this.activeModules.get(index);
            return module.getHudInfo().equals("") ? module.getName() : (module.getName() + ChatFormatting.GRAY + " " + module.getHudInfo());
        }
        
        @Override
        public Color getItemColor(final int index) {
            final GSColor c = ArrayListModule.this.color.getValue();
            return Color.getHSBColor(c.getHue() + (ArrayListModule.this.color.getRainbow() ? (0.02f * index) : 0.0f), c.getSaturation(), c.getBrightness());
        }
        
        @Override
        public boolean sortUp() {
            return ArrayListModule.this.sortUp.isOn();
        }
        
        @Override
        public boolean sortRight() {
            return ArrayListModule.this.sortRight.isOn();
        }
    }
}
