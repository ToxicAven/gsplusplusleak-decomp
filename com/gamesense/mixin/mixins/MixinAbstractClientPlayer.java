// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import java.util.UUID;
import com.gamesense.api.util.render.CapeUtil;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.CapesModule;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.entity.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ AbstractClientPlayer.class })
public abstract class MixinAbstractClientPlayer
{
    private String me;
    
    public MixinAbstractClientPlayer() {
        this.me = null;
    }
    
    @Shadow
    @Nullable
    protected abstract NetworkPlayerInfo func_175155_b();
    
    @Inject(method = { "getLocationCape" }, at = { @At("HEAD") }, cancellable = true)
    public void getLocationCape(final CallbackInfoReturnable<ResourceLocation> callbackInfoReturnable) {
        final UUID uuid = this.func_175155_b().func_178845_a().getId();
        final CapesModule capesModule = ModuleManager.getModule(CapesModule.class);
        if (CapeUtil.hasCape(uuid)) {
            if (this.me == null) {
                this.me = CapesModule.getUsName();
            }
            if (this.func_175155_b().func_178845_a().getName().equals(this.me) && !capesModule.isOn()) {
                return;
            }
            if (capesModule.capeMode.getValue().equalsIgnoreCase("Old")) {
                callbackInfoReturnable.setReturnValue(new ResourceLocation("gamesense:capeblack.png"));
            }
            else {
                callbackInfoReturnable.setReturnValue(new ResourceLocation("gamesense:capewhite.png"));
            }
        }
    }
}
