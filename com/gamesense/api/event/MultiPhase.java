// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.event;

public interface MultiPhase<T extends GameSenseEvent>
{
    Phase getPhase();
    
    T nextPhase();
}
