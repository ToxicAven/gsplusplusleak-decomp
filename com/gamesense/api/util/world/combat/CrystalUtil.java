// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.world.combat;

import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.player.PlayerUtil;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.Minecraft;

public class CrystalUtil
{
    private static final Minecraft mc;
    
    public static boolean canPlaceCrystal(final BlockPos blockPos, final boolean newPlacement) {
        if (notValidBlock(CrystalUtil.mc.field_71441_e.func_180495_p(blockPos).func_177230_c())) {
            return false;
        }
        final BlockPos posUp = blockPos.func_177984_a();
        if (newPlacement) {
            if (!CrystalUtil.mc.field_71441_e.func_175623_d(posUp)) {
                return false;
            }
        }
        else if (notValidMaterial(CrystalUtil.mc.field_71441_e.func_180495_p(posUp).func_185904_a()) || notValidMaterial(CrystalUtil.mc.field_71441_e.func_180495_p(posUp.func_177984_a()).func_185904_a())) {
            return false;
        }
        final AxisAlignedBB box = new AxisAlignedBB((double)posUp.func_177958_n(), (double)posUp.func_177956_o(), (double)posUp.func_177952_p(), posUp.func_177958_n() + 1.0, posUp.func_177956_o() + 2.0, posUp.func_177952_p() + 1.0);
        return CrystalUtil.mc.field_71441_e.func_175647_a((Class)Entity.class, box, Entity::func_70089_S).isEmpty();
    }
    
    public static boolean canPlaceCrystalExcludingCrystals(final BlockPos blockPos, final boolean newPlacement) {
        if (notValidBlock(CrystalUtil.mc.field_71441_e.func_180495_p(blockPos).func_177230_c())) {
            return false;
        }
        final BlockPos posUp = blockPos.func_177984_a();
        if (newPlacement) {
            if (!CrystalUtil.mc.field_71441_e.func_175623_d(posUp)) {
                return false;
            }
        }
        else if (notValidMaterial(CrystalUtil.mc.field_71441_e.func_180495_p(posUp).func_185904_a()) || notValidMaterial(CrystalUtil.mc.field_71441_e.func_180495_p(posUp.func_177984_a()).func_185904_a())) {
            return false;
        }
        final AxisAlignedBB box = new AxisAlignedBB((double)posUp.func_177958_n(), (double)posUp.func_177956_o(), (double)posUp.func_177952_p(), posUp.func_177958_n() + 1.0, posUp.func_177956_o() + 2.0, posUp.func_177952_p() + 1.0);
        return CrystalUtil.mc.field_71441_e.func_175647_a((Class)Entity.class, box, entity -> !entity.field_70128_L && !(entity instanceof EntityEnderCrystal)).isEmpty();
    }
    
    public static boolean notValidBlock(final Block block) {
        return block != Blocks.field_150357_h && block != Blocks.field_150343_Z;
    }
    
    public static boolean notValidMaterial(final Material material) {
        return material.func_76224_d() || !material.func_76222_j();
    }
    
    public static List<BlockPos> findCrystalBlocks(final float placeRange, final boolean mode) {
        return EntityUtil.getSphere(PlayerUtil.getPlayerPos(), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystal(pos, mode)).collect((Collector<? super Object, ?, List<BlockPos>>)Collectors.toList());
    }
    
    public static List<BlockPos> findCrystalBlocksExcludingCrystals(final float placeRange, final boolean mode) {
        return EntityUtil.getSphere(PlayerUtil.getPlayerPos(), placeRange, (int)placeRange, false, true, 0).stream().filter(pos -> canPlaceCrystalExcludingCrystals(pos, mode)).collect((Collector<? super Object, ?, List<BlockPos>>)Collectors.toList());
    }
    
    public static void breakCrystal(final Entity crystal) {
        CrystalUtil.mc.field_71442_b.func_78764_a((EntityPlayer)CrystalUtil.mc.field_71439_g, crystal);
        CrystalUtil.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
    }
    
    public static void breakCrystalPacket(final Entity crystal) {
        CrystalUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketUseEntity(crystal));
        CrystalUtil.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
    }
    
    static {
        mc = Minecraft.func_71410_x();
    }
}
