// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import com.gamesense.client.GameSense;
import com.gamesense.api.event.events.DrawBlockDamageEvent;
import net.minecraft.entity.Entity;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.BlockHighlight;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.RenderGlobal;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ RenderGlobal.class })
public class MixinRenderGlobal
{
    @Inject(method = { "drawSelectionBox" }, at = { @At("HEAD") }, cancellable = true)
    public void drawSelectionBox(final EntityPlayer player, final RayTraceResult movingObjectPositionIn, final int execute, final float partialTicks, final CallbackInfo callbackInfo) {
        if (ModuleManager.isModuleEnabled(BlockHighlight.class)) {
            callbackInfo.cancel();
        }
    }
    
    @Inject(method = { "drawBlockDamageTexture" }, at = { @At("HEAD") }, cancellable = true)
    public void drawBlockDamageTexture(final Tessellator tessellatorIn, final BufferBuilder bufferBuilderIn, final Entity entityIn, final float partialTicks, final CallbackInfo callbackInfo) {
        final DrawBlockDamageEvent drawBlockDamageEvent = new DrawBlockDamageEvent();
        GameSense.EVENT_BUS.post(drawBlockDamageEvent);
        if (drawBlockDamageEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}
