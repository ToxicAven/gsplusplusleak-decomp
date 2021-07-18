// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.api.util.player.social.SocialManager;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.ScorePlayerTeam;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.gui.GuiPlayerTabOverlay;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ GuiPlayerTabOverlay.class })
public class MixinGuiPlayerTabOverlay
{
    @Inject(method = { "getPlayerName" }, at = { @At("HEAD") }, cancellable = true)
    public void getPlayerNameHead(final NetworkPlayerInfo networkPlayerInfoIn, final CallbackInfoReturnable<String> callbackInfoReturnable) {
        callbackInfoReturnable.setReturnValue(this.getPlayerNameGS(networkPlayerInfoIn));
    }
    
    private String getPlayerNameGS(final NetworkPlayerInfo networkPlayerInfoIn) {
        final String displayName = (networkPlayerInfoIn.func_178854_k() != null) ? networkPlayerInfoIn.func_178854_k().func_150254_d() : ScorePlayerTeam.func_96667_a((Team)networkPlayerInfoIn.func_178850_i(), networkPlayerInfoIn.func_178845_a().getName());
        if (SocialManager.isFriend(displayName)) {
            return ModuleManager.getModule(ColorMain.class).getFriendColor() + displayName;
        }
        if (SocialManager.isEnemy(displayName)) {
            return ModuleManager.getModule(ColorMain.class).getEnemyColor() + displayName;
        }
        return displayName;
    }
}
