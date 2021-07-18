// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.combat;

import net.minecraft.block.BlockObsidian;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumHand;
import java.util.Iterator;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import com.gamesense.api.util.misc.Offsets;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.player.InventoryUtil;
import com.gamesense.client.module.ModuleManager;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import com.gamesense.api.util.player.PlacementUtil;
import java.util.Arrays;
import net.minecraft.entity.player.EntityPlayer;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "AutoTrap", category = Category.Combat)
public class AutoTrap extends Module
{
    ModeSetting offsetMode;
    ModeSetting targetMode;
    IntegerSetting enemyRange;
    IntegerSetting delayTicks;
    IntegerSetting blocksPerTick;
    BooleanSetting rotate;
    BooleanSetting sneakOnly;
    BooleanSetting disableNoBlock;
    BooleanSetting offhandObby;
    private final Timer delayTimer;
    private EntityPlayer targetPlayer;
    private int oldSlot;
    private int offsetSteps;
    private boolean outOfTargetBlock;
    private boolean activedOff;
    private boolean isSneaking;
    
    public AutoTrap() {
        this.offsetMode = this.registerMode("Pattern", Arrays.asList("Normal", "No Step", "Simple"), "Normal");
        this.targetMode = this.registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest");
        this.enemyRange = this.registerInteger("Range", 4, 0, 6);
        this.delayTicks = this.registerInteger("Tick Delay", 3, 0, 10);
        this.blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 1, 8);
        this.rotate = this.registerBoolean("Rotate", true);
        this.sneakOnly = this.registerBoolean("Sneak Only", false);
        this.disableNoBlock = this.registerBoolean("Disable No Obby", true);
        this.offhandObby = this.registerBoolean("Offhand Obby", false);
        this.delayTimer = new Timer();
        this.targetPlayer = null;
        this.oldSlot = -1;
        this.offsetSteps = 0;
        this.outOfTargetBlock = false;
        this.activedOff = false;
        this.isSneaking = false;
    }
    
    public void onEnable() {
        PlacementUtil.onEnable();
        if (AutoTrap.mc.field_71439_g == null || AutoTrap.mc.field_71441_e == null) {
            this.disable();
            return;
        }
        this.oldSlot = AutoTrap.mc.field_71439_g.field_71071_by.field_70461_c;
    }
    
    public void onDisable() {
        PlacementUtil.onDisable();
        if (AutoTrap.mc.field_71439_g == null | AutoTrap.mc.field_71441_e == null) {
            return;
        }
        if (this.outOfTargetBlock) {
            this.setDisabledMessage("No obsidian detected... AutoTrap turned OFF!");
        }
        if (this.oldSlot != AutoTrap.mc.field_71439_g.field_71071_by.field_70461_c && this.oldSlot != -1 && this.oldSlot != 9) {
            AutoTrap.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            this.oldSlot = -1;
        }
        if (this.isSneaking) {
            AutoTrap.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)AutoTrap.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        AutoCrystal.stopAC = false;
        if (this.offhandObby.getValue() && ModuleManager.isModuleEnabled(OffHand.class)) {
            OffHand.removeItem(0);
            this.activedOff = false;
        }
        this.outOfTargetBlock = false;
        this.targetPlayer = null;
    }
    
    @Override
    public void onUpdate() {
        if (AutoTrap.mc.field_71439_g == null || AutoTrap.mc.field_71441_e == null) {
            this.disable();
            return;
        }
        if (this.sneakOnly.getValue() && !AutoTrap.mc.field_71439_g.func_70093_af()) {
            return;
        }
        final int targetBlockSlot = InventoryUtil.findObsidianSlot(this.offhandObby.getValue(), this.activedOff);
        if ((this.outOfTargetBlock || targetBlockSlot == -1) && this.disableNoBlock.getValue()) {
            this.outOfTargetBlock = true;
            this.disable();
            return;
        }
        this.activedOff = true;
        final String s = this.targetMode.getValue();
        switch (s) {
            case "Nearest": {
                this.targetPlayer = PlayerUtil.findClosestTarget(this.enemyRange.getValue(), this.targetPlayer);
                break;
            }
            case "Looking": {
                this.targetPlayer = PlayerUtil.findLookingPlayer(this.enemyRange.getValue());
                break;
            }
            default: {
                this.targetPlayer = null;
                break;
            }
        }
        if (this.targetPlayer == null) {
            return;
        }
        final Vec3d targetVec3d = this.targetPlayer.func_174791_d();
        if (this.delayTimer.getTimePassed() / 50L >= this.delayTicks.getValue()) {
            this.delayTimer.reset();
            int blocksPlaced = 0;
            while (blocksPlaced <= this.blocksPerTick.getValue()) {
                final String s2 = this.offsetMode.getValue();
                int n2 = -1;
                switch (s2.hashCode()) {
                    case -579181109: {
                        if (s2.equals("No Step")) {
                            n2 = 0;
                            break;
                        }
                        break;
                    }
                    case -1818419758: {
                        if (s2.equals("Simple")) {
                            n2 = 1;
                            break;
                        }
                        break;
                    }
                }
                Vec3d[] offsetPattern = null;
                int maxSteps = 0;
                switch (n2) {
                    case 0: {
                        offsetPattern = Offsets.TRAP_STEP;
                        maxSteps = Offsets.TRAP_STEP.length;
                        break;
                    }
                    case 1: {
                        offsetPattern = Offsets.TRAP_SIMPLE;
                        maxSteps = Offsets.TRAP_SIMPLE.length;
                        break;
                    }
                    default: {
                        offsetPattern = Offsets.TRAP_FULL;
                        maxSteps = Offsets.TRAP_FULL.length;
                        break;
                    }
                }
                if (this.offsetSteps >= maxSteps) {
                    this.offsetSteps = 0;
                    break;
                }
                final BlockPos offsetPos = new BlockPos(offsetPattern[this.offsetSteps]);
                BlockPos targetPos = new BlockPos(targetVec3d).func_177982_a(offsetPos.func_177958_n(), offsetPos.func_177956_o(), offsetPos.func_177952_p());
                boolean tryPlacing = true;
                if (this.targetPlayer.field_70163_u % 1.0 > 0.2) {
                    targetPos = new BlockPos(targetPos.func_177958_n(), targetPos.func_177956_o() + 1, targetPos.func_177952_p());
                }
                if (!AutoTrap.mc.field_71441_e.func_180495_p(targetPos).func_185904_a().func_76222_j()) {
                    tryPlacing = false;
                }
                for (final Entity entity : AutoTrap.mc.field_71441_e.func_72839_b((Entity)null, new AxisAlignedBB(targetPos))) {
                    if (entity instanceof EntityPlayer) {
                        tryPlacing = false;
                        break;
                    }
                }
                if (tryPlacing && this.placeBlock(targetPos)) {
                    ++blocksPlaced;
                }
                ++this.offsetSteps;
                if (!this.isSneaking) {
                    continue;
                }
                AutoTrap.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)AutoTrap.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
                this.isSneaking = false;
            }
        }
    }
    
    private boolean placeBlock(final BlockPos pos) {
        EnumHand handSwing = EnumHand.MAIN_HAND;
        final int targetBlockSlot = InventoryUtil.findObsidianSlot(this.offhandObby.getValue(), this.activedOff);
        if (targetBlockSlot == -1) {
            this.outOfTargetBlock = true;
            return false;
        }
        if (targetBlockSlot == 9) {
            this.activedOff = true;
            if (!(AutoTrap.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock) || !(((ItemBlock)AutoTrap.mc.field_71439_g.func_184592_cb().func_77973_b()).func_179223_d() instanceof BlockObsidian)) {
                return false;
            }
            handSwing = EnumHand.OFF_HAND;
        }
        if (AutoTrap.mc.field_71439_g.field_71071_by.field_70461_c != targetBlockSlot && targetBlockSlot != 9) {
            AutoTrap.mc.field_71439_g.field_71071_by.field_70461_c = targetBlockSlot;
        }
        return PlacementUtil.place(pos, handSwing, this.rotate.getValue(), true);
    }
}
