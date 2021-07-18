// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.combat;

import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import com.gamesense.api.util.world.EntityUtil;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.util.NonNullList;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.AutoGG;
import net.minecraft.entity.Entity;
import com.gamesense.api.util.world.combat.DamageUtil;
import net.minecraft.init.Blocks;
import java.util.Iterator;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumFacing;
import com.gamesense.api.util.world.BlockUtil;
import net.minecraft.util.math.Vec3d;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import com.gamesense.api.util.player.InventoryUtil;
import net.minecraft.item.ItemBed;
import java.util.Arrays;
import com.gamesense.api.util.misc.Timer;
import net.minecraft.util.math.BlockPos;
import java.util.ArrayList;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "BedAura", category = Category.Combat)
public class BedAura extends Module
{
    ModeSetting attackMode;
    DoubleSetting attackRange;
    IntegerSetting breakDelay;
    IntegerSetting placeDelay;
    DoubleSetting targetRange;
    BooleanSetting rotate;
    BooleanSetting disableNone;
    BooleanSetting autoSwitch;
    BooleanSetting antiSuicide;
    IntegerSetting antiSuicideHealth;
    IntegerSetting minDamage;
    private boolean hasNone;
    private int oldSlot;
    private final ArrayList<BlockPos> placedPos;
    private final Timer breakTimer;
    private final Timer placeTimer;
    
    public BedAura() {
        this.attackMode = this.registerMode("Mode", Arrays.asList("Normal", "Own"), "Own");
        this.attackRange = this.registerDouble("Attack Range", 4.0, 0.0, 10.0);
        this.breakDelay = this.registerInteger("Break Delay", 1, 0, 20);
        this.placeDelay = this.registerInteger("Place Delay", 1, 0, 20);
        this.targetRange = this.registerDouble("Target Range", 7.0, 0.0, 16.0);
        this.rotate = this.registerBoolean("Rotate", true);
        this.disableNone = this.registerBoolean("Disable No Bed", false);
        this.autoSwitch = this.registerBoolean("Switch", true);
        this.antiSuicide = this.registerBoolean("Anti Suicide", false);
        this.antiSuicideHealth = this.registerInteger("Suicide Health", 14, 1, 36);
        this.minDamage = this.registerInteger("Min Damage", 5, 1, 36);
        this.hasNone = false;
        this.oldSlot = -1;
        this.placedPos = new ArrayList<BlockPos>();
        this.breakTimer = new Timer();
        this.placeTimer = new Timer();
    }
    
    public void onEnable() {
        this.hasNone = false;
        this.placedPos.clear();
        if (BedAura.mc.field_71439_g == null || BedAura.mc.field_71441_e == null) {
            this.disable();
            return;
        }
        final int bedSlot = InventoryUtil.findFirstItemSlot((Class<? extends Item>)ItemBed.class, 0, 8);
        if (BedAura.mc.field_71439_g.field_71071_by.field_70461_c != bedSlot && bedSlot != -1 && this.autoSwitch.getValue()) {
            this.oldSlot = BedAura.mc.field_71439_g.field_71071_by.field_70461_c;
            BedAura.mc.field_71439_g.field_71071_by.field_70461_c = bedSlot;
        }
        else if (bedSlot == -1) {
            this.hasNone = true;
        }
    }
    
    public void onDisable() {
        this.placedPos.clear();
        if (BedAura.mc.field_71439_g == null || BedAura.mc.field_71441_e == null) {
            return;
        }
        if (this.autoSwitch.getValue() && BedAura.mc.field_71439_g.field_71071_by.field_70461_c != this.oldSlot && this.oldSlot != -1) {
            BedAura.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
        }
        if (this.hasNone && this.disableNone.getValue()) {
            this.setDisabledMessage("No beds detected... BedAura turned OFF!");
        }
        this.hasNone = false;
        this.oldSlot = -1;
    }
    
    @Override
    public void onUpdate() {
        if (BedAura.mc.field_71439_g == null || BedAura.mc.field_71441_e == null || BedAura.mc.field_71439_g.field_71093_bK == 0) {
            this.disable();
            return;
        }
        final int bedSlot = InventoryUtil.findFirstItemSlot((Class<? extends Item>)ItemBed.class, 0, 8);
        if (BedAura.mc.field_71439_g.field_71071_by.field_70461_c != bedSlot && bedSlot != -1 && this.autoSwitch.getValue()) {
            this.oldSlot = BedAura.mc.field_71439_g.field_71071_by.field_70461_c;
            BedAura.mc.field_71439_g.field_71071_by.field_70461_c = bedSlot;
        }
        else if (bedSlot == -1) {
            this.hasNone = true;
        }
        if (this.antiSuicide.getValue() && BedAura.mc.field_71439_g.func_110143_aJ() + BedAura.mc.field_71439_g.func_110139_bj() < this.antiSuicideHealth.getValue()) {
            return;
        }
        if (this.breakTimer.getTimePassed() / 50L >= this.breakDelay.getValue()) {
            this.breakTimer.reset();
            this.breakBed();
        }
        if (this.hasNone) {
            if (this.disableNone.getValue()) {
                this.disable();
            }
        }
        else {
            if (BedAura.mc.field_71439_g.field_71071_by.func_70301_a(BedAura.mc.field_71439_g.field_71071_by.field_70461_c).func_77973_b() != Items.field_151104_aV) {
                return;
            }
            if (this.placeTimer.getTimePassed() / 50L >= this.placeDelay.getValue()) {
                this.placeTimer.reset();
                this.placeBed();
            }
        }
    }
    
    private void breakBed() {
        for (final TileEntity tileEntity : this.findBedEntities((EntityPlayer)BedAura.mc.field_71439_g)) {
            if (!(tileEntity instanceof TileEntityBed)) {
                continue;
            }
            if (this.rotate.getValue()) {
                BlockUtil.faceVectorPacketInstant(new Vec3d((double)tileEntity.func_174877_v().func_177958_n(), (double)tileEntity.func_174877_v().func_177956_o(), (double)tileEntity.func_174877_v().func_177952_p()), true);
            }
            BedAura.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(tileEntity.func_174877_v(), EnumFacing.UP, EnumHand.OFF_HAND, 0.0f, 0.0f, 0.0f));
        }
    }
    
    private void placeBed() {
        for (final EntityPlayer entityPlayer : this.findTargetEntities((EntityPlayer)BedAura.mc.field_71439_g)) {
            if (entityPlayer.field_70128_L) {
                continue;
            }
            final NonNullList<BlockPos> targetPos = this.findTargetPlacePos(entityPlayer);
            if (targetPos.size() < 1) {
                continue;
            }
            for (final BlockPos blockPos : targetPos) {
                final BlockPos targetPos2 = blockPos.func_177984_a();
                if (targetPos2.func_185332_f((int)BedAura.mc.field_71439_g.field_70165_t, (int)BedAura.mc.field_71439_g.field_70163_u, (int)BedAura.mc.field_71439_g.field_70161_v) > this.attackRange.getValue()) {
                    continue;
                }
                if (BedAura.mc.field_71441_e.func_180495_p(targetPos2).func_177230_c() != Blocks.field_150350_a) {
                    continue;
                }
                if (entityPlayer.func_180425_c() == targetPos2) {
                    continue;
                }
                if (DamageUtil.calculateDamage(targetPos2.func_177958_n(), targetPos2.func_177956_o(), targetPos2.func_177952_p(), (Entity)entityPlayer) < this.minDamage.getValue()) {
                    continue;
                }
                if (ModuleManager.isModuleEnabled(AutoGG.class)) {
                    AutoGG.INSTANCE.addTargetedPlayer(entityPlayer.func_70005_c_());
                }
                if (BedAura.mc.field_71441_e.func_180495_p(targetPos2.func_177974_f()).func_177230_c() == Blocks.field_150350_a) {
                    this.placeBedFinal(targetPos2, 90, EnumFacing.DOWN);
                    return;
                }
                if (BedAura.mc.field_71441_e.func_180495_p(targetPos2.func_177976_e()).func_177230_c() == Blocks.field_150350_a) {
                    this.placeBedFinal(targetPos2, -90, EnumFacing.DOWN);
                    return;
                }
                if (BedAura.mc.field_71441_e.func_180495_p(targetPos2.func_177978_c()).func_177230_c() == Blocks.field_150350_a) {
                    this.placeBedFinal(targetPos2, 0, EnumFacing.DOWN);
                    return;
                }
                if (BedAura.mc.field_71441_e.func_180495_p(targetPos2.func_177968_d()).func_177230_c() == Blocks.field_150350_a) {
                    this.placeBedFinal(targetPos2, 180, EnumFacing.SOUTH);
                }
            }
        }
    }
    
    private NonNullList<TileEntity> findBedEntities(final EntityPlayer entityPlayer) {
        final NonNullList<TileEntity> bedEntities = (NonNullList<TileEntity>)NonNullList.func_191196_a();
        BedAura.mc.field_71441_e.field_147482_g.stream().filter(tileEntity -> tileEntity instanceof TileEntityBed).filter(tileEntity -> tileEntity.func_145835_a(entityPlayer.field_70165_t, entityPlayer.field_70163_u, entityPlayer.field_70161_v) <= this.attackRange.getValue() * this.attackRange.getValue()).filter(this::isOwn).forEach(bedEntities::add);
        bedEntities.sort((Comparator)Comparator.comparing(tileEntity -> tileEntity.func_145835_a(entityPlayer.field_70165_t, entityPlayer.field_70163_u, entityPlayer.field_70161_v)));
        return bedEntities;
    }
    
    private boolean isOwn(final TileEntity tileEntity) {
        if (this.attackMode.getValue().equalsIgnoreCase("Normal")) {
            return true;
        }
        if (this.attackMode.getValue().equalsIgnoreCase("Own")) {
            for (final BlockPos blockPos : this.placedPos) {
                if (blockPos.func_185332_f(tileEntity.func_174877_v().func_177958_n(), tileEntity.func_174877_v().func_177956_o(), tileEntity.func_174877_v().func_177952_p()) <= 3.0) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private NonNullList<EntityPlayer> findTargetEntities(final EntityPlayer entityPlayer) {
        final NonNullList<EntityPlayer> targetEntities = (NonNullList<EntityPlayer>)NonNullList.func_191196_a();
        BedAura.mc.field_71441_e.field_73010_i.stream().filter(entityPlayer1 -> !EntityUtil.basicChecksEntity(entityPlayer1)).filter(entityPlayer1 -> entityPlayer1.func_70032_d((Entity)entityPlayer) <= this.targetRange.getValue()).sorted(Comparator.comparing(entityPlayer1 -> entityPlayer1.func_70032_d((Entity)entityPlayer))).forEach(targetEntities::add);
        return targetEntities;
    }
    
    private NonNullList<BlockPos> findTargetPlacePos(final EntityPlayer entityPlayer) {
        final NonNullList<BlockPos> targetPlacePos = (NonNullList<BlockPos>)NonNullList.func_191196_a();
        targetPlacePos.addAll((Collection)EntityUtil.getSphere(BedAura.mc.field_71439_g.func_180425_c(), this.attackRange.getValue().floatValue(), this.attackRange.getValue().intValue(), false, true, 0).stream().filter((Predicate<? super Object>)this::canPlaceBed).sorted(Comparator.comparing(blockPos -> 1.0f - DamageUtil.calculateDamage(blockPos.func_177984_a().func_177958_n(), blockPos.func_177984_a().func_177956_o(), blockPos.func_177984_a().func_177952_p(), (Entity)entityPlayer))).collect((Collector<? super Object, ?, List<? super Object>>)Collectors.toList()));
        return targetPlacePos;
    }
    
    private boolean canPlaceBed(final BlockPos blockPos) {
        return BedAura.mc.field_71441_e.func_180495_p(blockPos.func_177984_a()).func_177230_c() == Blocks.field_150350_a && BedAura.mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150350_a && BedAura.mc.field_71441_e.func_72872_a((Class)Entity.class, new AxisAlignedBB(blockPos)).isEmpty();
    }
    
    private void placeBedFinal(final BlockPos blockPos, final int direction, final EnumFacing enumFacing) {
        BedAura.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation((float)direction, 0.0f, BedAura.mc.field_71439_g.field_70122_E));
        if (BedAura.mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150350_a) {
            return;
        }
        final BlockPos neighbourPos = blockPos.func_177972_a(enumFacing);
        final EnumFacing oppositeFacing = enumFacing.func_176734_d();
        final Vec3d vec3d = new Vec3d((Vec3i)neighbourPos).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(oppositeFacing.func_176730_m()).func_186678_a(0.5));
        if (this.rotate.getValue()) {
            BlockUtil.faceVectorPacketInstant(vec3d, true);
        }
        BedAura.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)BedAura.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
        BedAura.mc.field_71442_b.func_187099_a(BedAura.mc.field_71439_g, BedAura.mc.field_71441_e, neighbourPos, oppositeFacing, vec3d, EnumHand.MAIN_HAND);
        BedAura.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
        BedAura.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)BedAura.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
        this.placedPos.add(blockPos);
    }
}
