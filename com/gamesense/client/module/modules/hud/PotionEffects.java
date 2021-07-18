// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import net.minecraft.potion.Potion;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.PotionEffect;
import net.minecraft.client.Minecraft;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.HUDModule;

@Module.Declaration(name = "PotionEffects", category = Category.HUD)
@Declaration(posX = 0, posZ = 300)
public class PotionEffects extends HUDModule
{
    BooleanSetting sortUp;
    BooleanSetting sortRight;
    ColorSetting color;
    private final PotionList list;
    
    public PotionEffects() {
        this.sortUp = this.registerBoolean("Sort Up", false);
        this.sortRight = this.registerBoolean("Sort Right", false);
        this.color = this.registerColor("Color", new GSColor(0, 255, 0, 255));
        this.list = new PotionList();
    }
    
    @Override
    public void populate(final Theme theme) {
        this.component = new ListComponent(this.getName(), theme.getPanelRenderer(), this.position, this.list);
    }
    
    private class PotionList implements HUDList
    {
        @Override
        public int getSize() {
            return PotionEffects.mc.field_71439_g.func_70651_bq().size();
        }
        
        @Override
        public String getItem(final int index) {
            final PotionEffect effect = (PotionEffect)PotionEffects.mc.field_71439_g.func_70651_bq().toArray()[index];
            final String name = I18n.func_135052_a(effect.func_188419_a().func_76393_a(), new Object[0]);
            final int amplifier = effect.func_76458_c() + 1;
            return name + " " + amplifier + ChatFormatting.GRAY + " " + Potion.func_188410_a(effect, 1.0f);
        }
        
        @Override
        public Color getItemColor(final int index) {
            return PotionEffects.this.color.getValue();
        }
        
        @Override
        public boolean sortUp() {
            return PotionEffects.this.sortUp.isOn();
        }
        
        @Override
        public boolean sortRight() {
            return PotionEffects.this.sortRight.isOn();
        }
    }
}
