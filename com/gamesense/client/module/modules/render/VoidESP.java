// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import com.gamesense.api.util.render.RenderUtil;
import net.minecraft.util.math.Vec3i;
import com.gamesense.api.event.events.RenderEvent;
import java.util.Iterator;
import java.util.List;
import net.minecraft.init.Blocks;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.render.GSColor;
import java.util.Arrays;
import net.minecraft.util.math.BlockPos;
import io.netty.util.internal.ConcurrentSet;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "VoidESP", category = Category.Render)
public class VoidESP extends Module
{
    IntegerSetting renderDistance;
    IntegerSetting activeYValue;
    ModeSetting renderType;
    ModeSetting renderMode;
    IntegerSetting width;
    ColorSetting color;
    private ConcurrentSet<BlockPos> voidHoles;
    
    public VoidESP() {
        this.renderDistance = this.registerInteger("Distance", 10, 1, 40);
        this.activeYValue = this.registerInteger("Activate Y", 20, 0, 256);
        this.renderType = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
        this.renderMode = this.registerMode("Mode", Arrays.asList("Box", "Flat"), "Flat");
        this.width = this.registerInteger("Width", 1, 1, 10);
        this.color = this.registerColor("Color", new GSColor(255, 255, 0));
    }
    
    @Override
    public void onUpdate() {
        if (VoidESP.mc.field_71439_g.field_71093_bK == 1) {
            return;
        }
        if (VoidESP.mc.field_71439_g.func_180425_c().func_177956_o() > this.activeYValue.getValue()) {
            return;
        }
        if (this.voidHoles == null) {
            this.voidHoles = (ConcurrentSet<BlockPos>)new ConcurrentSet();
        }
        else {
            this.voidHoles.clear();
        }
        final List<BlockPos> blockPosList = BlockUtil.getCircle(getPlayerPos(), 0, this.renderDistance.getValue(), false);
        for (final BlockPos blockPos : blockPosList) {
            if (VoidESP.mc.field_71441_e.func_180495_p(blockPos).func_177230_c().equals(Blocks.field_150357_h)) {
                continue;
            }
            if (this.isAnyBedrock(blockPos, Offsets.center)) {
                continue;
            }
            this.voidHoles.add((Object)blockPos);
        }
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (VoidESP.mc.field_71439_g == null || this.voidHoles == null) {
            return;
        }
        if (VoidESP.mc.field_71439_g.func_180425_c().func_177956_o() > this.activeYValue.getValue()) {
            return;
        }
        if (this.voidHoles.isEmpty()) {
            return;
        }
        this.voidHoles.forEach(blockPos -> {
            if (this.renderMode.getValue().equalsIgnoreCase("Box")) {
                this.drawBox(blockPos);
            }
            else {
                this.drawFlat(blockPos);
            }
            this.drawOutline(blockPos, this.width.getValue());
        });
    }
    
    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(VoidESP.mc.field_71439_g.field_70165_t), Math.floor(VoidESP.mc.field_71439_g.field_70163_u), Math.floor(VoidESP.mc.field_71439_g.field_70161_v));
    }
    
    private boolean isAnyBedrock(final BlockPos origin, final BlockPos[] offset) {
        for (final BlockPos pos : offset) {
            if (VoidESP.mc.field_71441_e.func_180495_p(origin.func_177971_a((Vec3i)pos)).func_177230_c().equals(Blocks.field_150357_h)) {
                return true;
            }
        }
        return false;
    }
    
    private void drawFlat(final BlockPos blockPos) {
        if (this.renderType.getValue().equalsIgnoreCase("Fill") || this.renderType.getValue().equalsIgnoreCase("Both")) {
            final GSColor c = new GSColor(this.color.getValue(), 50);
            if (this.renderMode.getValue().equalsIgnoreCase("Flat")) {
                RenderUtil.drawBox(blockPos, 1.0, c, 1);
            }
        }
    }
    
    private void drawBox(final BlockPos blockPos) {
        if (this.renderType.getValue().equalsIgnoreCase("Fill") || this.renderType.getValue().equalsIgnoreCase("Both")) {
            final GSColor c = new GSColor(this.color.getValue(), 50);
            RenderUtil.drawBox(blockPos, 1.0, c, 63);
        }
    }
    
    private void drawOutline(final BlockPos blockPos, final int width) {
        if (this.renderType.getValue().equalsIgnoreCase("Outline") || this.renderType.getValue().equalsIgnoreCase("Both")) {
            if (this.renderMode.getValue().equalsIgnoreCase("Box")) {
                RenderUtil.drawBoundingBox(blockPos, 1.0, (float)width, this.color.getValue());
            }
            if (this.renderMode.getValue().equalsIgnoreCase("Flat")) {
                RenderUtil.drawBoundingBoxWithSides(blockPos, width, this.color.getValue(), 1);
            }
        }
    }
    
    private static class Offsets
    {
        static final BlockPos[] center;
        
        static {
            center = new BlockPos[] { new BlockPos(0, 0, 0), new BlockPos(0, 1, 0), new BlockPos(0, 2, 0) };
        }
    }
}
