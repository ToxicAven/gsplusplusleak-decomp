// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.event.events;

import net.minecraft.entity.Entity;
import com.gamesense.api.event.GameSenseEvent;

public class RenderEntityEvent extends GameSenseEvent
{
    private final Entity entity;
    private final Type type;
    
    public RenderEntityEvent(final Entity entity, final Type type) {
        this.entity = entity;
        this.type = type;
    }
    
    public Entity getEntity() {
        return this.entity;
    }
    
    public Type getType() {
        return this.type;
    }
    
    public enum Type
    {
        TEXTURE, 
        COLOR;
    }
    
    public static class Head extends RenderEntityEvent
    {
        public Head(final Entity entity, final Type type) {
            super(entity, type);
        }
    }
    
    public static class Return extends RenderEntityEvent
    {
        public Return(final Entity entity, final Type type) {
            super(entity, type);
        }
    }
}
