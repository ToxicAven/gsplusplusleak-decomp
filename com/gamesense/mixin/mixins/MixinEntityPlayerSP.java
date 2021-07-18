// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import org.spongepowered.asm.mixin.injection.Inject;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.movement.Sprint;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.gamesense.client.GameSense;
import com.gamesense.api.event.events.PlayerMoveEvent;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.entity.EntityPlayerSP;
import org.spongepowered.asm.mixin.Mixin;
import net.minecraft.client.entity.AbstractClientPlayer;

@Mixin({ EntityPlayerSP.class })
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer
{
    @Shadow
    @Final
    public NetHandlerPlayClient field_71174_a;
    @Shadow
    protected Minecraft field_71159_c;
    @Shadow
    private boolean field_184841_cd;
    @Shadow
    private float field_175164_bL;
    @Shadow
    private float field_175165_bM;
    @Shadow
    private int field_175168_bP;
    @Shadow
    private double field_175172_bI;
    @Shadow
    private double field_175166_bJ;
    @Shadow
    private double field_175167_bK;
    @Shadow
    private boolean field_189811_cr;
    @Shadow
    private boolean field_175171_bO;
    @Shadow
    private boolean field_175170_bN;
    
    public MixinEntityPlayerSP() {
        super((World)Minecraft.func_71410_x().field_71441_e, Minecraft.func_71410_x().field_71449_j.func_148256_e());
    }
    
    @Shadow
    protected abstract boolean func_175160_A();
    
    @Redirect(method = { "move" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void move(final AbstractClientPlayer player, final MoverType type, final double x, final double y, final double z) {
        final PlayerMoveEvent moveEvent = new PlayerMoveEvent(type, x, y, z);
        GameSense.EVENT_BUS.post(moveEvent);
        super.func_70091_d(type, moveEvent.getX(), moveEvent.getY(), moveEvent.getZ());
    }
    
    @ModifyArg(method = { "setSprinting" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;setSprinting(Z)V"), index = 0)
    public boolean modifySprinting(final boolean sprinting) {
        final EntityPlayerSP player = Minecraft.func_71410_x().field_71439_g;
        final Sprint sprint = ModuleManager.getModule(Sprint.class);
        return (player != null && sprint.isEnabled() && sprint.shouldSprint(player)) || sprinting;
    }
    
    @Inject(method = { "onUpdateWalkingPlayer" }, at = { @At("HEAD") }, cancellable = true)
    public void onUpdateWalkingPlayerPre(final CallbackInfo callbackInfo) {
        Vec3d position = new Vec3d(this.field_70165_t, this.func_174813_aQ().field_72338_b, this.field_70161_v);
        Vec2f rotation = new Vec2f(this.field_70177_z, this.field_70125_A);
        OnUpdateWalkingPlayerEvent event = new OnUpdateWalkingPlayerEvent(position, rotation);
        GameSense.EVENT_BUS.post(event);
        event = event.nextPhase();
        GameSense.EVENT_BUS.post(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
            final boolean moving = event.isMoving() || this.isMoving(position);
            final boolean rotating = event.isRotating() || this.isRotating(rotation);
            position = event.getPosition();
            rotation = event.getRotation();
            ++this.field_175168_bP;
            this.sendSprintPacket();
            this.sendSneakPacket();
            this.sendPlayerPacket(moving, rotating, position, rotation);
        }
        event = event.nextPhase();
        GameSense.EVENT_BUS.post(event);
    }
    
    private void sendSprintPacket() {
        final boolean sprinting = this.func_70051_ag();
        if (sprinting != this.field_175171_bO) {
            if (sprinting) {
                this.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)this, CPacketEntityAction.Action.START_SPRINTING));
            }
            else {
                this.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)this, CPacketEntityAction.Action.STOP_SPRINTING));
            }
            this.field_175171_bO = sprinting;
        }
    }
    
    private void sendSneakPacket() {
        final boolean sneaking = this.func_70093_af();
        if (sneaking != this.field_175170_bN) {
            if (sneaking) {
                this.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)this, CPacketEntityAction.Action.START_SNEAKING));
            }
            else {
                this.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)this, CPacketEntityAction.Action.STOP_SNEAKING));
            }
            this.field_175170_bN = sneaking;
        }
    }
    
    private void sendPlayerPacket(boolean moving, final boolean rotating, final Vec3d position, final Vec2f rotation) {
        if (!this.func_175160_A()) {
            return;
        }
        if (this.func_184218_aH()) {
            this.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(this.field_70159_w, -999.0, this.field_70179_y, rotation.field_189982_i, rotation.field_189983_j, this.field_70122_E));
            moving = false;
        }
        else if (moving && rotating) {
            this.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(position.field_72450_a, position.field_72448_b, position.field_72449_c, rotation.field_189982_i, rotation.field_189983_j, this.field_70122_E));
        }
        else if (moving) {
            this.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(position.field_72450_a, position.field_72448_b, position.field_72449_c, this.field_70122_E));
        }
        else if (rotating) {
            this.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(rotation.field_189982_i, rotation.field_189983_j, this.field_70122_E));
        }
        else if (this.field_184841_cd != this.field_70122_E) {
            this.field_71174_a.func_147297_a((Packet)new CPacketPlayer(this.field_70122_E));
        }
        if (moving) {
            this.field_175172_bI = position.field_72450_a;
            this.field_175166_bJ = position.field_72448_b;
            this.field_175167_bK = position.field_72449_c;
            this.field_175168_bP = 0;
        }
        if (rotating) {
            this.field_175164_bL = rotation.field_189982_i;
            this.field_175165_bM = rotation.field_189983_j;
        }
        this.field_184841_cd = this.field_70122_E;
        this.field_189811_cr = this.field_71159_c.field_71474_y.field_189989_R;
    }
    
    private boolean isMoving(final Vec3d position) {
        final double xDiff = position.field_72450_a - this.field_175172_bI;
        final double yDiff = position.field_72448_b - this.field_175166_bJ;
        final double zDiff = position.field_72449_c - this.field_175167_bK;
        return xDiff * xDiff + yDiff * yDiff + zDiff * zDiff > 9.0E-4 || this.field_175168_bP >= 20;
    }
    
    private boolean isRotating(final Vec2f rotation) {
        final double yawDiff = rotation.field_189982_i - this.field_175164_bL;
        final double pitchDiff = rotation.field_189983_j - this.field_175165_bM;
        return yawDiff != 0.0 || pitchDiff != 0.0;
    }
}
