// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.event.events;

import net.minecraft.entity.Entity;
import com.gamesense.api.event.GameSenseEvent;

public class TotemPopEvent extends GameSenseEvent
{
    private final Entity entity;
    
    public TotemPopEvent(final Entity entity) {
        this.entity = entity;
    }
    
    public Entity getEntity() {
        return this.entity;
    }
}
