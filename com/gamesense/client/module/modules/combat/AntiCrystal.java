// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.combat;

import com.gamesense.api.util.world.combat.ac.CrystalInfo;
import net.minecraft.init.Items;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumFacing;
import com.gamesense.client.module.ModuleManager;
import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import com.gamesense.api.util.world.BlockUtil;
import net.minecraft.block.BlockAir;
import com.gamesense.api.util.world.combat.DamageUtil;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import java.util.Arrays;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "AntiCrystal", category = Category.Combat)
public class AntiCrystal extends Module
{
    DoubleSetting rangePlace;
    DoubleSetting enemyRange;
    DoubleSetting damageMin;
    DoubleSetting biasDamage;
    ModeSetting blockPlaced;
    IntegerSetting tickDelay;
    IntegerSetting blocksPerTick;
    BooleanSetting offHandMode;
    BooleanSetting rotate;
    BooleanSetting onlyIfEnemy;
    BooleanSetting nonAbusive;
    BooleanSetting checkDamage;
    BooleanSetting switchBack;
    BooleanSetting notOurCrystals;
    private int delayTimeTicks;
    private boolean isSneaking;
    
    public AntiCrystal() {
        this.rangePlace = this.registerDouble("Range Place", 5.9, 0.0, 6.0);
        this.enemyRange = this.registerDouble("Enemy Range", 12.0, 0.0, 20.0);
        this.damageMin = this.registerDouble("Damage Min", 4.0, 0.0, 15.0);
        this.biasDamage = this.registerDouble("Bias Damage", 1.0, 0.0, 3.0);
        this.blockPlaced = this.registerMode("Block Place", Arrays.asList("Pressure", "String"), "String");
        this.tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
        this.blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 8);
        this.offHandMode = this.registerBoolean("OffHand Mode", true);
        this.rotate = this.registerBoolean("Rotate", false);
        this.onlyIfEnemy = this.registerBoolean("Only If Enemy", true);
        this.nonAbusive = this.registerBoolean("Non Abusive", true);
        this.checkDamage = this.registerBoolean("Damage Check", true);
        this.switchBack = this.registerBoolean("Switch Back", true);
        this.notOurCrystals = this.registerBoolean("Ignore AutoCrystal", true);
        this.isSneaking = false;
    }
    
    public void onEnable() {
        this.delayTimeTicks = 0;
    }
    
    public void onDisable() {
        if (this.isSneaking) {
            AntiCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)AntiCrystal.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.delayTimeTicks < this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
            return;
        }
        this.delayTimeTicks = 0;
        if (this.onlyIfEnemy.getValue()) {
            if (AntiCrystal.mc.field_71441_e.field_73010_i.size() <= 1) {
                return;
            }
            boolean found = false;
            for (final EntityPlayer check : AntiCrystal.mc.field_71441_e.field_73010_i) {
                if (check != AntiCrystal.mc.field_71439_g && AntiCrystal.mc.field_71439_g.func_70032_d((Entity)check) <= this.enemyRange.getValue()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return;
            }
        }
        int blocksPlaced = 0;
        boolean pressureSwitch = true;
        int slotPressure = -1;
        for (final Entity t : AntiCrystal.mc.field_71441_e.field_72996_f) {
            if (t instanceof EntityEnderCrystal && AntiCrystal.mc.field_71439_g.func_70032_d(t) <= this.rangePlace.getValue()) {
                if (pressureSwitch) {
                    if (this.offHandMode.getValue() && isOffHandPressure(this.blockPlaced.getValue())) {
                        slotPressure = 9;
                    }
                    else if ((slotPressure = getHotBarPressure(this.blockPlaced.getValue())) == -1) {
                        return;
                    }
                    pressureSwitch = false;
                }
                if (!this.notOurCrystals.getValue() && this.usCrystal(t)) {
                    return;
                }
                if (this.checkDamage.getValue()) {
                    final float damage = (float)(DamageUtil.calculateDamage(t.field_70165_t, t.field_70163_u, t.field_70161_v, (Entity)AntiCrystal.mc.field_71439_g) * this.biasDamage.getValue());
                    if (damage < this.damageMin.getValue() && damage < AntiCrystal.mc.field_71439_g.func_110143_aJ()) {
                        return;
                    }
                }
                if (BlockUtil.getBlock(t.field_70165_t, t.field_70163_u, t.field_70161_v) instanceof BlockAir) {
                    this.placeBlock(new BlockPos(t.field_70165_t, t.field_70163_u, t.field_70161_v), slotPressure);
                    if (++blocksPlaced == this.blocksPerTick.getValue()) {
                        return;
                    }
                }
                if (!this.isSneaking) {
                    continue;
                }
                AntiCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)AntiCrystal.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
                this.isSneaking = false;
            }
        }
    }
    
    public boolean usCrystal(final Entity crystal) {
        final AutoCrystal autoCrystal = ModuleManager.getModule(AutoCrystal.class);
        return ModuleManager.isModuleEnabled(AutoCrystal.class) && autoCrystal.targets.stream().filter(placeInfo -> placeInfo.crystal.equals((Object)new BlockPos((double)(int)crystal.field_70165_t, crystal.field_70163_u - 1.0, (double)(int)crystal.field_70161_v))).findFirst().orElse(null) != null;
    }
    
    public static boolean isOffHandPressure(final String itemMode) {
        final OffHand offHand = ModuleManager.getModule(OffHand.class);
        return offHand.nonDefaultItem.getValue().equals(itemMode) || offHand.defaultItem.getValue().equals(itemMode);
    }
    
    private void placeBlock(final BlockPos pos, final int slotPressure) {
        int oldSlot = -1;
        final EnumFacing side = EnumFacing.DOWN;
        final BlockPos neighbour = pos.func_177972_a(side);
        final EnumFacing opposite = side.func_176734_d();
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        final Block neighbourBlock = AntiCrystal.mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
        if (slotPressure != 9 && AntiCrystal.mc.field_71439_g.field_71071_by.field_70461_c != slotPressure) {
            if (this.nonAbusive.getValue()) {
                return;
            }
            if (this.switchBack.getValue()) {
                oldSlot = AntiCrystal.mc.field_71439_g.field_71071_by.field_70461_c;
            }
            AntiCrystal.mc.field_71439_g.field_71071_by.field_70461_c = slotPressure;
        }
        if ((!this.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            AntiCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)AntiCrystal.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        boolean stoppedAC = false;
        if (ModuleManager.isModuleEnabled(AutoCrystal.class)) {
            AutoCrystal.stopAC = true;
            stoppedAC = true;
        }
        if (this.rotate.getValue()) {
            BlockUtil.faceVectorPacketInstant(hitVec, true);
        }
        EnumHand swingHand = EnumHand.MAIN_HAND;
        if (slotPressure == 9) {
            swingHand = EnumHand.OFF_HAND;
            if (!isPressure(AntiCrystal.mc.field_71439_g.func_184592_cb())) {
                return;
            }
        }
        else if (this.blockPlaced.getValue().equals("Pressure")) {
            if (!isPressure(AntiCrystal.mc.field_71439_g.func_184614_ca())) {
                return;
            }
        }
        else if (!isString(AntiCrystal.mc.field_71439_g.func_184614_ca())) {
            return;
        }
        AntiCrystal.mc.field_71442_b.func_187099_a(AntiCrystal.mc.field_71439_g, AntiCrystal.mc.field_71441_e, neighbour, opposite, hitVec, swingHand);
        AntiCrystal.mc.field_71439_g.func_184609_a(swingHand);
        if (this.switchBack.getValue() && oldSlot != -1) {
            AntiCrystal.mc.field_71439_g.field_71071_by.field_70461_c = oldSlot;
        }
        if (stoppedAC) {
            AutoCrystal.stopAC = false;
            stoppedAC = false;
        }
    }
    
    public static boolean isPressure(final ItemStack stack) {
        return stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemBlock && ((ItemBlock)stack.func_77973_b()).func_179223_d() instanceof BlockPressurePlate;
    }
    
    public static boolean isString(final ItemStack stack) {
        return stack != ItemStack.field_190927_a && !(stack.func_77973_b() instanceof ItemBlock) && stack.func_77973_b() == Items.field_151007_F;
    }
    
    public static int getHotBarPressure(final String mode) {
        for (int i = 0; i < 9; ++i) {
            if (mode.equals("Pressure")) {
                if (isPressure(AntiCrystal.mc.field_71439_g.field_71071_by.func_70301_a(i))) {
                    return i;
                }
            }
            else if (isString(AntiCrystal.mc.field_71439_g.field_71071_by.func_70301_a(i))) {
                return i;
            }
        }
        return -1;
    }
}
