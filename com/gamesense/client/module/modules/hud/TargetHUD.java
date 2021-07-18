// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.hud;

import com.lukflug.panelstudio.Interface;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.init.Items;
import net.minecraft.util.text.TextFormatting;
import com.gamesense.client.clickgui.GameSenseGUI;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.util.Comparator;
import net.minecraft.entity.EntityLivingBase;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.hud.HUDComponent;
import net.minecraft.client.Minecraft;
import java.awt.Point;
import com.gamesense.api.util.world.EntityUtil;
import java.util.Objects;
import net.minecraft.client.network.NetHandlerPlayClient;
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

@Module.Declaration(name = "TargetHUD", category = Category.HUD)
@Declaration(posX = 0, posZ = 70)
public class TargetHUD extends HUDModule
{
    IntegerSetting range;
    ColorSetting outline;
    ColorSetting background;
    private static EntityPlayer targetPlayer;
    
    public TargetHUD() {
        this.range = this.registerInteger("Range", 100, 10, 260);
        this.outline = this.registerColor("Outline", new GSColor(255, 0, 0, 255));
        this.background = this.registerColor("Background", new GSColor(0, 0, 0, 255));
    }
    
    @Override
    public void populate(final Theme theme) {
        this.component = new TargetHUDComponent(theme);
    }
    
    private static Color getNameColor(final String playerName) {
        if (SocialManager.isFriend(playerName)) {
            return new GSColor(ModuleManager.getModule(ColorMain.class).getFriendGSColor(), 255);
        }
        if (SocialManager.isEnemy(playerName)) {
            return new GSColor(ModuleManager.getModule(ColorMain.class).getEnemyGSColor(), 255);
        }
        return new GSColor(255, 255, 255, 255);
    }
    
    private static GSColor getHealthColor(int health) {
        if (health > 36) {
            health = 36;
        }
        if (health < 0) {
            health = 0;
        }
        final int red = (int)(255.0 - health * 7.0833);
        final int green = 255 - red;
        return new GSColor(red, green, 0, 255);
    }
    
    private static boolean isValidEntity(final Entity e) {
        return e instanceof EntityPlayer && e != TargetHUD.mc.field_71439_g;
    }
    
    private static float getPing(final EntityPlayer player) {
        float ping = 0.0f;
        try {
            ping = EntityUtil.clamp((float)Objects.requireNonNull(TargetHUD.mc.func_147114_u()).func_175102_a(player.func_110124_au()).func_178853_c(), 1.0f, 300.0f);
        }
        catch (NullPointerException ex) {}
        return ping;
    }
    
    public static boolean isRenderingEntity(final EntityPlayer entityPlayer) {
        return TargetHUD.targetPlayer == entityPlayer;
    }
    
    private class TargetHUDComponent extends HUDComponent
    {
        public TargetHUDComponent(final Theme theme) {
            super(TargetHUD.this.getName(), theme.getPanelRenderer(), TargetHUD.this.position);
        }
        
        @Override
        public void render(final Context context) {
            super.render(context);
            if (TargetHUD.mc.field_71441_e != null && TargetHUD.mc.field_71439_g.field_70173_aa >= 10) {
                final EntityPlayer entityPlayer = (EntityPlayer)TargetHUD.mc.field_71441_e.field_72996_f.stream().filter(entity -> isValidEntity(entity)).map(entity -> entity).min(Comparator.comparing(c -> TargetHUD.mc.field_71439_g.func_70032_d(c))).orElse(null);
                if (entityPlayer != null && entityPlayer.func_70032_d((Entity)TargetHUD.mc.field_71439_g) <= TargetHUD.this.range.getValue()) {
                    final Color bgcolor = new GSColor(TargetHUD.this.background.getValue(), 100);
                    context.getInterface().fillRect(context.getRect(), bgcolor, bgcolor, bgcolor, bgcolor);
                    final Color color = TargetHUD.this.outline.getValue();
                    context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(context.getSize().width, 1)), color, color, color, color);
                    context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(1, context.getSize().height)), color, color, color, color);
                    context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - 1, context.getPos().y), new Dimension(1, context.getSize().height)), color, color, color, color);
                    context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x, context.getPos().y + context.getSize().height - 1), new Dimension(context.getSize().width, 1)), color, color, color, color);
                    TargetHUD.targetPlayer = entityPlayer;
                    GameSenseGUI.renderEntity((EntityLivingBase)entityPlayer, new Point(context.getPos().x + 35, context.getPos().y + 87 - (entityPlayer.func_70093_af() ? 10 : 0)), 43);
                    TargetHUD.targetPlayer = null;
                    final String playerName = entityPlayer.func_70005_c_();
                    final Color nameColor = getNameColor(playerName);
                    context.getInterface().drawString(new Point(context.getPos().x + 71, context.getPos().y + 11), TextFormatting.BOLD + playerName, nameColor);
                    final int playerHealth = (int)(entityPlayer.func_110143_aJ() + entityPlayer.func_110139_bj());
                    final Color healthColor = getHealthColor(playerHealth);
                    context.getInterface().drawString(new Point(context.getPos().x + 71, context.getPos().y + 23), TextFormatting.WHITE + "Health: " + TextFormatting.RESET + playerHealth, healthColor);
                    context.getInterface().drawString(new Point(context.getPos().x + 71, context.getPos().y + 33), "Distance: " + (int)entityPlayer.func_70032_d((Entity)TargetHUD.mc.field_71439_g), new Color(255, 255, 255));
                    String info;
                    if (entityPlayer.field_71071_by.func_70440_f(2).func_77973_b().equals(Items.field_185160_cR)) {
                        info = TextFormatting.LIGHT_PURPLE + "Wasp";
                    }
                    else if (entityPlayer.field_71071_by.func_70440_f(2).func_77973_b().equals(Items.field_151163_ad)) {
                        info = TextFormatting.RED + "Threat";
                    }
                    else if (entityPlayer.field_71071_by.func_70440_f(3).func_77973_b().equals(Items.field_190931_a)) {
                        info = TextFormatting.GREEN + "NewFag";
                    }
                    else {
                        info = TextFormatting.WHITE + "None";
                    }
                    context.getInterface().drawString(new Point(context.getPos().x + 71, context.getPos().y + 43), info + TextFormatting.WHITE + " | " + getPing(entityPlayer) + " ms", new Color(255, 255, 255));
                    String status = null;
                    Color statusColor = null;
                    for (final PotionEffect effect : entityPlayer.func_70651_bq()) {
                        if (effect.func_188419_a() == MobEffects.field_76437_t) {
                            status = "Weakness!";
                            statusColor = new Color(135, 0, 25);
                        }
                        else if (effect.func_188419_a() == MobEffects.field_76441_p) {
                            status = "Invisible!";
                            statusColor = new Color(90, 90, 90);
                        }
                        else {
                            if (effect.func_188419_a() != MobEffects.field_76420_g) {
                                continue;
                            }
                            status = "Strength!";
                            statusColor = new Color(185, 65, 185);
                        }
                    }
                    if (status != null) {
                        context.getInterface().drawString(new Point(context.getPos().x + 71, context.getPos().y + 55), TextFormatting.WHITE + "Status: " + TextFormatting.RESET + status, statusColor);
                    }
                    int xPos = context.getPos().x + 150;
                    for (final ItemStack itemStack : entityPlayer.func_184193_aE()) {
                        xPos -= 20;
                        GameSenseGUI.renderItem(itemStack, new Point(xPos, context.getPos().y + 73));
                    }
                }
            }
        }
        
        @Override
        public int getWidth(final Interface inter) {
            return 162;
        }
        
        @Override
        public void getHeight(final Context context) {
            context.setHeight(94);
        }
    }
}
