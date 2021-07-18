// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemFood;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.exploits.PacketUse;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.entity.player.EntityPlayer;
import com.gamesense.api.event.events.ReachDistanceEvent;
import com.gamesense.api.event.events.DamageBlockEvent;
import net.minecraft.util.EnumFacing;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.gamesense.api.event.events.DestroyBlockEvent;
import com.gamesense.client.GameSense;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ PlayerControllerMP.class })
public abstract class MixinPlayerControllerMP
{
    @Shadow
    public abstract void func_78750_j();
    
    @Inject(method = { "onPlayerDestroyBlock" }, at = { @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playEvent(ILnet/minecraft/util/math/BlockPos;I)V") }, cancellable = true)
    private void onPlayerDestroyBlock(final BlockPos pos, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        GameSense.EVENT_BUS.post(new DestroyBlockEvent(pos));
    }
    
    @Inject(method = { "onPlayerDamageBlock(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;)Z" }, at = { @At("HEAD") }, cancellable = true)
    private void onPlayerDamageBlock(final BlockPos posBlock, final EnumFacing directionFacing, final CallbackInfoReturnable<Boolean> callbackInfoReturnable) {
        final DamageBlockEvent event = new DamageBlockEvent(posBlock, directionFacing);
        GameSense.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            callbackInfoReturnable.setReturnValue(false);
        }
    }
    
    @Inject(method = { "getBlockReachDistance" }, at = { @At("RETURN") }, cancellable = true)
    private void getReachDistanceHook(final CallbackInfoReturnable<Float> distance) {
        final ReachDistanceEvent reachDistanceEvent = new ReachDistanceEvent(distance.getReturnValue());
        GameSense.EVENT_BUS.post(reachDistanceEvent);
        distance.setReturnValue(reachDistanceEvent.getDistance());
    }
    
    @Inject(method = { "onStoppedUsingItem" }, at = { @At("HEAD") }, cancellable = true)
    public void onStoppedUsingItem(final EntityPlayer playerIn, final CallbackInfo ci) {
        final PacketUse packetUse = ModuleManager.getModule(PacketUse.class);
        if (packetUse.isEnabled() && ((packetUse.food.getValue() && playerIn.func_184586_b(playerIn.func_184600_cs()).func_77973_b() instanceof ItemFood) || (packetUse.potion.getValue() && playerIn.func_184586_b(playerIn.func_184600_cs()).func_77973_b() instanceof ItemPotion) || packetUse.all.getValue())) {
            this.func_78750_j();
            playerIn.func_184597_cx();
            ci.cancel();
        }
    }
}
