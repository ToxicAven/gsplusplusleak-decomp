// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import com.gamesense.api.util.render.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;
import net.minecraft.util.math.RayTraceResult;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.util.render.GSColor;
import java.util.Arrays;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "BlockHighlight", category = Category.Render)
public class BlockHighlight extends Module
{
    ModeSetting renderLook;
    ModeSetting renderType;
    IntegerSetting lineWidth;
    ColorSetting renderColor;
    private int lookInt;
    
    public BlockHighlight() {
        this.renderLook = this.registerMode("Render", Arrays.asList("Block", "Side"), "Block");
        this.renderType = this.registerMode("Type", Arrays.asList("Outline", "Fill", "Both"), "Outline");
        this.lineWidth = this.registerInteger("Width", 1, 1, 5);
        this.renderColor = this.registerColor("Color", new GSColor(255, 0, 0, 255));
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        final RayTraceResult rayTraceResult = BlockHighlight.mc.field_71476_x;
        if (rayTraceResult == null) {
            return;
        }
        final EnumFacing enumFacing = BlockHighlight.mc.field_71476_x.field_178784_b;
        if (enumFacing == null) {
            return;
        }
        final GSColor colorWithOpacity = new GSColor(this.renderColor.getValue(), 50);
        final String s = this.renderLook.getValue();
        switch (s) {
            case "Block": {
                this.lookInt = 0;
                break;
            }
            case "Side": {
                this.lookInt = 1;
                break;
            }
        }
        if (rayTraceResult != null && rayTraceResult.field_72313_a == RayTraceResult.Type.BLOCK) {
            final BlockPos blockPos = rayTraceResult.func_178782_a();
            final AxisAlignedBB axisAlignedBB = BlockHighlight.mc.field_71441_e.func_180495_p(blockPos).func_185918_c((World)BlockHighlight.mc.field_71441_e, blockPos);
            if (axisAlignedBB != null && blockPos != null && BlockHighlight.mc.field_71441_e.func_180495_p(blockPos).func_185904_a() != Material.field_151579_a) {
                final String s2 = this.renderType.getValue();
                switch (s2) {
                    case "Outline": {
                        this.renderOutline(axisAlignedBB, this.lineWidth.getValue(), this.renderColor.getValue(), enumFacing, this.lookInt);
                        break;
                    }
                    case "Fill": {
                        this.renderFill(axisAlignedBB, colorWithOpacity, enumFacing, this.lookInt);
                        break;
                    }
                    case "Both": {
                        this.renderOutline(axisAlignedBB, this.lineWidth.getValue(), this.renderColor.getValue(), enumFacing, this.lookInt);
                        this.renderFill(axisAlignedBB, colorWithOpacity, enumFacing, this.lookInt);
                        break;
                    }
                }
            }
        }
    }
    
    public void renderOutline(final AxisAlignedBB axisAlignedBB, final int width, final GSColor color, final EnumFacing enumFacing, final int lookInt) {
        if (lookInt == 0) {
            RenderUtil.drawBoundingBox(axisAlignedBB, width, color);
        }
        else if (lookInt == 1) {
            RenderUtil.drawBoundingBoxWithSides(axisAlignedBB, width, color, this.findRenderingSide(enumFacing));
        }
    }
    
    public void renderFill(final AxisAlignedBB axisAlignedBB, final GSColor color, final EnumFacing enumFacing, final int lookInt) {
        int facing = 0;
        if (lookInt == 0) {
            facing = 63;
        }
        else if (lookInt == 1) {
            facing = this.findRenderingSide(enumFacing);
        }
        RenderUtil.drawBox(axisAlignedBB, true, 1.0, color, facing);
    }
    
    private int findRenderingSide(final EnumFacing enumFacing) {
        int facing = 0;
        if (enumFacing == EnumFacing.EAST) {
            facing = 32;
        }
        else if (enumFacing == EnumFacing.WEST) {
            facing = 16;
        }
        else if (enumFacing == EnumFacing.NORTH) {
            facing = 4;
        }
        else if (enumFacing == EnumFacing.SOUTH) {
            facing = 8;
        }
        else if (enumFacing == EnumFacing.UP) {
            facing = 2;
        }
        else if (enumFacing == EnumFacing.DOWN) {
            facing = 1;
        }
        return facing;
    }
}
