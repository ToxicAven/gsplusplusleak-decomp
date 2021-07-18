// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.render.RenderUtil;
import java.util.function.Predicate;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "HitSpheres", category = Category.Render)
public class HitSpheres extends Module
{
    IntegerSetting range;
    DoubleSetting lineWidth;
    IntegerSetting slices;
    IntegerSetting stacks;
    
    public HitSpheres() {
        this.range = this.registerInteger("Range", 100, 10, 260);
        this.lineWidth = this.registerDouble("Line Width", 2.0, 1.0, 5.0);
        this.slices = this.registerInteger("Slices", 20, 10, 30);
        this.stacks = this.registerInteger("Stacks", 15, 10, 20);
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        final double posX;
        final double posY;
        final double posZ;
        final GSColor color;
        HitSpheres.mc.field_71441_e.field_73010_i.stream().filter(this::isValidPlayer).forEach(entityPlayer -> {
            posX = entityPlayer.field_70142_S + (entityPlayer.field_70165_t - entityPlayer.field_70142_S) * HitSpheres.mc.field_71428_T.field_194147_b;
            posY = entityPlayer.field_70137_T + (entityPlayer.field_70163_u - entityPlayer.field_70137_T) * HitSpheres.mc.field_71428_T.field_194147_b;
            posZ = entityPlayer.field_70136_U + (entityPlayer.field_70161_v - entityPlayer.field_70136_U) * HitSpheres.mc.field_71428_T.field_194147_b;
            color = this.findRenderColor(entityPlayer);
            RenderUtil.drawSphere(posX, posY, posZ, 6.0f, this.slices.getValue(), this.stacks.getValue(), this.lineWidth.getValue().floatValue(), color);
        });
    }
    
    private boolean isValidPlayer(final EntityPlayer entityPlayer) {
        return entityPlayer != HitSpheres.mc.field_71439_g && entityPlayer.func_70032_d((Entity)HitSpheres.mc.field_71439_g) <= this.range.getValue();
    }
    
    private GSColor findRenderColor(final EntityPlayer entityPlayer) {
        final String name = entityPlayer.func_70005_c_();
        final double distance = HitSpheres.mc.field_71439_g.func_70032_d((Entity)entityPlayer);
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        if (SocialManager.isFriend(name)) {
            return colorMain.getFriendGSColor();
        }
        if (distance >= 8.0) {
            return new GSColor(0, 255, 0, 255);
        }
        if (distance < 8.0) {
            return new GSColor(255, (int)(HitSpheres.mc.field_71439_g.func_70032_d((Entity)entityPlayer) * 255.0f / 150.0f), 0, 255);
        }
        if (SocialManager.isEnemy(name)) {
            return colorMain.getEnemyGSColor();
        }
        return new GSColor(1, 1, 1, 255);
    }
}
