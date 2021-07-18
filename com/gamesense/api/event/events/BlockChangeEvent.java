// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.event.events;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import com.gamesense.api.event.GameSenseEvent;

public class BlockChangeEvent extends GameSenseEvent
{
    private final BlockPos position;
    private final Block block;
    
    public BlockChangeEvent(final BlockPos position, final Block block) {
        this.position = position;
        this.block = block;
    }
    
    public Block getBlock() {
        return this.block;
    }
    
    public BlockPos getPosition() {
        return this.position;
    }
}
