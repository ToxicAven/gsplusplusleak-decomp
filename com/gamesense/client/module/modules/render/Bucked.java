// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.util.world.HoleUtil;
import net.minecraft.entity.Entity;
import com.gamesense.api.util.player.social.SocialManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import java.util.function.Predicate;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.util.render.GSColor;
import java.util.Arrays;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Bucked", category = Category.Render)
public class Bucked extends Module
{
    IntegerSetting range;
    BooleanSetting self;
    BooleanSetting friend;
    BooleanSetting enemiesOnly;
    ModeSetting heightMode;
    ModeSetting renderMode;
    IntegerSetting width;
    ColorSetting color;
    
    public Bucked() {
        this.range = this.registerInteger("Range", 100, 10, 260);
        this.self = this.registerBoolean("Self", false);
        this.friend = this.registerBoolean("Friend", true);
        this.enemiesOnly = this.registerBoolean("Only Enemies", false);
        this.heightMode = this.registerMode("Height", Arrays.asList("Single", "Double"), "Single");
        this.renderMode = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
        this.width = this.registerInteger("Line Width", 2, 1, 5);
        this.color = this.registerColor("Color", new GSColor(0, 255, 0, 255));
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (Bucked.mc.field_71439_g == null || Bucked.mc.field_71441_e == null) {
            return;
        }
        final BlockPos blockPos;
        Bucked.mc.field_71441_e.field_73010_i.stream().filter(this::isValidTarget).forEach(entityPlayer -> {
            blockPos = new BlockPos(this.roundValueToCenter(entityPlayer.field_70165_t), this.roundValueToCenter(entityPlayer.field_70163_u), this.roundValueToCenter(entityPlayer.field_70161_v));
            if (!this.isSurrounded(blockPos)) {
                this.renderESP(blockPos, this.findGSColor(entityPlayer));
            }
        });
    }
    
    private boolean isValidTarget(final EntityPlayer entityPlayer) {
        return entityPlayer != null && !entityPlayer.field_70128_L && entityPlayer.func_110143_aJ() > 0.0f && (!this.enemiesOnly.getValue() || SocialManager.isEnemy(entityPlayer.func_70005_c_())) && entityPlayer.func_70032_d((Entity)Bucked.mc.field_71439_g) <= this.range.getValue() && (this.self.getValue() || entityPlayer != Bucked.mc.field_71439_g) && (this.friend.getValue() || !SocialManager.isFriend(entityPlayer.func_70005_c_()));
    }
    
    private boolean isSurrounded(final BlockPos blockPos) {
        return HoleUtil.isHole(blockPos, true, false).getType() != HoleUtil.HoleType.NONE;
    }
    
    private void renderESP(final BlockPos blockPos, final GSColor color) {
        final int upValue = this.heightMode.getValue().equalsIgnoreCase("Double") ? 2 : 1;
        final GSColor gsColor1 = new GSColor(color, 255);
        final GSColor gsColor2 = new GSColor(color, 50);
        final String s = this.renderMode.getValue();
        switch (s) {
            case "Both": {
                RenderUtil.drawBox(blockPos, upValue, gsColor2, 63);
                RenderUtil.drawBoundingBox(blockPos, upValue, this.width.getValue(), gsColor1);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(blockPos, upValue, this.width.getValue(), gsColor1);
                break;
            }
            case "Fill": {
                RenderUtil.drawBox(blockPos, upValue, gsColor2, 63);
                break;
            }
        }
    }
    
    private GSColor findGSColor(final EntityPlayer entityPlayer) {
        if (SocialManager.isFriend(entityPlayer.func_70005_c_())) {
            return ModuleManager.getModule(ColorMain.class).getFriendGSColor();
        }
        if (SocialManager.isEnemy(entityPlayer.func_70005_c_())) {
            return ModuleManager.getModule(ColorMain.class).getEnemyGSColor();
        }
        return this.color.getValue();
    }
    
    private double roundValueToCenter(final double inputVal) {
        double roundVal = (double)Math.round(inputVal);
        if (roundVal > inputVal) {
            roundVal -= 0.5;
        }
        else if (roundVal <= inputVal) {
            roundVal += 0.5;
        }
        return roundVal;
    }
}
