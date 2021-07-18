// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.gamesense.client.GameSense;
import com.gamesense.api.event.events.RenderEntityEvent;
import net.minecraft.client.renderer.GlStateManager;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.NoRender;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.EntityLivingBase;

@Mixin({ RenderLivingBase.class })
public abstract class MixinRenderLivingBase<T extends EntityLivingBase> extends Render<T>
{
    protected final Minecraft mc;
    private boolean isClustered;
    
    protected MixinRenderLivingBase() {
        super((RenderManager)null);
        this.mc = Minecraft.func_71410_x();
    }
    
    @Inject(method = { "renderModel" }, at = { @At("HEAD") }, cancellable = true)
    protected void renderModel(final T entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final CallbackInfo callbackInfo) {
        if (!this.func_180548_c((Entity)entitylivingbaseIn)) {
            return;
        }
        final NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && noRender.noCluster.getValue() && this.mc.field_71439_g.func_70032_d((Entity)entitylivingbaseIn) < 1.0f && entitylivingbaseIn != this.mc.field_71439_g) {
            GlStateManager.func_187408_a(GlStateManager.Profile.TRANSPARENT_MODEL);
            this.isClustered = true;
            if (!noRender.incrementNoClusterRender()) {
                callbackInfo.cancel();
            }
        }
        else {
            this.isClustered = false;
        }
        final RenderEntityEvent.Head renderEntityHeadEvent = new RenderEntityEvent.Head((Entity)entitylivingbaseIn, RenderEntityEvent.Type.COLOR);
        GameSense.EVENT_BUS.post(renderEntityHeadEvent);
        if (renderEntityHeadEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
    
    @Inject(method = { "renderModel" }, at = { @At("RETURN") }, cancellable = true)
    protected void renderModelReturn(final T entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleFactor, final CallbackInfo callbackInfo) {
        final RenderEntityEvent.Return renderEntityReturnEvent = new RenderEntityEvent.Return((Entity)entitylivingbaseIn, RenderEntityEvent.Type.COLOR);
        GameSense.EVENT_BUS.post(renderEntityReturnEvent);
        if (!renderEntityReturnEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }
    
    @Inject(method = { "renderLayers" }, at = { @At("HEAD") }, cancellable = true)
    protected void renderLayers(final T entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTicks, final float ageInTicks, final float netHeadYaw, final float headPitch, final float scaleIn, final CallbackInfo callbackInfo) {
        if (this.isClustered && !ModuleManager.getModule(NoRender.class).getNoClusterRender()) {
            callbackInfo.cancel();
        }
    }
    
    @Redirect(method = { "setBrightness" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V", ordinal = 6))
    protected void glTexEnvi0(int target, final int parameterName, final int parameter) {
        if (!this.isClustered) {
            GlStateManager.func_187399_a(target, parameterName, parameter);
        }
    }
    
    @Redirect(method = { "setBrightness" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V", ordinal = 7))
    protected void glTexEnvi1(int target, final int parameterName, final int parameter) {
        if (!this.isClustered) {
            GlStateManager.func_187399_a(target, parameterName, parameter);
        }
    }
    
    @Redirect(method = { "setBrightness" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;glTexEnvi(III)V", ordinal = 8))
    protected void glTexEnvi2(int target, final int parameterName, final int parameter) {
        if (!this.isClustered) {
            GlStateManager.func_187399_a(target, parameterName, parameter);
        }
    }
}
