// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import com.gamesense.api.event.events.WaterPushEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.gamesense.api.event.events.PlayerJumpEvent;
import com.gamesense.client.GameSense;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.entity.player.EntityPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ EntityPlayer.class })
public abstract class MixinEntityPlayer
{
    @Shadow
    public abstract String func_70005_c_();
    
    @Inject(method = { "jump" }, at = { @At("HEAD") }, cancellable = true)
    public void onJump(final CallbackInfo callbackInfo) {
        if (Minecraft.func_71410_x().field_71439_g.func_70005_c_() == this.func_70005_c_()) {
            GameSense.EVENT_BUS.post(new PlayerJumpEvent());
        }
    }
    
    @Inject(method = { "isPushedByWater" }, at = { @At("HEAD") }, cancellable = true)
    private void onPushedByWater(final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final WaterPushEvent event = new WaterPushEvent();
        GameSense.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }
}
