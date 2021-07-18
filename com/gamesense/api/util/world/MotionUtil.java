// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.world;

import net.minecraft.potion.Potion;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;

public class MotionUtil
{
    public static boolean isMoving(final EntityLivingBase entity) {
        return entity.field_191988_bg != 0.0f || entity.field_70702_br != 0.0f;
    }
    
    public static void setSpeed(final EntityLivingBase entity, final double speed) {
        final double[] dir = forward(speed);
        entity.field_70159_w = dir[0];
        entity.field_70179_y = dir[1];
    }
    
    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (Minecraft.func_71410_x().field_71439_g != null && Minecraft.func_71410_x().field_71439_g.func_70644_a(Potion.func_188412_a(1))) {
            final int amplifier = Minecraft.func_71410_x().field_71439_g.func_70660_b(Potion.func_188412_a(1)).func_76458_c();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }
    
    public static double[] forward(final double speed) {
        float forward = Minecraft.func_71410_x().field_71439_g.field_71158_b.field_192832_b;
        float side = Minecraft.func_71410_x().field_71439_g.field_71158_b.field_78902_a;
        float yaw = Minecraft.func_71410_x().field_71439_g.field_70126_B + (Minecraft.func_71410_x().field_71439_g.field_70177_z - Minecraft.func_71410_x().field_71439_g.field_70126_B) * Minecraft.func_71410_x().func_184121_ak();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += ((forward > 0.0f) ? -45 : 45);
            }
            else if (side < 0.0f) {
                yaw += ((forward > 0.0f) ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            }
            else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        final double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        final double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        final double posX = forward * speed * cos + side * speed * sin;
        final double posZ = forward * speed * sin - side * speed * cos;
        return new double[] { posX, posZ };
    }
}
