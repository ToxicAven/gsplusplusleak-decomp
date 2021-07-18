// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.world;

import java.util.Arrays;
import net.minecraft.init.Blocks;
import java.util.ArrayList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.Minecraft;
import java.util.List;

public class BlockUtil
{
    public static final List blackList;
    public static final List shulkerList;
    private static final Minecraft mc;
    
    public static IBlockState getState(final BlockPos pos) {
        return BlockUtil.mc.field_71441_e.func_180495_p(pos);
    }
    
    public static boolean checkForNeighbours(final BlockPos blockPos) {
        if (!hasNeighbour(blockPos)) {
            for (final EnumFacing side : EnumFacing.values()) {
                final BlockPos neighbour = blockPos.func_177972_a(side);
                if (hasNeighbour(neighbour)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
    
    private static boolean hasNeighbour(final BlockPos blockPos) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = blockPos.func_177972_a(side);
            if (!BlockUtil.mc.field_71441_e.func_180495_p(neighbour).func_185904_a().func_76222_j()) {
                return true;
            }
        }
        return false;
    }
    
    public static Block getBlock(final BlockPos pos) {
        return getState(pos).func_177230_c();
    }
    
    public static Block getBlock(final double x, final double y, final double z) {
        return BlockUtil.mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
    }
    
    public static boolean canBeClicked(final BlockPos pos) {
        return getBlock(pos).func_176209_a(getState(pos), false);
    }
    
    public static void faceVectorPacketInstant(final Vec3d vec, final Boolean roundAngles) {
        final float[] rotations = getNeededRotations2(vec);
        BlockUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(rotations[0], ((boolean)roundAngles) ? ((float)MathHelper.func_180184_b((int)rotations[1], 360)) : rotations[1], BlockUtil.mc.field_71439_g.field_70122_E));
    }
    
    private static float[] getNeededRotations2(final Vec3d vec) {
        final Vec3d eyesPos = getEyesPos();
        final double diffX = vec.field_72450_a - eyesPos.field_72450_a;
        final double diffY = vec.field_72448_b - eyesPos.field_72448_b;
        final double diffZ = vec.field_72449_c - eyesPos.field_72449_c;
        final double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        final float yaw = (float)Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        final float pitch = (float)(-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[] { BlockUtil.mc.field_71439_g.field_70177_z + MathHelper.func_76142_g(yaw - BlockUtil.mc.field_71439_g.field_70177_z), BlockUtil.mc.field_71439_g.field_70125_A + MathHelper.func_76142_g(pitch - BlockUtil.mc.field_71439_g.field_70125_A) };
    }
    
    public static Vec3d getEyesPos() {
        return new Vec3d(BlockUtil.mc.field_71439_g.field_70165_t, BlockUtil.mc.field_71439_g.field_70163_u + BlockUtil.mc.field_71439_g.func_70047_e(), BlockUtil.mc.field_71439_g.field_70161_v);
    }
    
    public static List<BlockPos> getCircle(final BlockPos loc, final int y, final float r, final boolean hollow) {
        final List<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = loc.func_177958_n();
        final int cz = loc.func_177952_p();
        for (int x = cx - (int)r; x <= cx + r; ++x) {
            for (int z = cz - (int)r; z <= cz + r; ++z) {
                final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z);
                if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                    final BlockPos l = new BlockPos(x, y, z);
                    circleblocks.add(l);
                }
            }
        }
        return circleblocks;
    }
    
    public static EnumFacing getPlaceableSide(final BlockPos pos) {
        for (final EnumFacing side : EnumFacing.values()) {
            final BlockPos neighbour = pos.func_177972_a(side);
            if (BlockUtil.mc.field_71441_e.func_180495_p(neighbour).func_177230_c().func_176209_a(BlockUtil.mc.field_71441_e.func_180495_p(neighbour), false)) {
                final IBlockState blockState = BlockUtil.mc.field_71441_e.func_180495_p(neighbour);
                if (!blockState.func_185904_a().func_76222_j()) {
                    return side;
                }
            }
        }
        return null;
    }
    
    public static EnumFacing getPlaceableSideExlude(final BlockPos pos, final ArrayList<EnumFacing> excluding) {
        for (final EnumFacing side : EnumFacing.values()) {
            if (!excluding.contains(side)) {
                final BlockPos neighbour = pos.func_177972_a(side);
                if (BlockUtil.mc.field_71441_e.func_180495_p(neighbour).func_177230_c().func_176209_a(BlockUtil.mc.field_71441_e.func_180495_p(neighbour), false)) {
                    final IBlockState blockState = BlockUtil.mc.field_71441_e.func_180495_p(neighbour);
                    if (!blockState.func_185904_a().func_76222_j()) {
                        return side;
                    }
                }
            }
        }
        return null;
    }
    
    public static Vec3d getCenterOfBlock(final double playerX, final double playerY, final double playerZ) {
        final double newX = Math.floor(playerX) + 0.5;
        final double newY = Math.floor(playerY);
        final double newZ = Math.floor(playerZ) + 0.5;
        return new Vec3d(newX, newY, newZ);
    }
    
    static {
        mc = Minecraft.func_71410_x();
        blackList = Arrays.asList(Blocks.field_150477_bB, (Block)Blocks.field_150486_ae, Blocks.field_150447_bR, Blocks.field_150462_ai, Blocks.field_150467_bQ, Blocks.field_150382_bo, (Block)Blocks.field_150438_bZ, Blocks.field_150409_cd, Blocks.field_150367_z);
        shulkerList = Arrays.asList(Blocks.field_190977_dl, Blocks.field_190978_dm, Blocks.field_190979_dn, Blocks.field_190980_do, Blocks.field_190981_dp, Blocks.field_190982_dq, Blocks.field_190983_dr, Blocks.field_190984_ds, Blocks.field_190985_dt, Blocks.field_190986_du, Blocks.field_190987_dv, Blocks.field_190988_dw, Blocks.field_190989_dx, Blocks.field_190990_dy, Blocks.field_190991_dz, Blocks.field_190975_dA);
    }
}
