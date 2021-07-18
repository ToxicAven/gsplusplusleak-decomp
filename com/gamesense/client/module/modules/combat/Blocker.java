// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.combat;

import com.gamesense.api.util.world.combat.CrystalUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import com.gamesense.api.util.player.InventoryUtil;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import java.util.Iterator;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockAnvil;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.util.EnumHand;
import net.minecraft.init.Blocks;
import com.gamesense.api.util.world.BlockUtil;
import net.minecraft.block.BlockObsidian;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.SpoofRotationUtil;
import java.util.Arrays;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Blocker", category = Category.Combat)
public class Blocker extends Module
{
    BooleanSetting rotate;
    BooleanSetting anvilBlocker;
    BooleanSetting offHandObby;
    BooleanSetting pistonBlocker;
    BooleanSetting antiFacePlace;
    IntegerSetting BlocksPerTick;
    ModeSetting blockPlaced;
    IntegerSetting tickDelay;
    private int delayTimeTicks;
    private boolean noObby;
    private boolean noActive;
    private boolean activedBefore;
    
    public Blocker() {
        this.rotate = this.registerBoolean("Rotate", true);
        this.anvilBlocker = this.registerBoolean("Anvil", true);
        this.offHandObby = this.registerBoolean("Off Hand Obby", true);
        this.pistonBlocker = this.registerBoolean("Piston", true);
        this.antiFacePlace = this.registerBoolean("Shift AntiFacePlace", true);
        this.BlocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 10);
        this.blockPlaced = this.registerMode("Block Place", Arrays.asList("Pressure", "String"), "String");
        this.tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
        this.delayTimeTicks = 0;
    }
    
    public void onEnable() {
        SpoofRotationUtil.ROTATION_UTIL.onEnable();
        PlacementUtil.onEnable();
        if (Blocker.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        if (!this.anvilBlocker.getValue() && !this.pistonBlocker.getValue() && !this.antiFacePlace.getValue()) {
            this.noActive = true;
            this.disable();
        }
        this.noObby = false;
    }
    
    public void onDisable() {
        SpoofRotationUtil.ROTATION_UTIL.onDisable();
        PlacementUtil.onDisable();
        if (Blocker.mc.field_71439_g == null) {
            return;
        }
        if (this.noActive) {
            this.setDisabledMessage("Nothing is active... Blocker turned OFF!");
        }
        else if (this.noObby) {
            this.setDisabledMessage("Obsidian not found... Blocker turned OFF!");
        }
    }
    
    @Override
    public void onUpdate() {
        if (Blocker.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        if (this.noObby) {
            this.disable();
            return;
        }
        if (this.delayTimeTicks < this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
        }
        else {
            SpoofRotationUtil.ROTATION_UTIL.shouldSpoofAngles(true);
            this.delayTimeTicks = 0;
            if (this.anvilBlocker.getValue()) {
                this.blockAnvil();
            }
            if (this.pistonBlocker.getValue()) {
                this.blockPiston();
            }
            if (this.antiFacePlace.getValue() && Blocker.mc.field_71474_y.field_74311_E.func_151468_f()) {
                this.antiFacePlace();
            }
        }
    }
    
    private void antiFacePlace() {
        int blocksPlaced = 0;
        for (final Vec3d surround : new Vec3d[] { new Vec3d(1.0, 1.0, 0.0), new Vec3d(-1.0, 1.0, 0.0), new Vec3d(0.0, 1.0, 1.0), new Vec3d(0.0, 1.0, -1.0) }) {
            final BlockPos pos = new BlockPos(Blocker.mc.field_71439_g.field_70165_t + surround.field_72450_a, Blocker.mc.field_71439_g.field_70163_u, Blocker.mc.field_71439_g.field_70161_v + surround.field_72449_c);
            final Block temp;
            if ((temp = BlockUtil.getBlock(pos)) instanceof BlockObsidian || temp == Blocks.field_150357_h) {
                if (blocksPlaced++ == 0) {
                    AntiCrystal.getHotBarPressure(this.blockPlaced.getValue());
                }
                PlacementUtil.placeItem(new BlockPos((double)pos.func_177958_n(), pos.func_177956_o() + surround.field_72448_b, (double)pos.func_177952_p()), EnumHand.MAIN_HAND, this.rotate.getValue(), Items.field_151007_F.getClass());
                if (blocksPlaced == this.BlocksPerTick.getValue()) {
                    return;
                }
            }
        }
    }
    
    private void blockAnvil() {
        boolean found = false;
        for (final Entity t : Blocker.mc.field_71441_e.field_72996_f) {
            if (t instanceof EntityFallingBlock) {
                final Block ex = ((EntityFallingBlock)t).field_175132_d.func_177230_c();
                if (!(ex instanceof BlockAnvil) || (int)t.field_70165_t != (int)Blocker.mc.field_71439_g.field_70165_t || (int)t.field_70161_v != (int)Blocker.mc.field_71439_g.field_70161_v || !(BlockUtil.getBlock(Blocker.mc.field_71439_g.field_70165_t, Blocker.mc.field_71439_g.field_70163_u + 2.0, Blocker.mc.field_71439_g.field_70161_v) instanceof BlockAir)) {
                    continue;
                }
                this.placeBlock(new BlockPos(Blocker.mc.field_71439_g.field_70165_t, Blocker.mc.field_71439_g.field_70163_u + 2.0, Blocker.mc.field_71439_g.field_70161_v));
                MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "AutoAnvil detected... Anvil Blocked!");
                found = true;
            }
        }
        if (!found && this.activedBefore) {
            this.activedBefore = false;
            OffHand.removeItem(0);
        }
    }
    
    private void blockPiston() {
        for (final Entity t : Blocker.mc.field_71441_e.field_72996_f) {
            if (t instanceof EntityEnderCrystal && t.field_70165_t >= Blocker.mc.field_71439_g.field_70165_t - 1.5 && t.field_70165_t <= Blocker.mc.field_71439_g.field_70165_t + 1.5 && t.field_70161_v >= Blocker.mc.field_71439_g.field_70161_v - 1.5 && t.field_70161_v <= Blocker.mc.field_71439_g.field_70161_v + 1.5) {
                for (int i = -2; i < 3; ++i) {
                    for (int j = -2; j < 3; ++j) {
                        if ((i == 0 || j == 0) && BlockUtil.getBlock(t.field_70165_t + i, t.field_70163_u, t.field_70161_v + j) instanceof BlockPistonBase) {
                            this.breakCrystalPiston(t);
                            MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "PistonCrystal detected... Destroyed crystal!");
                        }
                    }
                }
            }
        }
    }
    
    private void placeBlock(final BlockPos pos) {
        EnumHand handSwing = EnumHand.MAIN_HAND;
        final int obsidianSlot = InventoryUtil.findObsidianSlot(this.offHandObby.getValue(), this.activedBefore);
        if (obsidianSlot == -1) {
            this.noObby = true;
            return;
        }
        if (obsidianSlot == 9) {
            this.activedBefore = true;
            if (!(Blocker.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock) || !(((ItemBlock)Blocker.mc.field_71439_g.func_184592_cb().func_77973_b()).func_179223_d() instanceof BlockObsidian)) {
                return;
            }
            handSwing = EnumHand.OFF_HAND;
        }
        if (Blocker.mc.field_71439_g.field_71071_by.field_70461_c != obsidianSlot && obsidianSlot != 9) {
            Blocker.mc.field_71439_g.field_71071_by.field_70461_c = obsidianSlot;
        }
        PlacementUtil.place(pos, handSwing, this.rotate.getValue(), true);
    }
    
    private void breakCrystalPiston(final Entity crystal) {
        if (this.rotate.getValue()) {
            SpoofRotationUtil.ROTATION_UTIL.lookAtPacket(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v, (EntityPlayer)Blocker.mc.field_71439_g);
        }
        CrystalUtil.breakCrystal(crystal);
        if (this.rotate.getValue()) {
            SpoofRotationUtil.ROTATION_UTIL.resetRotation();
        }
    }
}
