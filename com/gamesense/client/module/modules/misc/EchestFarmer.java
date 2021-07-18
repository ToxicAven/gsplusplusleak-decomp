// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import com.gamesense.api.util.player.PlacementUtil;
import net.minecraft.util.math.Vec3i;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import com.gamesense.client.module.modules.combat.OffHand;
import com.gamesense.api.util.player.InventoryUtil;
import net.minecraft.init.Items;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.EntityLivingBase;
import com.gamesense.api.util.world.BlockUtil;
import java.util.function.ToIntFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import com.gamesense.api.util.player.SpoofRotationUtil;
import net.minecraft.util.math.Vec2f;
import java.util.function.Predicate;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.event.Phase;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.EnumFacing;
import java.util.ArrayList;
import net.minecraft.util.math.BlockPos;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "EchestFarmer", category = Category.Misc)
public class EchestFarmer extends Module
{
    ModeSetting breakBlock;
    ModeSetting HowplaceBlock;
    IntegerSetting stackCount;
    IntegerSetting tickDelay;
    BooleanSetting offHandEchest;
    BooleanSetting rotate;
    BooleanSetting forceRotation;
    private int delayTimeTicks;
    private int echestToMine;
    private int slotObby;
    private int slotPick;
    BlockPos blockAim;
    private boolean looking;
    private boolean noSpace;
    private boolean materialsNeeded;
    private boolean prevBreak;
    private ArrayList<EnumFacing> sides;
    Vec3d lastHitVec;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
    
    public EchestFarmer() {
        this.breakBlock = this.registerMode("Break Block", Arrays.asList("Normal", "Packet"), "Packet");
        this.HowplaceBlock = this.registerMode("Place Block", Arrays.asList("Near", "Looking"), "Looking");
        this.stackCount = this.registerInteger("N^Stack", 0, 0, 64);
        this.tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
        this.offHandEchest = this.registerBoolean("OffHand echest", false);
        this.rotate = this.registerBoolean("Rotate", false);
        this.forceRotation = this.registerBoolean("ForceRotation", false);
        this.sides = new ArrayList<EnumFacing>();
        Vec2f rotation;
        PlayerPacket packet;
        this.onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
            if (event.getPhase() == Phase.PRE && this.rotate.getValue() && this.lastHitVec != null && this.forceRotation.getValue()) {
                rotation = RotationUtil.getRotationTo(this.lastHitVec);
                packet = new PlayerPacket(this, rotation);
                PlayerPacketManager.INSTANCE.addPacket(packet);
            }
        }, (Predicate<OnUpdateWalkingPlayerEvent>[])new Predicate[0]);
    }
    
    public void onEnable() {
        SpoofRotationUtil.ROTATION_UTIL.onEnable();
        this.initValues();
    }
    
    private void initValues() {
        final boolean prevBreak = false;
        this.looking = prevBreak;
        this.noSpace = prevBreak;
        this.prevBreak = prevBreak;
        this.delayTimeTicks = 0;
        this.materialsNeeded = true;
        final int obbyCount = EchestFarmer.mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> itemStack.func_77973_b() instanceof ItemBlock && ((ItemBlock)itemStack.func_77973_b()).func_179223_d() == Blocks.field_150343_Z).mapToInt(ItemStack::func_190916_E).sum();
        final int stackWanted = (this.stackCount.getValue() == 0) ? -1 : (this.stackCount.getValue() * 64);
        this.echestToMine = (stackWanted - obbyCount) / 8;
        if (this.HowplaceBlock.getValue().equals("Looking")) {
            try {
                this.blockAim = EchestFarmer.mc.field_71476_x.func_178782_a();
                final BlockPos blockAim = this.blockAim;
                ++blockAim.field_177960_b;
            }
            catch (NullPointerException e) {
                this.disable();
                return;
            }
            if (BlockUtil.getPlaceableSide(this.blockAim) == null) {
                this.looking = false;
                return;
            }
            this.sides.clear();
            this.sides.add(EnumFacing.func_190914_a(this.blockAim, (EntityLivingBase)EchestFarmer.mc.field_71439_g));
        }
        else {
            for (final int[] sur : new int[][] { { 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }, { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 } }) {
                for (final int h : new int[] { 1, 0 }) {
                    if (BlockUtil.getBlock(EchestFarmer.mc.field_71439_g.field_70165_t + sur[0], EchestFarmer.mc.field_71439_g.field_70163_u + h, EchestFarmer.mc.field_71439_g.field_70161_v + sur[1]) instanceof BlockAir && BlockUtil.getPlaceableSide(new BlockPos(EchestFarmer.mc.field_71439_g.field_70165_t + sur[0], EchestFarmer.mc.field_71439_g.field_70163_u + h, EchestFarmer.mc.field_71439_g.field_70161_v + sur[1])) != null && !PistonCrystal.someoneInCoords(EchestFarmer.mc.field_71439_g.field_70165_t + sur[0], EchestFarmer.mc.field_71439_g.field_70161_v + sur[1])) {
                        this.blockAim = new BlockPos(EchestFarmer.mc.field_71439_g.field_70165_t + sur[0], EchestFarmer.mc.field_71439_g.field_70163_u + h, EchestFarmer.mc.field_71439_g.field_70161_v + sur[1]);
                        break;
                    }
                }
                if (this.blockAim != null) {
                    break;
                }
            }
            if (this.blockAim == null) {
                this.noSpace = false;
                return;
            }
        }
        if (this.isToggleMsg()) {
            if (this.stackCount.getValue() == 0) {
                PistonCrystal.printDebug("Starting farming obby", false);
            }
            else {
                PistonCrystal.printDebug(String.format("N^obby: %d, N^stack: %d, echest needed: %d", obbyCount, stackWanted, this.echestToMine), false);
            }
        }
        this.slotPick = InventoryUtil.findFirstItemSlot(Items.field_151046_w.getClass(), 0, 9);
        if (this.offHandEchest.getValue()) {
            this.slotObby = 11;
            OffHand.requestItems(2);
            EchestFarmer.mc.field_71439_g.field_71071_by.field_70461_c = this.slotPick;
            EchestFarmer.mc.field_71442_b.func_78765_e();
        }
        else {
            this.slotObby = InventoryUtil.findFirstBlockSlot(Blocks.field_150477_bB.getClass(), 0, 9);
        }
        if (this.slotObby == -1 || this.slotPick == -1) {
            this.materialsNeeded = false;
        }
    }
    
    public void onDisable() {
        String output = "";
        if (!this.materialsNeeded) {
            output = "No materials detected... " + ((this.slotObby == -1) ? "No Echest detected " : "") + ((this.slotPick == -1) ? "No Pick detected" : "");
        }
        else if (this.noSpace) {
            output = "Not enough space";
        }
        else if (this.looking) {
            output = "Impossible to place";
        }
        if (!output.equals("")) {
            PistonCrystal.printDebug(output, true);
        }
        else if (this.echestToMine == 0) {
            PistonCrystal.printDebug("Mined every echest", false);
        }
        if (this.offHandEchest.getValue()) {
            OffHand.removeItem(2);
        }
    }
    
    @Override
    public void onUpdate() {
        if (EchestFarmer.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        if (this.delayTimeTicks < this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
            return;
        }
        this.delayTimeTicks = 0;
        if (this.blockAim == null || !this.materialsNeeded || this.slotPick == -1 || this.looking || this.noSpace) {
            this.disable();
            return;
        }
        if (BlockUtil.getBlock(this.blockAim) instanceof BlockAir) {
            if (this.prevBreak && --this.echestToMine == 0) {
                this.disable();
                return;
            }
            this.placeBlock(this.blockAim);
            this.prevBreak = false;
        }
        else {
            if (EchestFarmer.mc.field_71439_g.field_71071_by.field_70461_c != this.slotPick) {
                EchestFarmer.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(this.slotPick));
                EchestFarmer.mc.field_71439_g.field_71071_by.field_70461_c = this.slotPick;
                EchestFarmer.mc.field_71442_b.func_78765_e();
            }
            final EnumFacing sideBreak = BlockUtil.getPlaceableSide(this.blockAim);
            if (sideBreak != null) {
                final String s = this.breakBlock.getValue();
                switch (s) {
                    case "Packet": {
                        if (!this.prevBreak) {
                            EchestFarmer.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                            EchestFarmer.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.blockAim, sideBreak));
                            EchestFarmer.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockAim, sideBreak));
                            this.prevBreak = true;
                            break;
                        }
                        break;
                    }
                    case "Normal": {
                        EchestFarmer.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                        EchestFarmer.mc.field_71442_b.func_180512_c(this.blockAim, sideBreak);
                        this.prevBreak = true;
                        break;
                    }
                }
            }
        }
    }
    
    private void placeBlock(final BlockPos pos) {
        EnumHand handSwing;
        if (this.slotObby == 11) {
            handSwing = EnumHand.OFF_HAND;
        }
        else {
            handSwing = EnumHand.MAIN_HAND;
            if (EchestFarmer.mc.field_71439_g.field_71071_by.field_70461_c != this.slotObby) {
                EchestFarmer.mc.field_71439_g.field_71071_by.field_70461_c = this.slotObby;
                EchestFarmer.mc.field_71442_b.func_78765_e();
            }
        }
        if ((EchestFarmer.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemBlock && ((ItemBlock)EchestFarmer.mc.field_71439_g.func_184614_ca().func_77973_b()).func_179223_d() != Blocks.field_150477_bB) || (EchestFarmer.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemBlock && ((ItemBlock)EchestFarmer.mc.field_71439_g.func_184592_cb().func_77973_b()).func_179223_d() != Blocks.field_150477_bB)) {
            return;
        }
        if (this.forceRotation.getValue()) {
            final EnumFacing side = BlockUtil.getPlaceableSide(this.blockAim);
            if (side == null) {
                return;
            }
            final BlockPos neighbour = this.blockAim.func_177972_a(side);
            final EnumFacing opposite = side.func_176734_d();
            if (!BlockUtil.canBeClicked(neighbour)) {
                return;
            }
            this.lastHitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        }
        PlacementUtil.place(pos, handSwing, this.rotate.getValue(), true);
    }
}
