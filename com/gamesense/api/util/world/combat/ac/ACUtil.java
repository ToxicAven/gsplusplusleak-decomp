// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.world.combat.ac;

import net.minecraft.entity.item.EntityEnderCrystal;
import java.util.Iterator;
import com.gamesense.api.util.world.combat.DamageUtil;
import net.minecraft.util.math.BlockPos;
import java.util.List;

public class ACUtil
{
    public static CrystalInfo.PlaceInfo calculateBestPlacement(final ACSettings settings, final PlayerInfo target, final List<BlockPos> possibleLocations) {
        final double x = settings.playerPos.field_72450_a;
        final double y = settings.playerPos.field_72448_b;
        final double z = settings.playerPos.field_72449_c;
        BlockPos best = null;
        float bestDamage = 0.0f;
        for (final BlockPos crystal : possibleLocations) {
            if (target.entity.func_70092_e(crystal.func_177958_n() + 0.5, crystal.func_177956_o() + 1.0, crystal.func_177952_p() + 0.5) <= settings.enemyRangeSq) {
                final float currentDamage = DamageUtil.calculateDamageThreaded(crystal.func_177958_n() + 0.5, crystal.func_177956_o() + 1.0, crystal.func_177952_p() + 0.5, target);
                if (currentDamage == bestDamage) {
                    if (best != null && crystal.func_177954_c(x, y, z) >= best.func_177954_c(x, y, z)) {
                        continue;
                    }
                    bestDamage = currentDamage;
                    best = crystal;
                }
                else {
                    if (currentDamage <= bestDamage) {
                        continue;
                    }
                    bestDamage = currentDamage;
                    best = crystal;
                }
            }
        }
        if (best != null && (bestDamage >= settings.minDamage || ((target.health <= settings.facePlaceHealth || target.lowArmour) && bestDamage >= settings.minFacePlaceDamage))) {
            return new CrystalInfo.PlaceInfo(bestDamage, target, best);
        }
        return null;
    }
    
    public static CrystalInfo.BreakInfo calculateBestBreakable(final ACSettings settings, final PlayerInfo target, final List<EntityEnderCrystal> crystals) {
        final double x = settings.playerPos.field_72450_a;
        final double y = settings.playerPos.field_72448_b;
        final double z = settings.playerPos.field_72449_c;
        final boolean smart = settings.breakMode.equalsIgnoreCase("Smart");
        EntityEnderCrystal best = null;
        float bestDamage = 0.0f;
        for (final EntityEnderCrystal crystal : crystals) {
            final float currentDamage = DamageUtil.calculateDamageThreaded(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v, target);
            if (currentDamage == bestDamage) {
                if (best != null && crystal.func_70092_e(x, y, z) >= best.func_70092_e(x, y, z)) {
                    continue;
                }
                bestDamage = currentDamage;
                best = crystal;
            }
            else {
                if (currentDamage <= bestDamage) {
                    continue;
                }
                bestDamage = currentDamage;
                best = crystal;
            }
        }
        if (best != null) {
            boolean shouldAdd = false;
            if (smart) {
                if (bestDamage >= (double)settings.minBreakDamage || ((target.health <= settings.facePlaceHealth || target.lowArmour) && bestDamage > settings.minFacePlaceDamage)) {
                    shouldAdd = true;
                }
            }
            else {
                shouldAdd = true;
            }
            if (shouldAdd) {
                return new CrystalInfo.BreakInfo(bestDamage, target, best);
            }
        }
        return null;
    }
}
