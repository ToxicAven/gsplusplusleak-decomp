// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import java.util.function.Predicate;
import com.gamesense.api.event.events.PacketEvent;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.PlayerMoveEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.Entity;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Freecam", category = Category.Render)
public class Freecam extends Module
{
    BooleanSetting cancelPackets;
    DoubleSetting speed;
    private double posX;
    private double posY;
    private double posZ;
    private float pitch;
    private float yaw;
    private EntityOtherPlayerMP clonedPlayer;
    private boolean isRidingEntity;
    private Entity ridingEntity;
    @EventHandler
    private final Listener<PlayerMoveEvent> moveListener;
    @EventHandler
    private final Listener<PlayerSPPushOutOfBlocksEvent> pushListener;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    
    public Freecam() {
        this.cancelPackets = this.registerBoolean("Cancel Packets", true);
        this.speed = this.registerDouble("Speed", 10.0, 0.0, 20.0);
        this.moveListener = new Listener<PlayerMoveEvent>(event -> Freecam.mc.field_71439_g.field_70145_X = true, (Predicate<PlayerMoveEvent>[])new Predicate[0]);
        this.pushListener = new Listener<PlayerSPPushOutOfBlocksEvent>(event -> event.setCanceled(true), (Predicate<PlayerSPPushOutOfBlocksEvent>[])new Predicate[0]);
        this.sendListener = new Listener<PacketEvent.Send>(event -> {
            if ((event.getPacket() instanceof CPacketPlayer || event.getPacket() instanceof CPacketInput) && this.cancelPackets.getValue()) {
                event.cancel();
            }
        }, (Predicate<PacketEvent.Send>[])new Predicate[0]);
    }
    
    public void onEnable() {
        if (Freecam.mc.field_71439_g != null) {
            this.isRidingEntity = (Freecam.mc.field_71439_g.func_184187_bx() != null);
            if (Freecam.mc.field_71439_g.func_184187_bx() == null) {
                this.posX = Freecam.mc.field_71439_g.field_70165_t;
                this.posY = Freecam.mc.field_71439_g.field_70163_u;
                this.posZ = Freecam.mc.field_71439_g.field_70161_v;
            }
            else {
                this.ridingEntity = Freecam.mc.field_71439_g.func_184187_bx();
                Freecam.mc.field_71439_g.func_184210_p();
            }
            this.pitch = Freecam.mc.field_71439_g.field_70125_A;
            this.yaw = Freecam.mc.field_71439_g.field_70177_z;
            (this.clonedPlayer = new EntityOtherPlayerMP((World)Freecam.mc.field_71441_e, Freecam.mc.func_110432_I().func_148256_e())).func_82149_j((Entity)Freecam.mc.field_71439_g);
            this.clonedPlayer.field_70759_as = Freecam.mc.field_71439_g.field_70759_as;
            Freecam.mc.field_71441_e.func_73027_a(-100, (Entity)this.clonedPlayer);
            Freecam.mc.field_71439_g.field_71075_bZ.field_75100_b = true;
            Freecam.mc.field_71439_g.field_71075_bZ.func_75092_a((float)(this.speed.getValue() / 100.0));
            Freecam.mc.field_71439_g.field_70145_X = true;
        }
    }
    
    public void onDisable() {
        final EntityPlayer localPlayer = (EntityPlayer)Freecam.mc.field_71439_g;
        if (localPlayer != null) {
            Freecam.mc.field_71439_g.func_70080_a(this.posX, this.posY, this.posZ, this.yaw, this.pitch);
            Freecam.mc.field_71441_e.func_73028_b(-100);
            this.clonedPlayer = null;
            final double posX = 0.0;
            this.posZ = posX;
            this.posY = posX;
            this.posX = posX;
            final float n = 0.0f;
            this.yaw = n;
            this.pitch = n;
            Freecam.mc.field_71439_g.field_71075_bZ.field_75100_b = false;
            Freecam.mc.field_71439_g.field_71075_bZ.func_75092_a(0.05f);
            Freecam.mc.field_71439_g.field_70145_X = false;
            final EntityPlayerSP field_71439_g = Freecam.mc.field_71439_g;
            final EntityPlayerSP field_71439_g2 = Freecam.mc.field_71439_g;
            final EntityPlayerSP field_71439_g3 = Freecam.mc.field_71439_g;
            final double field_70159_w = 0.0;
            field_71439_g3.field_70179_y = field_70159_w;
            field_71439_g2.field_70181_x = field_70159_w;
            field_71439_g.field_70159_w = field_70159_w;
            if (this.isRidingEntity) {
                Freecam.mc.field_71439_g.func_184205_a(this.ridingEntity, true);
            }
        }
    }
    
    @Override
    public void onUpdate() {
        Freecam.mc.field_71439_g.field_71075_bZ.field_75100_b = true;
        Freecam.mc.field_71439_g.field_71075_bZ.func_75092_a((float)(this.speed.getValue() / 100.0));
        Freecam.mc.field_71439_g.field_70145_X = true;
        Freecam.mc.field_71439_g.field_70122_E = false;
        Freecam.mc.field_71439_g.field_70143_R = 0.0f;
    }
}
