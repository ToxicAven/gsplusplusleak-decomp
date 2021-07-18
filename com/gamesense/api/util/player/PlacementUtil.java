// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.player;

import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.EnumActionResult;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoCrystal;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import com.gamesense.api.util.world.BlockUtil;
import net.minecraft.util.EnumFacing;
import java.util.ArrayList;
import net.minecraft.item.Item;
import net.minecraft.block.Block;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.client.Minecraft;

public class PlacementUtil
{
    private static final Minecraft mc;
    private static int placementConnections;
    private static boolean isSneaking;
    
    public static void onEnable() {
        ++PlacementUtil.placementConnections;
    }
    
    public static void onDisable() {
        --PlacementUtil.placementConnections;
        if (PlacementUtil.placementConnections == 0 && PlacementUtil.isSneaking) {
            PlacementUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)PlacementUtil.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            PlacementUtil.isSneaking = false;
        }
    }
    
    public static boolean placeBlock(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final Class<? extends Block> blockToPlace) {
        final int oldSlot = PlacementUtil.mc.field_71439_g.field_71071_by.field_70461_c;
        final int newSlot = InventoryUtil.findFirstBlockSlot(blockToPlace, 0, 8);
        if (newSlot == -1) {
            return false;
        }
        PlacementUtil.mc.field_71439_g.field_71071_by.field_70461_c = newSlot;
        final boolean output = place(blockPos, hand, rotate);
        PlacementUtil.mc.field_71439_g.field_71071_by.field_70461_c = oldSlot;
        return output;
    }
    
    public static boolean placeItem(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final Class<? extends Item> itemToPlace) {
        final int oldSlot = PlacementUtil.mc.field_71439_g.field_71071_by.field_70461_c;
        final int newSlot = InventoryUtil.findFirstItemSlot(itemToPlace, 0, 8);
        if (newSlot == -1) {
            return false;
        }
        PlacementUtil.mc.field_71439_g.field_71071_by.field_70461_c = newSlot;
        final boolean output = place(blockPos, hand, rotate);
        PlacementUtil.mc.field_71439_g.field_71071_by.field_70461_c = oldSlot;
        return output;
    }
    
    public static boolean place(final BlockPos blockPos, final EnumHand hand, final boolean rotate) {
        return placeBlock(blockPos, hand, rotate, true, null);
    }
    
    public static boolean place(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final ArrayList<EnumFacing> forceSide) {
        return placeBlock(blockPos, hand, rotate, true, forceSide);
    }
    
    public static boolean place(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final boolean checkAction) {
        return placeBlock(blockPos, hand, rotate, checkAction, null);
    }
    
    public static boolean placeBlock(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final boolean checkAction, final ArrayList<EnumFacing> forceSide) {
        final EntityPlayerSP player = PlacementUtil.mc.field_71439_g;
        final WorldClient world = PlacementUtil.mc.field_71441_e;
        final PlayerControllerMP playerController = PlacementUtil.mc.field_71442_b;
        if (player == null || world == null || playerController == null) {
            return false;
        }
        if (!world.func_180495_p(blockPos).func_185904_a().func_76222_j()) {
            return false;
        }
        final EnumFacing side = (forceSide != null) ? BlockUtil.getPlaceableSideExlude(blockPos, forceSide) : BlockUtil.getPlaceableSide(blockPos);
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = blockPos.func_177972_a(side);
        final EnumFacing opposite = side.func_176734_d();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        final Block neighbourBlock = world.func_180495_p(neighbour).func_177230_c();
        if ((!PlacementUtil.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            player.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)player, CPacketEntityAction.Action.START_SNEAKING));
            PlacementUtil.isSneaking = true;
        }
        boolean stoppedAC = false;
        if (ModuleManager.isModuleEnabled(AutoCrystal.class)) {
            AutoCrystal.stopAC = true;
            stoppedAC = true;
        }
        if (rotate) {
            BlockUtil.faceVectorPacketInstant(hitVec, true);
        }
        final EnumActionResult action = playerController.func_187099_a(player, world, neighbour, opposite, hitVec, hand);
        if (!checkAction || action == EnumActionResult.SUCCESS) {
            player.func_184609_a(hand);
            PlacementUtil.mc.field_71467_ac = 4;
        }
        if (stoppedAC) {
            AutoCrystal.stopAC = false;
        }
        return action == EnumActionResult.SUCCESS;
    }
    
    public static boolean placePrecise(final BlockPos blockPos, final EnumHand hand, final boolean rotate, final Vec3d precise, final EnumFacing forceSide, final boolean onlyRotation, final boolean support) {
        final EntityPlayerSP player = PlacementUtil.mc.field_71439_g;
        final WorldClient world = PlacementUtil.mc.field_71441_e;
        final PlayerControllerMP playerController = PlacementUtil.mc.field_71442_b;
        if (player == null || world == null || playerController == null) {
            return false;
        }
        if (!world.func_180495_p(blockPos).func_185904_a().func_76222_j()) {
            return false;
        }
        final EnumFacing side = (forceSide == null) ? BlockUtil.getPlaceableSide(blockPos) : forceSide;
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = blockPos.func_177972_a(side);
        final EnumFacing opposite = side.func_176734_d();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        final Block neighbourBlock = world.func_180495_p(neighbour).func_177230_c();
        if ((!PlacementUtil.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            player.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)player, CPacketEntityAction.Action.START_SNEAKING));
            PlacementUtil.isSneaking = true;
        }
        boolean stoppedAC = false;
        if (ModuleManager.isModuleEnabled(AutoCrystal.class)) {
            AutoCrystal.stopAC = true;
            stoppedAC = true;
        }
        if (rotate && !support) {
            BlockUtil.faceVectorPacketInstant((precise == null) ? hitVec : precise, true);
        }
        if (!onlyRotation) {
            final EnumActionResult action = playerController.func_187099_a(player, world, neighbour, opposite, (precise == null) ? hitVec : precise, hand);
            if (action == EnumActionResult.SUCCESS) {
                player.func_184609_a(hand);
                PlacementUtil.mc.field_71467_ac = 4;
            }
            if (stoppedAC) {
                AutoCrystal.stopAC = false;
            }
            return action == EnumActionResult.SUCCESS;
        }
        return true;
    }
    
    static {
        mc = Minecraft.func_71410_x();
        PlacementUtil.placementConnections = 0;
        PlacementUtil.isSneaking = false;
    }
}
