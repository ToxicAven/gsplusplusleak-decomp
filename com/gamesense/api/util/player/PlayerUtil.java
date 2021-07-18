// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.player;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import com.gamesense.api.util.world.BlockUtil;
import net.minecraft.block.BlockAir;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import com.gamesense.api.util.world.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.Minecraft;

public class PlayerUtil
{
    private static final Minecraft mc;
    
    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(PlayerUtil.mc.field_71439_g.field_70165_t), Math.floor(PlayerUtil.mc.field_71439_g.field_70163_u), Math.floor(PlayerUtil.mc.field_71439_g.field_70161_v));
    }
    
    public static EntityPlayer findClosestTarget(double rangeMax, final EntityPlayer aimTarget) {
        rangeMax *= rangeMax;
        final List<EntityPlayer> playerList = (List<EntityPlayer>)PlayerUtil.mc.field_71441_e.field_73010_i;
        EntityPlayer closestTarget = null;
        for (final EntityPlayer entityPlayer : playerList) {
            if (EntityUtil.basicChecksEntity((Entity)entityPlayer)) {
                continue;
            }
            if (aimTarget == null && PlayerUtil.mc.field_71439_g.func_70068_e((Entity)entityPlayer) <= rangeMax) {
                closestTarget = entityPlayer;
            }
            else {
                if (aimTarget == null || PlayerUtil.mc.field_71439_g.func_70068_e((Entity)entityPlayer) > rangeMax || PlayerUtil.mc.field_71439_g.func_70068_e((Entity)entityPlayer) >= PlayerUtil.mc.field_71439_g.func_70068_e((Entity)aimTarget)) {
                    continue;
                }
                closestTarget = entityPlayer;
            }
        }
        return closestTarget;
    }
    
    public static EntityPlayer findClosestTarget() {
        final List<EntityPlayer> playerList = (List<EntityPlayer>)PlayerUtil.mc.field_71441_e.field_73010_i;
        EntityPlayer closestTarget = null;
        for (final EntityPlayer entityPlayer : playerList) {
            if (EntityUtil.basicChecksEntity((Entity)entityPlayer)) {
                continue;
            }
            if (closestTarget == null) {
                closestTarget = entityPlayer;
            }
            else {
                if (PlayerUtil.mc.field_71439_g.func_70068_e((Entity)entityPlayer) >= PlayerUtil.mc.field_71439_g.func_70068_e((Entity)closestTarget)) {
                    continue;
                }
                closestTarget = entityPlayer;
            }
        }
        return closestTarget;
    }
    
    public static EntityPlayer findLookingPlayer(final double rangeMax) {
        final ArrayList<EntityPlayer> listPlayer = new ArrayList<EntityPlayer>();
        for (final EntityPlayer playerSin : PlayerUtil.mc.field_71441_e.field_73010_i) {
            if (EntityUtil.basicChecksEntity((Entity)playerSin)) {
                continue;
            }
            if (PlayerUtil.mc.field_71439_g.func_70032_d((Entity)playerSin) > rangeMax) {
                continue;
            }
            listPlayer.add(playerSin);
        }
        EntityPlayer target = null;
        final Vec3d positionEyes = PlayerUtil.mc.field_71439_g.func_174824_e(PlayerUtil.mc.func_184121_ak());
        final Vec3d rotationEyes = PlayerUtil.mc.field_71439_g.func_70676_i(PlayerUtil.mc.func_184121_ak());
        final int precision = 2;
        for (int i = 0; i < (int)rangeMax; ++i) {
            for (int j = precision; j > 0; --j) {
                for (final EntityPlayer targetTemp : listPlayer) {
                    final AxisAlignedBB playerBox = targetTemp.func_174813_aQ();
                    final double xArray = positionEyes.field_72450_a + rotationEyes.field_72450_a * i + rotationEyes.field_72450_a / j;
                    final double yArray = positionEyes.field_72448_b + rotationEyes.field_72448_b * i + rotationEyes.field_72448_b / j;
                    final double zArray = positionEyes.field_72449_c + rotationEyes.field_72449_c * i + rotationEyes.field_72449_c / j;
                    if (playerBox.field_72337_e >= yArray && playerBox.field_72338_b <= yArray && playerBox.field_72336_d >= xArray && playerBox.field_72340_a <= xArray && playerBox.field_72334_f >= zArray && playerBox.field_72339_c <= zArray) {
                        target = targetTemp;
                    }
                }
            }
        }
        return target;
    }
    
    public static float getHealth() {
        return PlayerUtil.mc.field_71439_g.func_110143_aJ() + PlayerUtil.mc.field_71439_g.func_110139_bj();
    }
    
    public static void centerPlayer(Vec3d centeredBlock) {
        final double xDeviation = Math.abs(centeredBlock.field_72450_a - PlayerUtil.mc.field_71439_g.field_70165_t);
        final double zDeviation = Math.abs(centeredBlock.field_72449_c - PlayerUtil.mc.field_71439_g.field_70161_v);
        if (xDeviation <= 0.1 && zDeviation <= 0.1) {
            centeredBlock = Vec3d.field_186680_a;
        }
        else {
            double newX = -2.0;
            double newZ = -2.0;
            final int xRel = (PlayerUtil.mc.field_71439_g.field_70165_t < 0.0) ? -1 : 1;
            final int zRel = (PlayerUtil.mc.field_71439_g.field_70161_v < 0.0) ? -1 : 1;
            if (BlockUtil.getBlock(PlayerUtil.mc.field_71439_g.field_70165_t, PlayerUtil.mc.field_71439_g.field_70163_u - 1.0, PlayerUtil.mc.field_71439_g.field_70161_v) instanceof BlockAir) {
                if (Math.abs(PlayerUtil.mc.field_71439_g.field_70165_t % 1.0) * 100.0 <= 30.0) {
                    newX = Math.round(PlayerUtil.mc.field_71439_g.field_70165_t - 0.3 * xRel) + 0.5 * -xRel;
                }
                else if (Math.abs(PlayerUtil.mc.field_71439_g.field_70165_t % 1.0) * 100.0 >= 70.0) {
                    newX = Math.round(PlayerUtil.mc.field_71439_g.field_70165_t + 0.3 * xRel) - 0.5 * -xRel;
                }
                if (Math.abs(PlayerUtil.mc.field_71439_g.field_70161_v % 1.0) * 100.0 <= 30.0) {
                    newZ = Math.round(PlayerUtil.mc.field_71439_g.field_70161_v - 0.3 * zRel) + 0.5 * -zRel;
                }
                else if (Math.abs(PlayerUtil.mc.field_71439_g.field_70161_v % 1.0) * 100.0 >= 70.0) {
                    newZ = Math.round(PlayerUtil.mc.field_71439_g.field_70161_v + 0.3 * zRel) - 0.5 * -zRel;
                }
            }
            if (newX == -2.0) {
                if (PlayerUtil.mc.field_71439_g.field_70165_t > Math.round(PlayerUtil.mc.field_71439_g.field_70165_t)) {
                    newX = Math.round(PlayerUtil.mc.field_71439_g.field_70165_t) + 0.5;
                }
                else if (PlayerUtil.mc.field_71439_g.field_70165_t < Math.round(PlayerUtil.mc.field_71439_g.field_70165_t)) {
                    newX = Math.round(PlayerUtil.mc.field_71439_g.field_70165_t) - 0.5;
                }
                else {
                    newX = PlayerUtil.mc.field_71439_g.field_70165_t;
                }
            }
            if (newZ == -2.0) {
                if (PlayerUtil.mc.field_71439_g.field_70161_v > Math.round(PlayerUtil.mc.field_71439_g.field_70161_v)) {
                    newZ = Math.round(PlayerUtil.mc.field_71439_g.field_70161_v) + 0.5;
                }
                else if (PlayerUtil.mc.field_71439_g.field_70161_v < Math.round(PlayerUtil.mc.field_71439_g.field_70161_v)) {
                    newZ = Math.round(PlayerUtil.mc.field_71439_g.field_70161_v) - 0.5;
                }
                else {
                    newZ = PlayerUtil.mc.field_71439_g.field_70161_v;
                }
            }
            PlayerUtil.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(newX, PlayerUtil.mc.field_71439_g.field_70163_u, newZ, true));
            PlayerUtil.mc.field_71439_g.func_70107_b(newX, PlayerUtil.mc.field_71439_g.field_70163_u, newZ);
        }
    }
    
    static {
        mc = Minecraft.func_71410_x();
    }
}
