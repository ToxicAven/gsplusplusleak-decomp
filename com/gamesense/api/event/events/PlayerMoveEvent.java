// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.event.events;

import net.minecraft.entity.MoverType;
import com.gamesense.api.event.GameSenseEvent;

public class PlayerMoveEvent extends GameSenseEvent
{
    private MoverType type;
    private double x;
    private double y;
    private double z;
    
    public PlayerMoveEvent(final MoverType moverType, final double x, final double y, final double z) {
        this.type = moverType;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public MoverType getType() {
        return this.type;
    }
    
    public void setType(final MoverType type) {
        this.type = type;
    }
    
    public double getX() {
        return this.x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public void setX(final double x) {
        this.x = x;
    }
    
    public void setY(final double y) {
        this.y = y;
    }
    
    public void setZ(final double z) {
        this.z = z;
    }
}
