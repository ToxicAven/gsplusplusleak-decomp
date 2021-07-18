// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.gamesense.client.module.modules.hud.TargetInfo;
import net.minecraft.entity.player.EntityPlayer;
import com.gamesense.client.module.modules.hud.TargetHUD;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.Nametags;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ RenderPlayer.class })
public abstract class MixinRenderPlayer
{
    @Inject(method = { "renderEntityName" }, at = { @At("HEAD") }, cancellable = true)
    private void renderLivingLabel(final AbstractClientPlayer entity, final double x, final double y, final double z, final String name, final double distanceSq, final CallbackInfo callbackInfo) {
        if (ModuleManager.isModuleEnabled(Nametags.class)) {
            callbackInfo.cancel();
        }
        if (ModuleManager.isModuleEnabled(TargetHUD.class) && TargetHUD.isRenderingEntity((EntityPlayer)entity)) {
            callbackInfo.cancel();
        }
        if (ModuleManager.isModuleEnabled(TargetInfo.class) && TargetInfo.isRenderingEntity((EntityPlayer)entity)) {
            callbackInfo.cancel();
        }
    }
}
