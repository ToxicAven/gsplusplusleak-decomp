// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.combat;

import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import com.gamesense.api.util.misc.Offsets;
import com.gamesense.api.util.player.PlayerUtil;
import net.minecraft.block.Block;
import com.gamesense.api.util.player.InventoryUtil;
import net.minecraft.block.BlockWeb;
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

@Declaration(name = "AutoWeb", category = Category.Combat)
public class AutoWeb extends Module
{
    ModeSetting offsetMode;
    ModeSetting targetMode;
    IntegerSetting enemyRange;
    IntegerSetting delayTicks;
    IntegerSetting blocksPerTick;
    BooleanSetting rotate;
    BooleanSetting sneakOnly;
    BooleanSetting disableNoBlock;
    private final Timer delayTimer;
    private EntityPlayer targetPlayer;
    private int oldSlot;
    private int offsetSteps;
    private boolean outOfTargetBlock;
    private boolean isSneaking;
    
    public AutoWeb() {
        this.offsetMode = this.registerMode("Pattern", Arrays.asList("Single", "Double"), "Single");
        this.targetMode = this.registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest");
        this.enemyRange = this.registerInteger("Range", 4, 0, 6);
        this.delayTicks = this.registerInteger("Tick Delay", 3, 0, 10);
        this.blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 1, 8);
        this.rotate = this.registerBoolean("Rotate", true);
        this.sneakOnly = this.registerBoolean("Sneak Only", false);
        this.disableNoBlock = this.registerBoolean("Disable No Web", true);
        this.delayTimer = new Timer();
        this.targetPlayer = null;
        this.oldSlot = -1;
        this.offsetSteps = 0;
        this.outOfTargetBlock = false;
        this.isSneaking = false;
    }
    
    public void onEnable() {
        PlacementUtil.onEnable();
        if (AutoWeb.mc.field_71439_g == null || AutoWeb.mc.field_71441_e == null) {
            this.disable();
            return;
        }
        this.oldSlot = AutoWeb.mc.field_71439_g.field_71071_by.field_70461_c;
    }
    
    public void onDisable() {
        PlacementUtil.onDisable();
        if (AutoWeb.mc.field_71439_g == null | AutoWeb.mc.field_71441_e == null) {
            return;
        }
        if (this.outOfTargetBlock) {
            this.setDisabledMessage("No web detected... AutoWeb turned OFF!");
        }
        if (this.oldSlot != AutoWeb.mc.field_71439_g.field_71071_by.field_70461_c && this.oldSlot != -1) {
            AutoWeb.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            this.oldSlot = -1;
        }
        if (this.isSneaking) {
            AutoWeb.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)AutoWeb.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        AutoCrystal.stopAC = false;
        this.outOfTargetBlock = false;
        this.targetPlayer = null;
    }
    
    @Override
    public void onUpdate() {
        if (AutoWeb.mc.field_71439_g == null || AutoWeb.mc.field_71441_e == null) {
            this.disable();
            return;
        }
        if (this.sneakOnly.getValue() && !AutoWeb.mc.field_71439_g.func_70093_af()) {
            return;
        }
        final int targetBlockSlot = InventoryUtil.findFirstBlockSlot((Class<? extends Block>)BlockWeb.class, 0, 8);
        if ((this.outOfTargetBlock || targetBlockSlot == -1) && this.disableNoBlock.getValue()) {
            this.outOfTargetBlock = true;
            this.disable();
            return;
        }
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
                    case 2052876273: {
                        if (s2.equals("Double")) {
                            n2 = 0;
                            break;
                        }
                        break;
                    }
                }
                Vec3d[] offsetPattern = null;
                int maxSteps = 0;
                switch (n2) {
                    case 0: {
                        offsetPattern = Offsets.BURROW_DOUBLE;
                        maxSteps = Offsets.BURROW_DOUBLE.length;
                        break;
                    }
                    default: {
                        offsetPattern = Offsets.BURROW;
                        maxSteps = Offsets.BURROW.length;
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
                if (!AutoWeb.mc.field_71441_e.func_180495_p(targetPos).func_185904_a().func_76222_j()) {
                    tryPlacing = false;
                }
                if (tryPlacing && this.placeBlock(targetPos)) {
                    ++blocksPlaced;
                }
                ++this.offsetSteps;
                if (!this.isSneaking) {
                    continue;
                }
                AutoWeb.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)AutoWeb.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
                this.isSneaking = false;
            }
        }
    }
    
    private boolean placeBlock(final BlockPos pos) {
        final EnumHand handSwing = EnumHand.MAIN_HAND;
        final int targetBlockSlot = InventoryUtil.findFirstBlockSlot((Class<? extends Block>)BlockWeb.class, 0, 8);
        if (targetBlockSlot == -1) {
            this.outOfTargetBlock = true;
            return false;
        }
        if (AutoWeb.mc.field_71439_g.field_71071_by.field_70461_c != targetBlockSlot) {
            AutoWeb.mc.field_71439_g.field_71071_by.field_70461_c = targetBlockSlot;
        }
        return PlacementUtil.place(pos, handSwing, this.rotate.getValue(), true);
    }
}
