// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.player;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.client.Minecraft;

public class RotationUtil
{
    private static final Minecraft mc;
    
    public static Vec2f getRotationTo(final AxisAlignedBB box) {
        final EntityPlayerSP player = RotationUtil.mc.field_71439_g;
        if (player == null) {
            return Vec2f.field_189974_a;
        }
        final Vec3d eyePos = player.func_174824_e(1.0f);
        if (player.func_174813_aQ().func_72326_a(box)) {
            return getRotationTo(eyePos, box.func_189972_c());
        }
        final double x = MathHelper.func_151237_a(eyePos.field_72450_a, box.field_72340_a, box.field_72336_d);
        final double y = MathHelper.func_151237_a(eyePos.field_72448_b, box.field_72338_b, box.field_72337_e);
        final double z = MathHelper.func_151237_a(eyePos.field_72449_c, box.field_72339_c, box.field_72334_f);
        return getRotationTo(eyePos, new Vec3d(x, y, z));
    }
    
    public static Vec2f getRotationTo(final Vec3d posTo) {
        final EntityPlayerSP player = RotationUtil.mc.field_71439_g;
        return (player != null) ? getRotationTo(player.func_174824_e(1.0f), posTo) : Vec2f.field_189974_a;
    }
    
    public static Vec2f getRotationTo(final Vec3d posFrom, final Vec3d posTo) {
        return getRotationFromVec(posTo.func_178788_d(posFrom));
    }
    
    public static Vec2f getRotationFromVec(final Vec3d vec) {
        final double lengthXZ = Math.hypot(vec.field_72450_a, vec.field_72449_c);
        final double yaw = normalizeAngle(Math.toDegrees(Math.atan2(vec.field_72449_c, vec.field_72450_a)) - 90.0);
        final double pitch = normalizeAngle(Math.toDegrees(-Math.atan2(vec.field_72448_b, lengthXZ)));
        return new Vec2f((float)yaw, (float)pitch);
    }
    
    public static double normalizeAngle(double angle) {
        angle %= 360.0;
        if (angle >= 180.0) {
            angle -= 360.0;
        }
        if (angle < -180.0) {
            angle += 360.0;
        }
        return angle;
    }
    
    public static float normalizeAngle(float angle) {
        angle %= 360.0f;
        if (angle >= 180.0f) {
            angle -= 360.0f;
        }
        if (angle < -180.0f) {
            angle += 360.0f;
        }
        return angle;
    }
    
    static {
        mc = Minecraft.func_71410_x();
    }
}
