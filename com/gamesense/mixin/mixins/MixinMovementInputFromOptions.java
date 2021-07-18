// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.Minecraft;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.PlayerTweaks;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MovementInputFromOptions;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.util.MovementInput;

@Mixin(value = { MovementInputFromOptions.class }, priority = 10000)
public abstract class MixinMovementInputFromOptions extends MovementInput
{
    @Redirect(method = { "updatePlayerMoveState" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/settings/KeyBinding;isKeyDown()Z"))
    public boolean isKeyPressed(final KeyBinding keyBinding) {
        final int keyCode = keyBinding.func_151463_i();
        if (keyCode > 0 && keyCode < 256) {
            final PlayerTweaks playerTweaks = ModuleManager.getModule(PlayerTweaks.class);
            if (playerTweaks.isEnabled() && playerTweaks.guiMove.getValue() && Minecraft.func_71410_x().field_71462_r != null && !(Minecraft.func_71410_x().field_71462_r instanceof GuiChat)) {
                return Keyboard.isKeyDown(keyCode);
            }
        }
        return keyBinding.func_151470_d();
    }
}
