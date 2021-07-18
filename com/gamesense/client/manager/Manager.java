// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.manager;

import net.minecraft.profiler.Profiler;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.Minecraft;

public interface Manager
{
    default Minecraft getMinecraft() {
        return Minecraft.func_71410_x();
    }
    
    default EntityPlayerSP getPlayer() {
        return this.getMinecraft().field_71439_g;
    }
    
    default WorldClient getWorld() {
        return this.getMinecraft().field_71441_e;
    }
    
    default Profiler getProfiler() {
        return this.getMinecraft().field_71424_I;
    }
}
