// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import java.util.Map;
import com.gamesense.api.util.render.RenderUtil;
import java.util.function.Consumer;
import java.util.Comparator;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.state.IBlockState;
import com.gamesense.api.util.world.combat.DamageUtil;
import net.minecraft.init.Blocks;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import com.gamesense.api.event.events.RenderEvent;
import java.util.Optional;
import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import com.gamesense.api.util.world.HoleUtil;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.gamesense.api.util.world.EntityUtil;
import net.minecraft.entity.Entity;
import com.gamesense.api.util.render.GSColor;
import java.util.Arrays;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import java.util.HashMap;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "CityESP", category = Category.Render)
public class CityESP extends Module
{
    IntegerSetting range;
    IntegerSetting down;
    IntegerSetting sides;
    IntegerSetting depth;
    DoubleSetting minDamage;
    DoubleSetting maxDamage;
    BooleanSetting ignoreCrystals;
    ModeSetting targetMode;
    ModeSetting selectMode;
    ModeSetting renderMode;
    IntegerSetting width;
    ColorSetting color;
    private final HashMap<EntityPlayer, List<BlockPos>> cityable;
    private int oldSlot;
    private boolean packetMined;
    private BlockPos coordsPacketMined;
    
    public CityESP() {
        this.range = this.registerInteger("Range", 20, 1, 30);
        this.down = this.registerInteger("Down", 1, 0, 3);
        this.sides = this.registerInteger("Sides", 1, 0, 4);
        this.depth = this.registerInteger("Depth", 3, 0, 10);
        this.minDamage = this.registerDouble("Min Damage", 5.0, 0.0, 10.0);
        this.maxDamage = this.registerDouble("Max Self Damage", 7.0, 0.0, 20.0);
        this.ignoreCrystals = this.registerBoolean("Ignore Crystals", true);
        this.targetMode = this.registerMode("Target", Arrays.asList("Single", "All"), "Single");
        this.selectMode = this.registerMode("Select", Arrays.asList("Closest", "All"), "Closest");
        this.renderMode = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both"), "Both");
        this.width = this.registerInteger("Width", 1, 1, 10);
        this.color = this.registerColor("Color", new GSColor(102, 51, 153));
        this.cityable = new HashMap<EntityPlayer, List<BlockPos>>();
        this.packetMined = false;
        this.coordsPacketMined = new BlockPos(-1, -1, -1);
    }
    
    @Override
    public void onUpdate() {
        if (CityESP.mc.field_71439_g == null || CityESP.mc.field_71441_e == null) {
            return;
        }
        this.cityable.clear();
        final List<EntityPlayer> players = (List<EntityPlayer>)CityESP.mc.field_71441_e.field_73010_i.stream().filter(entityPlayer -> entityPlayer.func_70068_e((Entity)CityESP.mc.field_71439_g) <= this.range.getValue() * this.range.getValue()).filter(entityPlayer -> !EntityUtil.basicChecksEntity(entityPlayer)).collect(Collectors.toList());
        for (final EntityPlayer player : players) {
            if (player == CityESP.mc.field_71439_g) {
                continue;
            }
            List<BlockPos> blocks = EntityUtil.getBlocksIn((Entity)player);
            if (blocks.size() == 0) {
                continue;
            }
            int minY = Integer.MAX_VALUE;
            for (final BlockPos block : blocks) {
                final int y = block.func_177956_o();
                if (y < minY) {
                    minY = y;
                }
            }
            if (player.field_70163_u % 1.0 > 0.2) {
                ++minY;
            }
            final int finalMinY = minY;
            blocks = blocks.stream().filter(blockPos -> blockPos.func_177956_o() == finalMinY).collect((Collector<? super Object, ?, List<BlockPos>>)Collectors.toList());
            final Optional<BlockPos> any = blocks.stream().findAny();
            if (!any.isPresent()) {
                continue;
            }
            final HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(any.get(), false, true);
            if (holeInfo.getType() == HoleUtil.HoleType.NONE) {
                continue;
            }
            if (holeInfo.getSafety() == HoleUtil.BlockSafety.UNBREAKABLE) {
                continue;
            }
            final List<BlockPos> sides = new ArrayList<BlockPos>();
            for (final BlockPos block2 : blocks) {
                sides.addAll(this.cityableSides(block2, HoleUtil.getUnsafeSides(block2).keySet(), player));
            }
            if (sides.size() <= 0) {
                continue;
            }
            this.cityable.put(player, sides);
        }
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        final AtomicBoolean noRender = new AtomicBoolean(false);
        final AtomicBoolean atomicBoolean;
        this.cityable.entrySet().stream().sorted((entry, entry1) -> (int)entry.getKey().func_70068_e((Entity)entry1.getKey())).forEach(entry -> {
            if (!atomicBoolean.get()) {
                this.renderBoxes(entry.getValue());
                if (this.targetMode.getValue().equalsIgnoreCase("All")) {
                    atomicBoolean.set(true);
                }
            }
        });
    }
    
    private List<BlockPos> cityableSides(final BlockPos centre, final Set<HoleUtil.BlockOffset> weakSides, final EntityPlayer player) {
        final List<BlockPos> cityableSides = new ArrayList<BlockPos>();
        final HashMap<BlockPos, HoleUtil.BlockOffset> directions = new HashMap<BlockPos, HoleUtil.BlockOffset>();
        for (final HoleUtil.BlockOffset weakSide : weakSides) {
            final BlockPos pos = weakSide.offset(centre);
            if (CityESP.mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150350_a) {
                directions.put(pos, weakSide);
            }
        }
        BlockPos pos2;
        BlockPos pos3;
        List<BlockPos> square;
        IBlockState holder;
        final Iterator<BlockPos> iterator2;
        BlockPos pos4;
        final List<BlockPos> list;
        directions.forEach((blockPos, blockOffset) -> {
            if (blockOffset == HoleUtil.BlockOffset.DOWN) {
                return;
            }
            else {
                pos2 = blockOffset.left(blockPos.func_177979_c((int)this.down.getValue()), this.sides.getValue());
                pos3 = blockOffset.forward(blockOffset.right(blockPos, this.sides.getValue()), this.depth.getValue());
                square = EntityUtil.getSquare(pos2, pos3);
                holder = CityESP.mc.field_71441_e.func_180495_p(blockPos);
                CityESP.mc.field_71441_e.func_175698_g(blockPos);
                square.iterator();
                while (iterator2.hasNext()) {
                    pos4 = iterator2.next();
                    if (this.canPlaceCrystal(pos4.func_177977_b(), this.ignoreCrystals.getValue()) && DamageUtil.calculateDamage(pos4.func_177958_n() + 0.5, pos4.func_177956_o(), pos4.func_177952_p() + 0.5, (Entity)player) >= this.minDamage.getValue()) {
                        if (DamageUtil.calculateDamage(pos4.func_177958_n() + 0.5, pos4.func_177956_o(), pos4.func_177952_p() + 0.5, (Entity)CityESP.mc.field_71439_g) <= this.maxDamage.getValue()) {
                            list.add(blockPos);
                            break;
                        }
                        else {
                            break;
                        }
                    }
                }
                CityESP.mc.field_71441_e.func_175656_a(blockPos, holder);
                return;
            }
        });
        return cityableSides;
    }
    
    private boolean canPlaceCrystal(final BlockPos blockPos, final boolean ignoreCrystal) {
        final BlockPos boost = blockPos.func_177982_a(0, 1, 0);
        final BlockPos boost2 = blockPos.func_177982_a(0, 2, 0);
        final AxisAlignedBB axisAlignedBB = new AxisAlignedBB(boost, boost2);
        if (CityESP.mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150357_h && CityESP.mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150343_Z) {
            return false;
        }
        if (CityESP.mc.field_71441_e.func_180495_p(boost).func_177230_c() != Blocks.field_150350_a) {
            return false;
        }
        if (CityESP.mc.field_71441_e.func_180495_p(boost2).func_177230_c() != Blocks.field_150350_a) {
            return false;
        }
        if (!ignoreCrystal) {
            return CityESP.mc.field_71441_e.func_72872_a((Class)Entity.class, axisAlignedBB).isEmpty();
        }
        final List<Entity> entityList = (List<Entity>)CityESP.mc.field_71441_e.func_72872_a((Class)Entity.class, axisAlignedBB);
        entityList.removeIf(entity -> entity instanceof EntityEnderCrystal);
        return entityList.isEmpty();
    }
    
    private void renderBoxes(final List<BlockPos> blockPosList) {
        final String s = this.selectMode.getValue();
        switch (s) {
            case "Closest": {
                blockPosList.stream().min(Comparator.comparing(blockPos -> blockPos.func_177954_c((double)(int)CityESP.mc.field_71439_g.field_70165_t, (double)(int)CityESP.mc.field_71439_g.field_70163_u, (double)(int)CityESP.mc.field_71439_g.field_70161_v))).ifPresent((Consumer<? super Object>)this::renderBox);
                break;
            }
            case "All": {
                for (final BlockPos blockPos2 : blockPosList) {
                    this.renderBox(blockPos2);
                }
                break;
            }
        }
    }
    
    private void renderBox(final BlockPos blockPos) {
        final GSColor gsColor1 = new GSColor(this.color.getValue(), 255);
        final GSColor gsColor2 = new GSColor(this.color.getValue(), 50);
        final String s = this.renderMode.getValue();
        switch (s) {
            case "Both": {
                RenderUtil.drawBox(blockPos, 1.0, gsColor2, 63);
                RenderUtil.drawBoundingBox(blockPos, 1.0, this.width.getValue(), gsColor1);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(blockPos, 1.0, this.width.getValue(), gsColor1);
                break;
            }
            case "Fill": {
                RenderUtil.drawBox(blockPos, 1.0, gsColor2, 63);
                break;
            }
        }
    }
}
