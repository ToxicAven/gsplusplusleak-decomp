// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;

public class RenderEvent extends GameSenseEvent
{
    private final float partialTicks;
    
    public RenderEvent(final float partialTicks) {
        this.partialTicks = partialTicks;
    }
    
    public float getPartialTicks() {
        return this.partialTicks;
    }
}
