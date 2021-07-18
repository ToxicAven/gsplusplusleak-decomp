// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import net.minecraft.util.math.Vec3d;
import com.gamesense.api.util.render.RenderUtil;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.entity.Entity;
import com.gamesense.api.util.player.social.SocialManager;
import net.minecraft.entity.player.EntityPlayer;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.api.event.events.RenderEvent;
import java.util.Arrays;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Tracers", category = Category.Render)
public class Tracers extends Module
{
    IntegerSetting renderDistance;
    ModeSetting pointsTo;
    BooleanSetting colorType;
    ColorSetting nearColor;
    ColorSetting midColor;
    ColorSetting farColor;
    GSColor tracerColor;
    
    public Tracers() {
        this.renderDistance = this.registerInteger("Distance", 100, 10, 260);
        this.pointsTo = this.registerMode("Draw To", Arrays.asList("Head", "Feet"), "Feet");
        this.colorType = this.registerBoolean("Color Sync", true);
        this.nearColor = this.registerColor("Near Color", new GSColor(255, 0, 0, 255));
        this.midColor = this.registerColor("Middle Color", new GSColor(255, 255, 0, 255));
        this.farColor = this.registerColor("Far Color", new GSColor(0, 255, 0, 255));
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        final ColorMain colorMain2;
        Tracers.mc.field_71441_e.field_72996_f.stream().filter(e -> e instanceof EntityPlayer).filter(e -> e != Tracers.mc.field_71439_g).forEach(e -> {
            if (Tracers.mc.field_71439_g.func_70032_d(e) <= this.renderDistance.getValue()) {
                if (SocialManager.isFriend(e.func_70005_c_())) {
                    this.tracerColor = colorMain2.getFriendGSColor();
                }
                else if (SocialManager.isEnemy(e.func_70005_c_())) {
                    this.tracerColor = colorMain2.getEnemyGSColor();
                }
                else {
                    if (Tracers.mc.field_71439_g.func_70032_d(e) < 20.0f) {
                        this.tracerColor = this.nearColor.getValue();
                    }
                    if (Tracers.mc.field_71439_g.func_70032_d(e) >= 20.0f && Tracers.mc.field_71439_g.func_70032_d(e) < 50.0f) {
                        this.tracerColor = this.midColor.getValue();
                    }
                    if (Tracers.mc.field_71439_g.func_70032_d(e) >= 50.0f) {
                        this.tracerColor = this.farColor.getValue();
                    }
                    if (this.colorType.getValue()) {
                        this.tracerColor = this.getDistanceColor((int)Tracers.mc.field_71439_g.func_70032_d(e));
                    }
                }
                this.drawLineToEntityPlayer(e, this.tracerColor);
            }
        });
    }
    
    public void drawLineToEntityPlayer(final Entity e, final GSColor color) {
        final double[] xyz = interpolate(e);
        this.drawLine1(xyz[0], xyz[1], xyz[2], e.field_70131_O, color);
    }
    
    public static double[] interpolate(final Entity entity) {
        final double posX = interpolate(entity.field_70165_t, entity.field_70142_S);
        final double posY = interpolate(entity.field_70163_u, entity.field_70137_T);
        final double posZ = interpolate(entity.field_70161_v, entity.field_70136_U);
        return new double[] { posX, posY, posZ };
    }
    
    public static double interpolate(final double now, final double then) {
        return then + (now - then) * Tracers.mc.func_184121_ak();
    }
    
    public void drawLine1(final double posx, final double posy, final double posz, final double up, final GSColor color) {
        final Vec3d eyes = ActiveRenderInfo.getCameraPosition().func_72441_c(Tracers.mc.func_175598_ae().field_78730_l, Tracers.mc.func_175598_ae().field_78731_m, Tracers.mc.func_175598_ae().field_78728_n);
        RenderUtil.prepare();
        if (this.pointsTo.getValue().equalsIgnoreCase("Head")) {
            RenderUtil.drawLine(eyes.field_72450_a, eyes.field_72448_b, eyes.field_72449_c, posx, posy + up, posz, color);
        }
        else {
            RenderUtil.drawLine(eyes.field_72450_a, eyes.field_72448_b, eyes.field_72449_c, posx, posy, posz, color);
        }
        RenderUtil.release();
    }
    
    private GSColor getDistanceColor(int distance) {
        if (distance > 50) {
            distance = 50;
        }
        final int red = (int)(255.0 - distance * 5.1);
        final int green = 255 - red;
        return new GSColor(red, green, 0, 255);
    }
}
