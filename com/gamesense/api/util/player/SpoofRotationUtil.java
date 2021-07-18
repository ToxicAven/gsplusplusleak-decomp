// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.player;

import net.minecraft.entity.Entity;
import com.gamesense.api.util.world.EntityUtil;
import net.minecraft.entity.player.EntityPlayer;
import com.gamesense.client.GameSense;
import net.minecraft.network.Packet;
import java.util.function.Predicate;
import net.minecraft.network.play.client.CPacketPlayer;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.client.Minecraft;

public class SpoofRotationUtil
{
    private static final Minecraft mc;
    public static final SpoofRotationUtil ROTATION_UTIL;
    private int rotationConnections;
    private boolean shouldSpoofAngles;
    private boolean isSpoofingAngles;
    private double yaw;
    private double pitch;
    @EventHandler
    private final Listener<PacketEvent.Send> packetSendListener;
    
    private SpoofRotationUtil() {
        this.rotationConnections = 0;
        final Packet packet;
        this.packetSendListener = new Listener<PacketEvent.Send>(event -> {
            packet = event.getPacket();
            if (packet instanceof CPacketPlayer && this.shouldSpoofAngles && this.isSpoofingAngles) {
                ((CPacketPlayer)packet).field_149476_e = (float)this.yaw;
                ((CPacketPlayer)packet).field_149473_f = (float)this.pitch;
            }
        }, (Predicate<PacketEvent.Send>[])new Predicate[0]);
    }
    
    public void onEnable() {
        ++this.rotationConnections;
        if (this.rotationConnections == 1) {
            GameSense.EVENT_BUS.subscribe(this);
        }
    }
    
    public void onDisable() {
        --this.rotationConnections;
        if (this.rotationConnections == 0) {
            GameSense.EVENT_BUS.unsubscribe(this);
        }
    }
    
    public void lookAtPacket(final double px, final double py, final double pz, final EntityPlayer me) {
        final double[] v = EntityUtil.calculateLookAt(px, py, pz, (Entity)me);
        this.setYawAndPitch((float)v[0], (float)v[1]);
    }
    
    public void setYawAndPitch(final float yaw1, final float pitch1) {
        this.yaw = yaw1;
        this.pitch = pitch1;
        this.isSpoofingAngles = true;
    }
    
    public void resetRotation() {
        if (this.isSpoofingAngles) {
            this.yaw = SpoofRotationUtil.mc.field_71439_g.field_70177_z;
            this.pitch = SpoofRotationUtil.mc.field_71439_g.field_70125_A;
            this.isSpoofingAngles = false;
        }
    }
    
    public void shouldSpoofAngles(final boolean e) {
        this.shouldSpoofAngles = e;
    }
    
    public boolean isSpoofingAngles() {
        return this.isSpoofingAngles;
    }
    
    static {
        mc = Minecraft.func_71410_x();
        ROTATION_UTIL = new SpoofRotationUtil();
    }
}
