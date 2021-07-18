// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.gamesense.client.GameSense;
import com.gamesense.api.event.events.EntityCollisionEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ Entity.class })
public class MixinEntity
{
    @Inject(method = { "applyEntityCollision" }, at = { @At("HEAD") }, cancellable = true)
    public void velocity(final Entity entityIn, final CallbackInfo ci) {
        final EntityCollisionEvent event = new EntityCollisionEvent();
        GameSense.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
