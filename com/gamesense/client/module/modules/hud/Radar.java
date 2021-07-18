// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.hud;

import com.lukflug.panelstudio.Interface;
import net.minecraft.util.EnumFacing;
import java.awt.Rectangle;
import java.awt.Dimension;
import net.minecraft.entity.EntityCreature;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import java.awt.Point;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.Entity;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.api.util.player.social.SocialManager;
import java.awt.Color;
import net.minecraft.entity.player.EntityPlayer;
import com.lukflug.panelstudio.theme.Theme;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.HUDModule;

@Module.Declaration(name = "Radar", category = Category.HUD)
@Declaration(posX = 0, posZ = 200)
public class Radar extends HUDModule
{
    BooleanSetting renderPlayer;
    BooleanSetting renderMobs;
    ColorSetting playerColor;
    ColorSetting outlineColor;
    ColorSetting fillColor;
    
    public Radar() {
        this.renderPlayer = this.registerBoolean("Player", true);
        this.renderMobs = this.registerBoolean("Mobs", true);
        this.playerColor = this.registerColor("Player Color", new GSColor(0, 0, 255, 255));
        this.outlineColor = this.registerColor("Outline Color", new GSColor(255, 0, 0, 255));
        this.fillColor = this.registerColor("Fill Color", new GSColor(0, 0, 0, 255));
    }
    
    @Override
    public void populate(final Theme theme) {
        this.component = new RadarComponent(theme);
    }
    
    private Color getPlayerColor(final EntityPlayer entityPlayer) {
        if (SocialManager.isFriend(entityPlayer.func_70005_c_())) {
            return new GSColor(ModuleManager.getModule(ColorMain.class).getFriendGSColor(), 255);
        }
        if (SocialManager.isEnemy(entityPlayer.func_70005_c_())) {
            return new GSColor(ModuleManager.getModule(ColorMain.class).getEnemyGSColor(), 255);
        }
        return new GSColor(this.playerColor.getValue(), 255);
    }
    
    private Color getEntityColor(final Entity entity) {
        if (entity instanceof EntityMob || entity instanceof EntitySlime) {
            return new GSColor(255, 0, 0, 255);
        }
        if (entity instanceof EntityAnimal || entity instanceof EntitySquid) {
            return new GSColor(0, 255, 0, 255);
        }
        return new GSColor(255, 165, 0, 255);
    }
    
    private class RadarComponent extends HUDComponent
    {
        private final int maxRange = 50;
        
        public RadarComponent(final Theme theme) {
            super(Radar.this.getName(), theme.getPanelRenderer(), Radar.this.position);
        }
        
        @Override
        public void render(final Context context) {
            super.render(context);
            if (Radar.mc.field_71439_g != null && Radar.mc.field_71439_g.field_70173_aa >= 10) {
                if (Radar.this.renderPlayer.getValue()) {
                    Radar.mc.field_71441_e.field_73010_i.stream().filter(entityPlayer -> entityPlayer != Radar.mc.field_71439_g).forEach(entityPlayer -> this.renderEntityPoint((Entity)entityPlayer, Radar.this.getPlayerColor(entityPlayer), context));
                }
                if (Radar.this.renderMobs.getValue()) {
                    Radar.mc.field_71441_e.field_72996_f.stream().filter(entity -> !(entity instanceof EntityPlayer)).forEach(entity -> {
                        if (entity instanceof EntityCreature || entity instanceof EntitySlime || entity instanceof EntitySquid) {
                            this.renderEntityPoint(entity, Radar.this.getEntityColor(entity), context);
                        }
                        return;
                    });
                }
                final Color background = new GSColor(Radar.this.fillColor.getValue(), 100);
                context.getInterface().fillRect(context.getRect(), background, background, background, background);
                final Color outline = new GSColor(Radar.this.outlineColor.getValue(), 255);
                context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(context.getSize().width, 1)), outline, outline, outline, outline);
                context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(1, context.getSize().height)), outline, outline, outline, outline);
                context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - 1, context.getPos().y), new Dimension(1, context.getSize().height)), outline, outline, outline, outline);
                context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x, context.getPos().y + context.getSize().height - 1), new Dimension(context.getSize().width, 1)), outline, outline, outline, outline);
                final boolean isNorth = this.isFacing(EnumFacing.NORTH);
                final boolean isSouth = this.isFacing(EnumFacing.SOUTH);
                final boolean isEast = this.isFacing(EnumFacing.EAST);
                final boolean isWest = this.isFacing(EnumFacing.WEST);
                final Color selfColor = new Color(255, 255, 255, 255);
                final int distanceToCenter = context.getSize().height / 2;
                context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter + 3, context.getPos().y + distanceToCenter), new Point(context.getPos().x + distanceToCenter + (isEast ? 1 : 0), context.getPos().y + distanceToCenter), isEast ? outline : selfColor, isEast ? outline : selfColor);
                context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter, context.getPos().y + distanceToCenter + 3), new Point(context.getPos().x + distanceToCenter, context.getPos().y + distanceToCenter + (isSouth ? 1 : 0)), isSouth ? outline : selfColor, isSouth ? outline : selfColor);
                context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter - (isWest ? 1 : 0), context.getPos().y + distanceToCenter), new Point(context.getPos().x + distanceToCenter - 3, context.getPos().y + distanceToCenter), isWest ? outline : selfColor, isWest ? outline : selfColor);
                context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter, context.getPos().y + distanceToCenter - (isNorth ? 1 : 0)), new Point(context.getPos().x + distanceToCenter, context.getPos().y + distanceToCenter - 3), isNorth ? outline : selfColor, isNorth ? outline : selfColor);
            }
        }
        
        private boolean isFacing(final EnumFacing enumFacing) {
            return Radar.mc.field_71439_g.func_174811_aO().equals((Object)enumFacing);
        }
        
        private void renderEntityPoint(final Entity entity, final Color color, final Context context) {
            final int distanceX = this.findDistance1D(Radar.mc.field_71439_g.field_70165_t, entity.field_70165_t);
            final int distanceY = this.findDistance1D(Radar.mc.field_71439_g.field_70161_v, entity.field_70161_v);
            final int distanceToCenter = context.getSize().height / 2;
            if (distanceX > 50 || distanceY > 50 || distanceX < -50 || distanceY < -50) {
                return;
            }
            context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter + 1 + distanceX, context.getPos().y + distanceToCenter + distanceY), new Point(context.getPos().x + distanceToCenter - 1 + distanceX, context.getPos().y + distanceToCenter + distanceY), color, color);
            context.getInterface().drawLine(new Point(context.getPos().x + distanceToCenter + distanceX, context.getPos().y + distanceToCenter + 1 + distanceY), new Point(context.getPos().x + distanceToCenter + distanceX, context.getPos().y + distanceToCenter - 1 + distanceY), color, color);
        }
        
        private int findDistance1D(final double player, final double entity) {
            double player2 = player;
            double entity2 = entity;
            if (player2 < 0.0) {
                player2 *= -1.0;
            }
            if (entity2 < 0.0) {
                entity2 *= -1.0;
            }
            int value = (int)(entity2 - player2);
            if ((player > 0.0 && entity < 0.0) || (player < 0.0 && entity > 0.0)) {
                value = (int)(-1.0 * player + entity);
            }
            if ((player > 0.0 || player < 0.0) && entity < 0.0 && entity2 != player2) {
                value = (int)(-1.0 * player + entity);
            }
            if ((player < 0.0 && entity == 0.0) || (player == 0.0 && entity < 0.0)) {
                value = (int)(-1.0 * (entity2 - player2));
            }
            return value;
        }
        
        @Override
        public int getWidth(final Interface anInterface) {
            return 103;
        }
        
        @Override
        public void getHeight(final Context context) {
            context.setHeight(103);
        }
    }
}
