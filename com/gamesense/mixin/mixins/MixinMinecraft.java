// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import com.gamesense.api.config.SaveConfig;
import net.minecraft.crash.CrashReport;
import com.gamesense.mixin.mixins.accessor.AccessorEntityPlayerSP;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.MultiTask;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ Minecraft.class })
public class MixinMinecraft
{
    @Shadow
    public EntityPlayerSP field_71439_g;
    @Shadow
    public PlayerControllerMP field_71442_b;
    private boolean handActive;
    private boolean isHittingBlock;
    
    public MixinMinecraft() {
        this.handActive = false;
        this.isHittingBlock = false;
    }
    
    @Inject(method = { "rightClickMouse" }, at = { @At("HEAD") })
    public void rightClickMousePre(final CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled(MultiTask.class)) {
            this.isHittingBlock = this.field_71442_b.func_181040_m();
            this.field_71442_b.field_78778_j = false;
        }
    }
    
    @Inject(method = { "rightClickMouse" }, at = { @At("RETURN") })
    public void rightClickMousePost(final CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled(MultiTask.class) && !this.field_71442_b.func_181040_m()) {
            this.field_71442_b.field_78778_j = this.isHittingBlock;
        }
    }
    
    @Inject(method = { "sendClickBlockToController" }, at = { @At("HEAD") })
    public void sendClickBlockToControllerPre(final boolean leftClick, final CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled(MultiTask.class)) {
            this.handActive = this.field_71439_g.func_184587_cr();
            ((AccessorEntityPlayerSP)this.field_71439_g).gsSetHandActive(false);
        }
    }
    
    @Inject(method = { "sendClickBlockToController" }, at = { @At("RETURN") })
    public void sendClickBlockToControllerPost(final boolean leftClick, final CallbackInfo ci) {
        if (ModuleManager.isModuleEnabled(MultiTask.class) && !this.field_71439_g.func_184587_cr()) {
            ((AccessorEntityPlayerSP)this.field_71439_g).gsSetHandActive(this.handActive);
        }
    }
    
    @Inject(method = { "crashed" }, at = { @At("HEAD") })
    public void crashed(final CrashReport crash, final CallbackInfo callbackInfo) {
        SaveConfig.init();
    }
    
    @Inject(method = { "shutdown" }, at = { @At("HEAD") })
    public void shutdown(final CallbackInfo callbackInfo) {
        SaveConfig.init();
    }
}
