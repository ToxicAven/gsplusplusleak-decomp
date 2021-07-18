// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.hud;

import com.lukflug.panelstudio.Interface;
import com.gamesense.client.clickgui.GameSenseGUI;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.util.Comparator;
import net.minecraft.entity.EntityLivingBase;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import java.awt.Point;
import net.minecraft.entity.Entity;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.api.util.player.social.SocialManager;
import java.awt.Color;
import com.lukflug.panelstudio.theme.Theme;
import com.gamesense.api.util.render.GSColor;
import net.minecraft.entity.player.EntityPlayer;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.HUDModule;

@Module.Declaration(name = "TargetInfo", category = Category.HUD)
@Declaration(posX = 0, posZ = 150)
public class TargetInfo extends HUDModule
{
    IntegerSetting range;
    ColorSetting backgroundColor;
    ColorSetting outlineColor;
    public static EntityPlayer targetPlayer;
    
    public TargetInfo() {
        this.range = this.registerInteger("Range", 100, 10, 260);
        this.backgroundColor = this.registerColor("Background", new GSColor(0, 0, 0, 255));
        this.outlineColor = this.registerColor("Outline", new GSColor(255, 0, 0, 255));
    }
    
    @Override
    public void populate(final Theme theme) {
        this.component = new TargetInfoComponent(theme);
    }
    
    private Color getNameColor(final EntityPlayer entityPlayer) {
        if (SocialManager.isFriend(entityPlayer.func_70005_c_())) {
            return new GSColor(ModuleManager.getModule(ColorMain.class).getFriendGSColor(), 255);
        }
        if (SocialManager.isEnemy(entityPlayer.func_70005_c_())) {
            return new GSColor(ModuleManager.getModule(ColorMain.class).getEnemyGSColor(), 255);
        }
        return new GSColor(255, 255, 255, 255);
    }
    
    private Color getHealthColor(final EntityPlayer entityPlayer) {
        int health = (int)(entityPlayer.func_110143_aJ() + entityPlayer.func_110139_bj());
        if (health > 36) {
            health = 36;
        }
        if (health < 0) {
            health = 0;
        }
        final int red = (int)(255.0 - health * 7.0833);
        final int green = 255 - red;
        return new Color(red, green, 0, 100);
    }
    
    private static Color getDistanceColor(final EntityPlayer entityPlayer) {
        int distance = (int)entityPlayer.func_70032_d((Entity)TargetInfo.mc.field_71439_g);
        if (distance > 50) {
            distance = 50;
        }
        final int red = (int)(255.0 - distance * 5.1);
        final int green = 255 - red;
        return new Color(red, green, 0, 100);
    }
    
    public static boolean isRenderingEntity(final EntityPlayer entityPlayer) {
        return TargetInfo.targetPlayer == entityPlayer;
    }
    
    private class TargetInfoComponent extends HUDComponent
    {
        public TargetInfoComponent(final Theme theme) {
            super(TargetInfo.this.getName(), theme.getPanelRenderer(), TargetInfo.this.position);
        }
        
        @Override
        public void render(final Context context) {
            super.render(context);
            if (TargetInfo.mc.field_71439_g != null && TargetInfo.mc.field_71439_g.field_70173_aa >= 10) {
                final EntityPlayer entityPlayer = (EntityPlayer)TargetInfo.mc.field_71441_e.field_72996_f.stream().filter(entity -> entity instanceof EntityPlayer).filter(entity -> entity != TargetInfo.mc.field_71439_g).map(entity -> entity).min(Comparator.comparing(c -> TargetInfo.mc.field_71439_g.func_70032_d(c))).orElse(null);
                if (entityPlayer != null && entityPlayer.func_70032_d((Entity)TargetInfo.mc.field_71439_g) <= TargetInfo.this.range.getValue()) {
                    final Color background = new GSColor(TargetInfo.this.backgroundColor.getValue(), 100);
                    context.getInterface().fillRect(context.getRect(), background, background, background, background);
                    final Color outline = new GSColor(TargetInfo.this.outlineColor.getValue(), 255);
                    context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(context.getSize().width, 1)), outline, outline, outline, outline);
                    context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(1, context.getSize().height)), outline, outline, outline, outline);
                    context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - 1, context.getPos().y), new Dimension(1, context.getSize().height)), outline, outline, outline, outline);
                    context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x, context.getPos().y + context.getSize().height - 1), new Dimension(context.getSize().width, 1)), outline, outline, outline, outline);
                    final String name = entityPlayer.func_70005_c_();
                    final Color nameColor = TargetInfo.this.getNameColor(entityPlayer);
                    context.getInterface().drawString(new Point(context.getPos().x + 2, context.getPos().y + 2), name, nameColor);
                    final int healthVal = (int)(entityPlayer.func_110143_aJ() + entityPlayer.func_110139_bj());
                    final Color healthBox = TargetInfo.this.getHealthColor(entityPlayer);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + 32, context.getPos().y + 12, (int)(healthVal * 1.9444), 15), healthBox, healthBox, healthBox, healthBox);
                    final int distanceVal = (int)entityPlayer.func_70032_d((Entity)TargetInfo.mc.field_71439_g);
                    int width = (int)(distanceVal * 1.38);
                    if (width > 69) {
                        width = 69;
                    }
                    final Color distanceBox = getDistanceColor(entityPlayer);
                    context.getInterface().fillRect(new Rectangle(context.getPos().x + 32, context.getPos().y + 27, width, 15), distanceBox, distanceBox, distanceBox, distanceBox);
                    GameSenseGUI.renderEntity((EntityLivingBase)(TargetInfo.targetPlayer = entityPlayer), new Point(context.getPos().x + 17, context.getPos().y + 40), 15);
                    final String health = "Health: " + healthVal;
                    final Color healthColor = new Color(255, 255, 255, 255);
                    context.getInterface().drawString(new Point(context.getPos().x + 33, context.getPos().y + 14), health, healthColor);
                    final String distance = "Distance: " + distanceVal;
                    final Color distanceColor = new Color(255, 255, 255, 255);
                    context.getInterface().drawString(new Point(context.getPos().x + 33, context.getPos().y + 29), distance, distanceColor);
                }
            }
        }
        
        @Override
        public int getWidth(final Interface inter) {
            return 102;
        }
        
        @Override
        public void getHeight(final Context context) {
            context.setHeight(43);
        }
    }
}
