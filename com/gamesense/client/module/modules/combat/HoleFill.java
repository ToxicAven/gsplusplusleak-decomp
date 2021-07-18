// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.combat;

import net.minecraft.item.Item;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.Block;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.api.util.player.PlayerUtil;
import net.minecraft.util.NonNullList;
import java.util.Iterator;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import com.gamesense.api.util.world.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Collection;
import java.util.ArrayList;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.ItemBlock;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.api.util.player.PlacementUtil;
import java.util.Arrays;
import net.minecraft.util.math.BlockPos;
import java.util.HashMap;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "HoleFill", category = Category.Combat)
public class HoleFill extends Module
{
    ModeSetting mode;
    IntegerSetting placeDelay;
    IntegerSetting retryDelay;
    IntegerSetting bpc;
    DoubleSetting range;
    DoubleSetting playerRange;
    BooleanSetting onlyPlayer;
    BooleanSetting rotate;
    BooleanSetting autoSwitch;
    BooleanSetting offHandObby;
    BooleanSetting disableOnFinish;
    private int delayTicks;
    private int oldHandEnable;
    private boolean activedOff;
    private int obbySlot;
    private final HashMap<BlockPos, Integer> recentPlacements;
    
    public HoleFill() {
        this.mode = this.registerMode("Type", Arrays.asList("Obby", "Echest", "Both", "Web", "Plate"), "Obby");
        this.placeDelay = this.registerInteger("Delay", 2, 0, 10);
        this.retryDelay = this.registerInteger("Retry Delay", 10, 0, 50);
        this.bpc = this.registerInteger("Block pre Cycle", 2, 1, 5);
        this.range = this.registerDouble("Range", 4.0, 0.0, 10.0);
        this.playerRange = this.registerDouble("Player Range", 3.0, 1.0, 6.0);
        this.onlyPlayer = this.registerBoolean("Only Player", false);
        this.rotate = this.registerBoolean("Rotate", true);
        this.autoSwitch = this.registerBoolean("Switch", true);
        this.offHandObby = this.registerBoolean("Off Hand Obby", false);
        this.disableOnFinish = this.registerBoolean("Disable on Finish", true);
        this.delayTicks = 0;
        this.oldHandEnable = -1;
        this.recentPlacements = new HashMap<BlockPos, Integer>();
    }
    
    public void onEnable() {
        this.activedOff = false;
        PlacementUtil.onEnable();
        if (this.autoSwitch.getValue() && HoleFill.mc.field_71439_g != null) {
            this.oldHandEnable = HoleFill.mc.field_71439_g.field_71071_by.field_70461_c;
        }
        this.obbySlot = InventoryUtil.findObsidianSlot(this.offHandObby.getValue(), this.activedOff);
        if (this.obbySlot == 9) {
            this.activedOff = true;
        }
    }
    
    public void onDisable() {
        PlacementUtil.onDisable();
        if (this.autoSwitch.getValue() && HoleFill.mc.field_71439_g != null) {
            HoleFill.mc.field_71439_g.field_71071_by.field_70461_c = this.oldHandEnable;
        }
        this.recentPlacements.clear();
        if (this.offHandObby.getValue() && ModuleManager.isModuleEnabled(OffHand.class)) {
            OffHand.removeItem(0);
            this.activedOff = false;
        }
    }
    
    @Override
    public void onUpdate() {
        if (HoleFill.mc.field_71439_g == null || HoleFill.mc.field_71441_e == null) {
            this.disable();
            return;
        }
        this.recentPlacements.replaceAll((blockPos, integer) -> integer + 1);
        this.recentPlacements.values().removeIf(integer -> integer > this.retryDelay.getValue() * 2);
        if (this.delayTicks <= this.placeDelay.getValue() * 2) {
            ++this.delayTicks;
            return;
        }
        if (this.obbySlot == 9 && (!(HoleFill.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock) || !(((ItemBlock)HoleFill.mc.field_71439_g.func_184592_cb().func_77973_b()).func_179223_d() instanceof BlockObsidian))) {
            return;
        }
        if (this.autoSwitch.getValue()) {
            final int newHand = this.findRightBlock();
            if (newHand == -1) {
                return;
            }
            HoleFill.mc.field_71439_g.field_71071_by.field_70461_c = newHand;
            HoleFill.mc.field_71442_b.func_78750_j();
        }
        List<BlockPos> holePos = new ArrayList<BlockPos>(this.findHoles());
        holePos.removeAll(this.recentPlacements.keySet());
        final AtomicInteger placements = new AtomicInteger();
        holePos = holePos.stream().sorted(Comparator.comparing(blockPos -> blockPos.func_177954_c((double)(int)HoleFill.mc.field_71439_g.field_70165_t, (double)(int)HoleFill.mc.field_71439_g.field_70163_u, (double)(int)HoleFill.mc.field_71439_g.field_70161_v))).collect((Collector<? super Object, ?, List<BlockPos>>)Collectors.toList());
        final List<EntityPlayer> listPlayer = new ArrayList<EntityPlayer>(HoleFill.mc.field_71441_e.field_73010_i);
        listPlayer.removeIf(player -> EntityUtil.basicChecksEntity(player) || !this.onlyPlayer.getValue() || HoleFill.mc.field_71439_g.func_70032_d(player) > 6.0 + this.playerRange.getValue());
        final AtomicInteger atomicInteger;
        boolean output;
        boolean found;
        final List<EntityPlayer> list;
        final Iterator<EntityPlayer> iterator;
        EntityPlayer player2;
        holePos.removeIf(placePos -> {
            if (atomicInteger.get() >= this.bpc.getValue()) {
                return false;
            }
            else if (HoleFill.mc.field_71441_e.func_72839_b((Entity)null, new AxisAlignedBB(placePos)).stream().anyMatch(entity -> entity instanceof EntityPlayer)) {
                return true;
            }
            else {
                output = false;
                if (this.isHoldingRightBlock(HoleFill.mc.field_71439_g.field_71071_by.field_70461_c, HoleFill.mc.field_71439_g.func_184586_b(EnumHand.MAIN_HAND).func_77973_b()) || this.offHandObby.getValue()) {
                    found = false;
                    if (this.onlyPlayer.getValue()) {
                        list.iterator();
                        while (iterator.hasNext()) {
                            player2 = iterator.next();
                            if (player2.func_174831_c(placePos) < this.playerRange.getValue() * 2.0) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            return false;
                        }
                    }
                    if (this.placeBlock(placePos)) {
                        atomicInteger.getAndIncrement();
                        output = true;
                        this.delayTicks = 0;
                    }
                    this.recentPlacements.put(placePos, 0);
                }
                return output;
            }
        });
        if (this.disableOnFinish.getValue() && holePos.size() == 0) {
            this.disable();
        }
    }
    
    private boolean placeBlock(final BlockPos pos) {
        EnumHand handSwing = EnumHand.MAIN_HAND;
        if (this.offHandObby.getValue()) {
            final int obsidianSlot = InventoryUtil.findObsidianSlot(this.offHandObby.getValue(), this.activedOff);
            if (obsidianSlot == -1) {
                return false;
            }
            if (obsidianSlot == 9) {
                this.activedOff = true;
                if (!(HoleFill.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock) || !(((ItemBlock)HoleFill.mc.field_71439_g.func_184592_cb().func_77973_b()).func_179223_d() instanceof BlockObsidian)) {
                    return false;
                }
                handSwing = EnumHand.OFF_HAND;
            }
        }
        return PlacementUtil.place(pos, handSwing, this.rotate.getValue(), true);
    }
    
    private List<BlockPos> findHoles() {
        final NonNullList<BlockPos> holes = (NonNullList<BlockPos>)NonNullList.func_191196_a();
        final List<BlockPos> blockPosList = EntityUtil.getSphere(PlayerUtil.getPlayerPos(), this.range.getValue().floatValue(), this.range.getValue().intValue(), false, true, 0);
        for (final BlockPos blockPos : blockPosList) {
            if (HoleUtil.isHole(blockPos, true, true).getType() == HoleUtil.HoleType.SINGLE) {
                holes.add((Object)blockPos);
            }
        }
        return (List<BlockPos>)holes;
    }
    
    private int findRightBlock() {
        final String s = this.mode.getValue();
        switch (s) {
            case "Both": {
                final int newHand = InventoryUtil.findFirstBlockSlot((Class<? extends Block>)BlockObsidian.class, 0, 8);
                if (newHand == -1) {
                    return InventoryUtil.findFirstBlockSlot((Class<? extends Block>)BlockEnderChest.class, 0, 8);
                }
                return newHand;
            }
            case "Obby": {
                return InventoryUtil.findFirstBlockSlot((Class<? extends Block>)BlockObsidian.class, 0, 8);
            }
            case "Echest": {
                return InventoryUtil.findFirstBlockSlot((Class<? extends Block>)BlockEnderChest.class, 0, 8);
            }
            case "Web": {
                return InventoryUtil.findFirstBlockSlot((Class<? extends Block>)BlockWeb.class, 0, 8);
            }
            case "Plate": {
                return InventoryUtil.findFirstBlockSlot((Class<? extends Block>)BlockPressurePlate.class, 0, 8);
            }
            default: {
                return -1;
            }
        }
    }
    
    private Boolean isHoldingRightBlock(final int hand, final Item item) {
        if (hand == -1) {
            return false;
        }
        if (!(item instanceof ItemBlock)) {
            return false;
        }
        final Block block = ((ItemBlock)item).func_179223_d();
        final String s = this.mode.getValue();
        switch (s) {
            case "Both": {
                return block instanceof BlockObsidian || block instanceof BlockEnderChest;
            }
            case "Obby": {
                return block instanceof BlockObsidian;
            }
            case "Echest": {
                return block instanceof BlockEnderChest;
            }
            case "Web": {
                return block instanceof BlockWeb;
            }
            case "Plate": {
                return block instanceof BlockPressurePlate;
            }
            default: {
                return false;
            }
        }
    }
}
