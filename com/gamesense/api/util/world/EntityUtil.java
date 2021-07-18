// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.world;

import com.gamesense.api.util.player.social.SocialManager;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockAir;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;

public class EntityUtil
{
    private static final Minecraft mc;
    
    public static Block isColliding(final double posX, final double posY, final double posZ) {
        Block block = null;
        if (EntityUtil.mc.field_71439_g != null) {
            final AxisAlignedBB bb = (EntityUtil.mc.field_71439_g.func_184187_bx() != null) ? EntityUtil.mc.field_71439_g.func_184187_bx().func_174813_aQ().func_191195_a(0.0, 0.0, 0.0).func_72317_d(posX, posY, posZ) : EntityUtil.mc.field_71439_g.func_174813_aQ().func_191195_a(0.0, 0.0, 0.0).func_72317_d(posX, posY, posZ);
            final int y = (int)bb.field_72338_b;
            for (int x = MathHelper.func_76128_c(bb.field_72340_a); x < MathHelper.func_76128_c(bb.field_72336_d) + 1; ++x) {
                for (int z = MathHelper.func_76128_c(bb.field_72339_c); z < MathHelper.func_76128_c(bb.field_72334_f) + 1; ++z) {
                    block = EntityUtil.mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
                }
            }
        }
        return block;
    }
    
    public static boolean isInLiquid() {
        if (EntityUtil.mc.field_71439_g == null) {
            return false;
        }
        if (EntityUtil.mc.field_71439_g.field_70143_R >= 3.0f) {
            return false;
        }
        boolean inLiquid = false;
        final AxisAlignedBB bb = (EntityUtil.mc.field_71439_g.func_184187_bx() != null) ? EntityUtil.mc.field_71439_g.func_184187_bx().func_174813_aQ() : EntityUtil.mc.field_71439_g.func_174813_aQ();
        final int y = (int)bb.field_72338_b;
        for (int x = MathHelper.func_76128_c(bb.field_72340_a); x < MathHelper.func_76128_c(bb.field_72336_d) + 1; ++x) {
            for (int z = MathHelper.func_76128_c(bb.field_72339_c); z < MathHelper.func_76128_c(bb.field_72334_f) + 1; ++z) {
                final Block block = EntityUtil.mc.field_71441_e.func_180495_p(new BlockPos(x, y, z)).func_177230_c();
                if (!(block instanceof BlockAir)) {
                    if (!(block instanceof BlockLiquid)) {
                        return false;
                    }
                    inLiquid = true;
                }
            }
        }
        return inLiquid;
    }
    
    public static void setTimer(final float speed) {
        Minecraft.func_71410_x().field_71428_T.field_194149_e = 50.0f / speed;
    }
    
    public static void resetTimer() {
        Minecraft.func_71410_x().field_71428_T.field_194149_e = 50.0f;
    }
    
    public static Vec3d getInterpolatedAmount(final Entity entity, final double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }
    
    public static Vec3d getInterpolatedPos(final Entity entity, final float ticks) {
        return new Vec3d(entity.field_70142_S, entity.field_70137_T, entity.field_70136_U).func_178787_e(getInterpolatedAmount(entity, ticks));
    }
    
    public static Vec3d getInterpolatedAmount(final Entity entity, final double x, final double y, final double z) {
        return new Vec3d((entity.field_70165_t - entity.field_70142_S) * x, (entity.field_70163_u - entity.field_70137_T) * y, (entity.field_70161_v - entity.field_70136_U) * z);
    }
    
    public static float clamp(float val, final float min, final float max) {
        if (val <= min) {
            val = min;
        }
        if (val >= max) {
            val = max;
        }
        return val;
    }
    
    public static List<BlockPos> getSphere(final BlockPos loc, final float r, final int h, final boolean hollow, final boolean sphere, final int plus_y) {
        final List<BlockPos> circleBlocks = new ArrayList<BlockPos>();
        final int cx = loc.func_177958_n();
        final int cy = loc.func_177956_o();
        final int cz = loc.func_177952_p();
        for (int x = cx - (int)r; x <= cx + r; ++x) {
            for (int z = cz - (int)r; z <= cz + r; ++z) {
                for (int y = sphere ? (cy - (int)r) : cy; y < (sphere ? (cy + r) : ((float)(cy + h))); ++y) {
                    final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? ((cy - y) * (cy - y)) : 0);
                    if (dist < r * r && (!hollow || dist >= (r - 1.0f) * (r - 1.0f))) {
                        final BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleBlocks.add(l);
                    }
                }
            }
        }
        return circleBlocks;
    }
    
    public static List<BlockPos> getSquare(final BlockPos pos1, final BlockPos pos2) {
        final List<BlockPos> squareBlocks = new ArrayList<BlockPos>();
        final int x1 = pos1.func_177958_n();
        final int y1 = pos1.func_177956_o();
        final int z1 = pos1.func_177952_p();
        final int x2 = pos2.func_177958_n();
        final int y2 = pos2.func_177956_o();
        final int z2 = pos2.func_177952_p();
        for (int x3 = Math.min(x1, x2); x3 <= Math.max(x1, x2); ++x3) {
            for (int z3 = Math.min(z1, z2); z3 <= Math.max(z1, z2); ++z3) {
                for (int y3 = Math.min(y1, y2); y3 <= Math.max(y1, y2); ++y3) {
                    squareBlocks.add(new BlockPos(x3, y3, z3));
                }
            }
        }
        return squareBlocks;
    }
    
    public static double[] calculateLookAt(final double px, final double py, final double pz, final Entity me) {
        double dirx = me.field_70165_t - px;
        double diry = me.field_70163_u - py;
        double dirz = me.field_70161_v - pz;
        final double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);
        dirx /= len;
        diry /= len;
        dirz /= len;
        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);
        pitch = pitch * 180.0 / 3.141592653589793;
        yaw = yaw * 180.0 / 3.141592653589793;
        yaw += 90.0;
        return new double[] { yaw, pitch };
    }
    
    public static boolean basicChecksEntity(final Entity pl) {
        return pl.func_70005_c_().equals(EntityUtil.mc.field_71439_g.func_70005_c_()) || SocialManager.isFriend(pl.func_70005_c_()) || pl.field_70128_L;
    }
    
    public static BlockPos getPosition(final Entity pl) {
        return new BlockPos(Math.floor(pl.field_70165_t), Math.floor(pl.field_70163_u), Math.floor(pl.field_70161_v));
    }
    
    public static List<BlockPos> getBlocksIn(final Entity pl) {
        final List<BlockPos> blocks = new ArrayList<BlockPos>();
        final AxisAlignedBB bb = pl.func_174813_aQ();
        for (double x = Math.floor(bb.field_72340_a); x < Math.ceil(bb.field_72336_d); ++x) {
            for (double y = Math.floor(bb.field_72338_b); y < Math.ceil(bb.field_72337_e); ++y) {
                for (double z = Math.floor(bb.field_72339_c); z < Math.ceil(bb.field_72334_f); ++z) {
                    blocks.add(new BlockPos(x, y, z));
                }
            }
        }
        return blocks;
    }
    
    static {
        mc = Minecraft.func_71410_x();
    }
}
