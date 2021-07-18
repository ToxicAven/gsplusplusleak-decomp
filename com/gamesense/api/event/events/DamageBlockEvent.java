// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.event.events;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import com.gamesense.api.event.GameSenseEvent;

public class DamageBlockEvent extends GameSenseEvent
{
    private BlockPos blockPos;
    private EnumFacing enumFacing;
    
    public DamageBlockEvent(final BlockPos blockPos, final EnumFacing enumFacing) {
        this.blockPos = blockPos;
        this.enumFacing = enumFacing;
    }
    
    public BlockPos getBlockPos() {
        return this.blockPos;
    }
    
    public void setBlockPos(final BlockPos blockPos) {
        this.blockPos = blockPos;
    }
    
    public EnumFacing getEnumFacing() {
        return this.enumFacing;
    }
    
    public void setEnumFacing(final EnumFacing enumFacing) {
        this.enumFacing = enumFacing;
    }
}
