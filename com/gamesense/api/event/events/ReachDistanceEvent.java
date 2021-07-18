// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.event.events;

import com.gamesense.api.event.GameSenseEvent;

public class ReachDistanceEvent extends GameSenseEvent
{
    private float distance;
    
    public ReachDistanceEvent(final float distance) {
        this.distance = distance;
    }
    
    public float getDistance() {
        return this.distance;
    }
    
    public void setDistance(final float distance) {
        this.distance = distance;
    }
}
