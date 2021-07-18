// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.event.events;

import net.minecraft.util.math.BlockPos;
import com.gamesense.api.event.GameSenseEvent;

public class DestroyBlockEvent extends GameSenseEvent
{
    private BlockPos blockPos;
    
    public DestroyBlockEvent(final BlockPos blockPos) {
        this.blockPos = blockPos;
    }
    
    public BlockPos getBlockPos() {
        return this.blockPos;
    }
    
    public void setBlockPos(final BlockPos blockPos) {
        this.blockPos = blockPos;
    }
}
