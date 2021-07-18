// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins.accessor;

import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ EntityPlayerSP.class })
public interface AccessorEntityPlayerSP
{
    @Accessor("handActive")
    void gsSetHandActive(final boolean p0);
}
