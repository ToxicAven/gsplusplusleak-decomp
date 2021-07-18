// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.world.combat.ac;

import java.util.Iterator;
import net.minecraft.item.ItemStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.potion.Potion;

public class PlayerInfo
{
    private static final Potion RESISTANCE;
    private static final DamageSource EXPLOSION_SOURCE;
    public final EntityPlayer entity;
    public final float totalArmourValue;
    public final float armourToughness;
    public final float health;
    public final int enchantModifier;
    public final boolean hasResistance;
    public final boolean lowArmour;
    
    public PlayerInfo(final EntityPlayer entity, final float armorPercent) {
        this.entity = entity;
        this.totalArmourValue = (float)entity.func_70658_aO();
        this.armourToughness = (float)entity.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e();
        this.health = entity.func_110143_aJ() + entity.func_110139_bj();
        this.enchantModifier = EnchantmentHelper.func_77508_a(entity.func_184193_aE(), PlayerInfo.EXPLOSION_SOURCE);
        this.hasResistance = entity.func_70644_a(PlayerInfo.RESISTANCE);
        boolean i = false;
        for (final ItemStack stack : entity.func_184193_aE()) {
            if (1.0f - stack.func_77952_i() / (float)stack.func_77958_k() < armorPercent) {
                i = true;
                break;
            }
        }
        this.lowArmour = i;
    }
    
    public PlayerInfo(final EntityPlayer entity, final boolean lowArmour) {
        this.entity = entity;
        this.totalArmourValue = (float)entity.func_70658_aO();
        this.armourToughness = (float)entity.func_110148_a(SharedMonsterAttributes.field_189429_h).func_111126_e();
        this.health = entity.func_110143_aJ() + entity.func_110139_bj();
        this.enchantModifier = EnchantmentHelper.func_77508_a(entity.func_184193_aE(), PlayerInfo.EXPLOSION_SOURCE);
        this.hasResistance = entity.func_70644_a(PlayerInfo.RESISTANCE);
        this.lowArmour = lowArmour;
    }
    
    static {
        RESISTANCE = Potion.func_188412_a(11);
        EXPLOSION_SOURCE = new DamageSource("explosion").func_76351_m().func_94540_d();
    }
}
