// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import com.gamesense.client.module.modules.render.NoRender;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.ViewModel;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.gamesense.client.GameSense;
import com.gamesense.api.event.events.TransformSideFirstPersonEvent;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.util.EnumHandSide;
import net.minecraft.client.renderer.ItemRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ ItemRenderer.class })
public class MixinItemRenderer
{
    @Inject(method = { "transformSideFirstPerson" }, at = { @At("HEAD") })
    public void transformSideFirstPerson(final EnumHandSide hand, final float p_187459_2_, final CallbackInfo callbackInfo) {
        final TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
        GameSense.EVENT_BUS.post(event);
    }
    
    @Inject(method = { "transformEatFirstPerson" }, at = { @At("HEAD") }, cancellable = true)
    public void transformEatFirstPerson(final float p_187454_1_, final EnumHandSide hand, final ItemStack stack, final CallbackInfo callbackInfo) {
        final TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
        GameSense.EVENT_BUS.post(event);
        final ViewModel viewModel = ModuleManager.getModule(ViewModel.class);
        if (viewModel.isEnabled() && viewModel.cancelEating.getValue()) {
            callbackInfo.cancel();
        }
    }
    
    @Inject(method = { "transformFirstPerson" }, at = { @At("HEAD") })
    public void transformFirstPerson(final EnumHandSide hand, final float p_187453_2_, final CallbackInfo callbackInfo) {
        final TransformSideFirstPersonEvent event = new TransformSideFirstPersonEvent(hand);
        GameSense.EVENT_BUS.post(event);
    }
    
    @Inject(method = { "renderOverlays" }, at = { @At("HEAD") }, cancellable = true)
    public void renderOverlays(final float partialTicks, final CallbackInfo callbackInfo) {
        final NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && noRender.noOverlay.getValue()) {
            callbackInfo.cancel();
        }
    }
}
