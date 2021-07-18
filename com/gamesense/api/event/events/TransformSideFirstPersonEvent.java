// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.event.events;

import net.minecraft.util.EnumHandSide;
import com.gamesense.api.event.GameSenseEvent;

public class TransformSideFirstPersonEvent extends GameSenseEvent
{
    private final EnumHandSide enumHandSide;
    
    public TransformSideFirstPersonEvent(final EnumHandSide enumHandSide) {
        this.enumHandSide = enumHandSide;
    }
    
    public EnumHandSide getEnumHandSide() {
        return this.enumHandSide;
    }
}
