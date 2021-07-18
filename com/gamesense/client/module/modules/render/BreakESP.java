// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import net.minecraft.client.renderer.DestroyBlockProgress;
import com.gamesense.api.util.render.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.init.Blocks;
import com.gamesense.api.event.events.RenderEvent;
import java.util.function.Predicate;
import com.gamesense.api.util.render.GSColor;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.DrawBlockDamageEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "BreakESP", category = Category.Render)
public class BreakESP extends Module
{
    ModeSetting renderType;
    IntegerSetting lineWidth;
    IntegerSetting range;
    BooleanSetting cancelAnimation;
    ColorSetting color;
    @EventHandler
    private final Listener<DrawBlockDamageEvent> drawBlockDamageEventListener;
    
    public BreakESP() {
        this.renderType = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
        this.lineWidth = this.registerInteger("Width", 1, 0, 5);
        this.range = this.registerInteger("Range", 100, 1, 200);
        this.cancelAnimation = this.registerBoolean("No Animation", true);
        this.color = this.registerColor("Color", new GSColor(0, 255, 0, 255));
        this.drawBlockDamageEventListener = new Listener<DrawBlockDamageEvent>(event -> {
            if (this.cancelAnimation.getValue()) {
                event.cancel();
            }
        }, (Predicate<DrawBlockDamageEvent>[])new Predicate[0]);
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (BreakESP.mc.field_71439_g == null || BreakESP.mc.field_71441_e == null) {
            return;
        }
        BlockPos blockPos;
        int progress;
        AxisAlignedBB axisAlignedBB;
        BreakESP.mc.field_71438_f.field_72738_E.forEach((integer, destroyBlockProgress) -> {
            if (destroyBlockProgress != null) {
                blockPos = destroyBlockProgress.func_180246_b();
                if (BreakESP.mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150350_a) {
                    if (blockPos.func_185332_f((int)BreakESP.mc.field_71439_g.field_70165_t, (int)BreakESP.mc.field_71439_g.field_70163_u, (int)BreakESP.mc.field_71439_g.field_70161_v) <= this.range.getValue()) {
                        progress = destroyBlockProgress.func_73106_e();
                        axisAlignedBB = BreakESP.mc.field_71441_e.func_180495_p(blockPos).func_185918_c((World)BreakESP.mc.field_71441_e, blockPos);
                        this.renderESP(axisAlignedBB, progress, this.color.getValue());
                    }
                }
            }
        });
    }
    
    private void renderESP(final AxisAlignedBB axisAlignedBB, final int progress, final GSColor color) {
        final GSColor fillColor = new GSColor(color, 50);
        final GSColor outlineColor = new GSColor(color, 255);
        final double centerX = axisAlignedBB.field_72340_a + (axisAlignedBB.field_72336_d - axisAlignedBB.field_72340_a) / 2.0;
        final double centerY = axisAlignedBB.field_72338_b + (axisAlignedBB.field_72337_e - axisAlignedBB.field_72338_b) / 2.0;
        final double centerZ = axisAlignedBB.field_72339_c + (axisAlignedBB.field_72334_f - axisAlignedBB.field_72339_c) / 2.0;
        final double progressValX = progress * ((axisAlignedBB.field_72336_d - centerX) / 10.0);
        final double progressValY = progress * ((axisAlignedBB.field_72337_e - centerY) / 10.0);
        final double progressValZ = progress * ((axisAlignedBB.field_72334_f - centerZ) / 10.0);
        final AxisAlignedBB axisAlignedBB2 = new AxisAlignedBB(centerX - progressValX, centerY - progressValY, centerZ - progressValZ, centerX + progressValX, centerY + progressValY, centerZ + progressValZ);
        final String s = this.renderType.getValue();
        switch (s) {
            case "Fill": {
                RenderUtil.drawBox(axisAlignedBB2, true, 0.0, fillColor, 63);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(axisAlignedBB2, this.lineWidth.getValue(), outlineColor);
                break;
            }
            case "Both": {
                RenderUtil.drawBox(axisAlignedBB2, true, 0.0, fillColor, 63);
                RenderUtil.drawBoundingBox(axisAlignedBB2, this.lineWidth.getValue(), outlineColor);
                break;
            }
        }
    }
}
