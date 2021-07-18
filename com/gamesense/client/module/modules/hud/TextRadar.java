// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.hud;

import java.awt.Color;
import net.minecraft.util.text.TextFormatting;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import com.gamesense.api.util.player.social.SocialManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import java.util.Arrays;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.HUDModule;

@Module.Declaration(name = "TextRadar", category = Category.HUD)
@Declaration(posX = 0, posZ = 50)
public class TextRadar extends HUDModule
{
    ModeSetting display;
    BooleanSetting sortUp;
    BooleanSetting sortRight;
    IntegerSetting range;
    private final PlayerList list;
    
    public TextRadar() {
        this.display = this.registerMode("Display", Arrays.asList("All", "Friend", "Enemy"), "All");
        this.sortUp = this.registerBoolean("Sort Up", false);
        this.sortRight = this.registerBoolean("Sort Right", false);
        this.range = this.registerInteger("Range", 100, 1, 260);
        this.list = new PlayerList();
    }
    
    @Override
    public void populate(final Theme theme) {
        this.component = new ListComponent(this.getName(), theme.getPanelRenderer(), this.position, this.list);
    }
    
    @Override
    public void onRender() {
        this.list.players.clear();
        TextRadar.mc.field_71441_e.field_72996_f.stream().filter(e -> e instanceof EntityPlayer).filter(e -> e != TextRadar.mc.field_71439_g).forEach(e -> {
            if (TextRadar.mc.field_71439_g.func_70032_d((Entity)e) <= this.range.getValue()) {
                if (!this.display.getValue().equalsIgnoreCase("Friend") || SocialManager.isFriend(((Entity)e).func_70005_c_())) {
                    if (!this.display.getValue().equalsIgnoreCase("Enemy") || SocialManager.isEnemy(((Entity)e).func_70005_c_())) {
                        this.list.players.add(e);
                    }
                }
            }
        });
    }
    
    private class PlayerList implements HUDList
    {
        public List<EntityPlayer> players;
        
        private PlayerList() {
            this.players = new ArrayList<EntityPlayer>();
        }
        
        @Override
        public int getSize() {
            return this.players.size();
        }
        
        @Override
        public String getItem(final int index) {
            final EntityPlayer e = this.players.get(index);
            TextFormatting friendcolor;
            if (SocialManager.isFriend(e.func_70005_c_())) {
                friendcolor = ModuleManager.getModule(ColorMain.class).getFriendColor();
            }
            else if (SocialManager.isEnemy(e.func_70005_c_())) {
                friendcolor = ModuleManager.getModule(ColorMain.class).getEnemyColor();
            }
            else {
                friendcolor = TextFormatting.GRAY;
            }
            final float health = e.func_110143_aJ() + e.func_110139_bj();
            TextFormatting healthcolor;
            if (health <= 5.0f) {
                healthcolor = TextFormatting.RED;
            }
            else if (health > 5.0f && health < 15.0f) {
                healthcolor = TextFormatting.YELLOW;
            }
            else {
                healthcolor = TextFormatting.GREEN;
            }
            final float distance = TextRadar.mc.field_71439_g.func_70032_d((Entity)e);
            TextFormatting distancecolor;
            if (distance < 20.0f) {
                distancecolor = TextFormatting.RED;
            }
            else if (distance >= 20.0f && distance < 50.0f) {
                distancecolor = TextFormatting.YELLOW;
            }
            else {
                distancecolor = TextFormatting.GREEN;
            }
            return TextFormatting.GRAY + "[" + healthcolor + (int)health + TextFormatting.GRAY + "] " + friendcolor + e.func_70005_c_() + TextFormatting.GRAY + " [" + distancecolor + (int)distance + TextFormatting.GRAY + "]";
        }
        
        @Override
        public Color getItemColor(final int index) {
            return new Color(255, 255, 255);
        }
        
        @Override
        public boolean sortUp() {
            return TextRadar.this.sortUp.isOn();
        }
        
        @Override
        public boolean sortRight() {
            return TextRadar.this.sortRight.isOn();
        }
    }
}
