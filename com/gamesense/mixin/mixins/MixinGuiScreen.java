// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.item.ItemShulkerBox;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.ShulkerViewer;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ GuiScreen.class })
public class MixinGuiScreen
{
    @Inject(method = { "renderToolTip" }, at = { @At("HEAD") }, cancellable = true)
    public void renderToolTip(final ItemStack stack, final int x, final int y, final CallbackInfo callbackInfo) {
        final ShulkerViewer shulkerViewer = ModuleManager.getModule(ShulkerViewer.class);
        if (shulkerViewer.isEnabled() && stack.func_77973_b() instanceof ItemShulkerBox && stack.func_77978_p() != null && stack.func_77978_p().func_150297_b("BlockEntityTag", 10) && stack.func_77978_p().func_74775_l("BlockEntityTag").func_150297_b("Items", 9)) {
            callbackInfo.cancel();
            shulkerViewer.renderShulkerPreview(stack, x + 6, y - 33, 162, 66);
        }
    }
}
