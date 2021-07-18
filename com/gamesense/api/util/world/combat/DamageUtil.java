// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.world.combat;

import com.gamesense.api.util.world.combat.ac.PlayerInfo;
import net.minecraft.potion.Potion;
import net.minecraft.util.math.MathHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.CombatRules;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.DamageSource;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.Explosion;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;
import net.minecraft.entity.Entity;
import net.minecraft.client.Minecraft;

public class DamageUtil
{
    private static final Minecraft mc;
    
    public static float calculateDamage(final double posX, final double posY, final double posZ, final Entity entity) {
        float finalDamage = 1.0f;
        try {
            final float doubleExplosionSize = 12.0f;
            final double distancedSize = entity.func_70011_f(posX, posY, posZ) / doubleExplosionSize;
            final double blockDensity = entity.field_70170_p.func_72842_a(new Vec3d(posX, posY, posZ), entity.func_174813_aQ());
            final double v = (1.0 - distancedSize) * blockDensity;
            final float damage = (float)(int)((v * v + v) / 2.0 * 7.0 * doubleExplosionSize + 1.0);
            if (entity instanceof EntityLivingBase) {
                finalDamage = getBlastReduction((EntityLivingBase)entity, getDamageMultiplied(damage), new Explosion((World)DamageUtil.mc.field_71441_e, (Entity)null, posX, posY, posZ, 6.0f, false, true));
            }
        }
        catch (NullPointerException ex) {}
        return finalDamage;
    }
    
    public static float getBlastReduction(final EntityLivingBase entity, float damage, final Explosion explosion) {
        if (entity instanceof EntityPlayer) {
            final EntityPlayer ep = (EntityPlayer)entity;
            final DamageSource ds = DamageSource.func_94539_a(explosion);
            damage = CombatRules.func_189427_a(damage, (float)ep.func_70658_aO(), (float)ep.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
            final int k = EnchantmentHelper.func_77508_a(ep.func_184193_aE(), ds);
            final float f = MathHelper.func_76131_a((float)k, 0.0f, 20.0f);
            damage *= 1.0f - f / 25.0f;
            if (entity.func_70644_a(Potion.func_188412_a(11))) {
                damage -= damage / 4.0f;
            }
            damage = Math.max(damage, 0.0f);
            return damage;
        }
        damage = CombatRules.func_189427_a(damage, (float)entity.func_70658_aO(), (float)entity.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e());
        return damage;
    }
    
    public static float calculateDamageThreaded(final double posX, final double posY, final double posZ, final PlayerInfo playerInfo) {
        float finalDamage = 1.0f;
        try {
            final float doubleExplosionSize = 12.0f;
            final double distancedSize = playerInfo.entity.func_70011_f(posX, posY, posZ) / doubleExplosionSize;
            final double blockDensity = playerInfo.entity.field_70170_p.func_72842_a(new Vec3d(posX, posY, posZ), playerInfo.entity.func_174813_aQ());
            final double v = (1.0 - distancedSize) * blockDensity;
            final float damage = (float)(int)((v * v + v) / 2.0 * 7.0 * doubleExplosionSize + 1.0);
            finalDamage = getBlastReductionThreaded(playerInfo, getDamageMultiplied(damage));
        }
        catch (NullPointerException ex) {}
        return finalDamage;
    }
    
    public static float getBlastReductionThreaded(final PlayerInfo playerInfo, float damage) {
        damage = CombatRules.func_189427_a(damage, playerInfo.totalArmourValue, playerInfo.armourToughness);
        final float f = MathHelper.func_76131_a((float)playerInfo.enchantModifier, 0.0f, 20.0f);
        damage *= 1.0f - f / 25.0f;
        if (playerInfo.hasResistance) {
            damage -= damage / 4.0f;
        }
        damage = Math.max(damage, 0.0f);
        return damage;
    }
    
    private static float getDamageMultiplied(final float damage) {
        final int diff = DamageUtil.mc.field_71441_e.func_175659_aa().func_151525_a();
        return damage * ((diff == 0) ? 0.0f : ((diff == 2) ? 1.0f : ((diff == 1) ? 0.5f : 1.5f)));
    }
    
    static {
        mc = Minecraft.func_71410_x();
    }
}
