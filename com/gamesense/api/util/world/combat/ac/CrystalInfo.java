// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.world.combat.ac;

import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.item.EntityEnderCrystal;

public class CrystalInfo
{
    public final float damage;
    public final PlayerInfo target;
    
    private CrystalInfo(final float damage, final PlayerInfo target) {
        this.damage = damage;
        this.target = target;
    }
    
    public static class BreakInfo extends CrystalInfo
    {
        public final EntityEnderCrystal crystal;
        
        public BreakInfo(final float damage, final PlayerInfo target, final EntityEnderCrystal crystal) {
            super(damage, target, null);
            this.crystal = crystal;
        }
    }
    
    public static class PlaceInfo extends CrystalInfo
    {
        public final BlockPos crystal;
        
        public PlaceInfo(final float damage, final PlayerInfo target, final BlockPos crystal) {
            super(damage, target, null);
            this.crystal = crystal;
        }
    }
}
