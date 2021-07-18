// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import com.gamesense.api.util.render.RenderUtil;
import java.util.function.BiConsumer;
import com.gamesense.api.event.events.RenderEvent;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import com.gamesense.api.util.world.HoleUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.player.PlayerUtil;
import com.google.common.collect.Sets;
import java.util.Arrays;
import com.gamesense.api.util.render.GSColor;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.concurrent.ConcurrentHashMap;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "HoleESP", category = Category.Render)
public class HoleESP extends Module
{
    public IntegerSetting range;
    ModeSetting customHoles;
    ModeSetting type;
    ModeSetting mode;
    BooleanSetting hideOwn;
    BooleanSetting flatOwn;
    DoubleSetting slabHeight;
    IntegerSetting width;
    ColorSetting bedrockColor;
    ColorSetting obsidianColor;
    ColorSetting customColor;
    IntegerSetting ufoAlpha;
    private ConcurrentHashMap<AxisAlignedBB, GSColor> holes;
    
    public HoleESP() {
        this.range = this.registerInteger("Range", 5, 1, 20);
        this.customHoles = this.registerMode("Show", Arrays.asList("Single", "Double", "Custom"), "Single");
        this.type = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
        this.mode = this.registerMode("Mode", Arrays.asList("Air", "Ground", "Flat", "Slab", "Double"), "Air");
        this.hideOwn = this.registerBoolean("Hide Own", false);
        this.flatOwn = this.registerBoolean("Flat Own", false);
        this.slabHeight = this.registerDouble("Slab Height", 0.5, 0.1, 1.5);
        this.width = this.registerInteger("Width", 1, 1, 10);
        this.bedrockColor = this.registerColor("Bedrock Color", new GSColor(0, 255, 0));
        this.obsidianColor = this.registerColor("Obsidian Color", new GSColor(255, 0, 0));
        this.customColor = this.registerColor("Custom Color", new GSColor(0, 0, 255));
        this.ufoAlpha = this.registerInteger("UFOAlpha", 255, 0, 255);
    }
    
    @Override
    public void onUpdate() {
        if (HoleESP.mc.field_71439_g == null || HoleESP.mc.field_71441_e == null) {
            return;
        }
        if (this.holes == null) {
            this.holes = new ConcurrentHashMap<AxisAlignedBB, GSColor>();
        }
        else {
            this.holes.clear();
        }
        final int range = (int)Math.ceil(this.range.getValue());
        final HashSet<BlockPos> possibleHoles = (HashSet<BlockPos>)Sets.newHashSet();
        final List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), (float)range, range, false, true, 0);
        for (final BlockPos pos2 : blockPosList) {
            if (!HoleESP.mc.field_71441_e.func_180495_p(pos2).func_177230_c().equals(Blocks.field_150350_a)) {
                continue;
            }
            if (HoleESP.mc.field_71441_e.func_180495_p(pos2.func_177982_a(0, -1, 0)).func_177230_c().equals(Blocks.field_150350_a)) {
                continue;
            }
            if (!HoleESP.mc.field_71441_e.func_180495_p(pos2.func_177982_a(0, 1, 0)).func_177230_c().equals(Blocks.field_150350_a)) {
                continue;
            }
            if (!HoleESP.mc.field_71441_e.func_180495_p(pos2.func_177982_a(0, 2, 0)).func_177230_c().equals(Blocks.field_150350_a)) {
                continue;
            }
            possibleHoles.add(pos2);
        }
        final HoleUtil.HoleInfo holeInfo;
        final HoleUtil.HoleType holeType;
        HoleUtil.BlockSafety holeSafety;
        AxisAlignedBB centreBlocks;
        GSColor colour;
        String mode;
        possibleHoles.forEach(pos -> {
            holeInfo = HoleUtil.isHole(pos, false, false);
            holeType = holeInfo.getType();
            if (holeType != HoleUtil.HoleType.NONE) {
                holeSafety = holeInfo.getSafety();
                centreBlocks = holeInfo.getCentre();
                if (centreBlocks != null) {
                    if (holeSafety == HoleUtil.BlockSafety.UNBREAKABLE) {
                        colour = new GSColor(this.bedrockColor.getValue(), 255);
                    }
                    else {
                        colour = new GSColor(this.obsidianColor.getValue(), 255);
                    }
                    if (holeType == HoleUtil.HoleType.CUSTOM) {
                        colour = new GSColor(this.customColor.getValue(), 255);
                    }
                    mode = this.customHoles.getValue();
                    if (mode.equalsIgnoreCase("Custom") && (holeType == HoleUtil.HoleType.CUSTOM || holeType == HoleUtil.HoleType.DOUBLE)) {
                        this.holes.put(centreBlocks, colour);
                    }
                    else if (mode.equalsIgnoreCase("Double") && holeType == HoleUtil.HoleType.DOUBLE) {
                        this.holes.put(centreBlocks, colour);
                    }
                    else if (holeType == HoleUtil.HoleType.SINGLE) {
                        this.holes.put(centreBlocks, colour);
                    }
                }
            }
        });
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (HoleESP.mc.field_71439_g == null || HoleESP.mc.field_71441_e == null || this.holes == null || this.holes.isEmpty()) {
            return;
        }
        this.holes.forEach(this::renderHoles);
    }
    
    private void renderHoles(final AxisAlignedBB hole, final GSColor color) {
        final String s = this.type.getValue();
        switch (s) {
            case "Outline": {
                this.renderOutline(hole, color);
                break;
            }
            case "Fill": {
                this.renderFill(hole, color);
                break;
            }
            case "Both": {
                this.renderOutline(hole, color);
                this.renderFill(hole, color);
                break;
            }
        }
    }
    
    private void renderFill(final AxisAlignedBB hole, final GSColor color) {
        final GSColor fillColor = new GSColor(color, 50);
        final int ufoAlpha = this.ufoAlpha.getValue() * 50 / 255;
        if (this.hideOwn.getValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
            return;
        }
        final String s = this.mode.getValue();
        switch (s) {
            case "Air": {
                if (this.flatOwn.getValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                    break;
                }
                RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 63);
                break;
            }
            case "Ground": {
                RenderUtil.drawBox(hole.func_72317_d(0.0, -1.0, 0.0), true, 1.0, new GSColor(fillColor, ufoAlpha), fillColor.getAlpha(), 63);
                break;
            }
            case "Flat": {
                RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                break;
            }
            case "Slab": {
                if (this.flatOwn.getValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                    break;
                }
                RenderUtil.drawBox(hole, false, this.slabHeight.getValue(), fillColor, ufoAlpha, 63);
                break;
            }
            case "Double": {
                if (this.flatOwn.getValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    RenderUtil.drawBox(hole, true, 1.0, fillColor, ufoAlpha, 1);
                    break;
                }
                RenderUtil.drawBox(hole.func_186666_e(hole.field_72337_e + 1.0), true, 2.0, fillColor, ufoAlpha, 63);
                break;
            }
        }
    }
    
    private void renderOutline(final AxisAlignedBB hole, final GSColor color) {
        final GSColor outlineColor = new GSColor(color, 255);
        if (this.hideOwn.getValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
            return;
        }
        final String s = this.mode.getValue();
        switch (s) {
            case "Air": {
                if (this.flatOwn.getValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, this.width.getValue(), outlineColor, this.ufoAlpha.getValue(), 1);
                    break;
                }
                RenderUtil.drawBoundingBox(hole, this.width.getValue(), outlineColor, this.ufoAlpha.getValue());
                break;
            }
            case "Ground": {
                RenderUtil.drawBoundingBox(hole.func_72317_d(0.0, -1.0, 0.0), this.width.getValue(), new GSColor(outlineColor, this.ufoAlpha.getValue()), outlineColor.getAlpha());
                break;
            }
            case "Flat": {
                RenderUtil.drawBoundingBoxWithSides(hole, this.width.getValue(), outlineColor, this.ufoAlpha.getValue(), 1);
                break;
            }
            case "Slab": {
                if (this.flatOwn.getValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, this.width.getValue(), outlineColor, this.ufoAlpha.getValue(), 1);
                    break;
                }
                RenderUtil.drawBoundingBox(hole.func_186666_e(hole.field_72338_b + this.slabHeight.getValue()), this.width.getValue(), outlineColor, this.ufoAlpha.getValue());
                break;
            }
            case "Double": {
                if (this.flatOwn.getValue() && hole.func_72326_a(HoleESP.mc.field_71439_g.func_174813_aQ())) {
                    RenderUtil.drawBoundingBoxWithSides(hole, this.width.getValue(), outlineColor, this.ufoAlpha.getValue(), 1);
                    break;
                }
                RenderUtil.drawBoundingBox(hole.func_186666_e(hole.field_72337_e + 1.0), this.width.getValue(), outlineColor, this.ufoAlpha.getValue());
                break;
            }
        }
    }
}
