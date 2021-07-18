// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.HUDModule;

@Module.Declaration(name = "Watermark", category = Category.HUD)
@Declaration(posX = 0, posZ = 0)
public class Watermark extends HUDModule
{
    ColorSetting color;
    
    public Watermark() {
        this.color = this.registerColor("Color", new GSColor(255, 0, 0, 255));
    }
    
    @Override
    public void populate(final Theme theme) {
        this.component = new ListComponent(this.getName(), theme.getPanelRenderer(), this.position, new WatermarkList());
    }
    
    private class WatermarkList implements HUDList
    {
        @Override
        public int getSize() {
            return 1;
        }
        
        @Override
        public String getItem(final int index) {
            return "gs++ p2.3.1.c4";
        }
        
        @Override
        public Color getItemColor(final int index) {
            return Watermark.this.color.getValue();
        }
        
        @Override
        public boolean sortUp() {
            return false;
        }
        
        @Override
        public boolean sortRight() {
            return false;
        }
    }
}
