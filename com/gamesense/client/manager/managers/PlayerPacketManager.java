// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.manager.managers;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import java.util.function.Predicate;
import com.gamesense.api.util.misc.CollectionUtil;
import com.gamesense.api.event.Phase;
import java.util.ArrayList;
import com.gamesense.api.event.events.RenderEntityEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import com.gamesense.api.event.events.PacketEvent;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import com.gamesense.api.util.player.PlayerPacket;
import java.util.List;
import com.gamesense.client.manager.Manager;

public enum PlayerPacketManager implements Manager
{
    INSTANCE;
    
    private final List<PlayerPacket> packets;
    private Vec3d prevServerSidePosition;
    private Vec3d serverSidePosition;
    private Vec2f prevServerSideRotation;
    private Vec2f serverSideRotation;
    private Vec2f clientSidePitch;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
    @EventHandler
    private final Listener<PacketEvent.PostSend> postSendListener;
    @EventHandler
    private final Listener<TickEvent.ClientTickEvent> tickEventListener;
    @EventHandler
    private final Listener<RenderEntityEvent.Head> renderEntityEventHeadListener;
    @EventHandler
    private final Listener<RenderEntityEvent.Return> renderEntityEventReturnListener;
    
    private PlayerPacketManager() {
        this.packets = new ArrayList<PlayerPacket>();
        this.prevServerSidePosition = Vec3d.field_186680_a;
        this.serverSidePosition = Vec3d.field_186680_a;
        this.prevServerSideRotation = Vec2f.field_189974_a;
        this.serverSideRotation = Vec2f.field_189974_a;
        this.clientSidePitch = Vec2f.field_189974_a;
        PlayerPacket packet;
        this.onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
            if (event.getPhase() != Phase.BY || this.packets.isEmpty()) {
                return;
            }
            else {
                packet = CollectionUtil.maxOrNull(this.packets, PlayerPacket::getPriority);
                if (packet != null) {
                    event.cancel();
                    event.apply(packet);
                }
                this.packets.clear();
                return;
            }
        }, (Predicate<OnUpdateWalkingPlayerEvent>[])new Predicate[0]);
        Packet rawPacket;
        EntityPlayerSP player;
        CPacketPlayer packet2;
        this.postSendListener = new Listener<PacketEvent.PostSend>(event -> {
            if (event.isCancelled()) {
                return;
            }
            else {
                rawPacket = event.getPacket();
                player = this.getPlayer();
                if (player != null && rawPacket instanceof CPacketPlayer) {
                    packet2 = (CPacketPlayer)rawPacket;
                    if (packet2.field_149480_h) {
                        this.serverSidePosition = new Vec3d(packet2.field_149479_a, packet2.field_149477_b, packet2.field_149478_c);
                    }
                    if (packet2.field_149481_i) {
                        this.serverSideRotation = new Vec2f(packet2.field_149476_e, packet2.field_149473_f);
                        player.field_70759_as = packet2.field_149476_e;
                    }
                }
                return;
            }
        }, (byte)5, (Predicate<PacketEvent.PostSend>[])new Predicate[0]);
        this.tickEventListener = new Listener<TickEvent.ClientTickEvent>(event -> {
            if (event.phase != TickEvent.Phase.START) {
                return;
            }
            else {
                this.prevServerSidePosition = this.serverSidePosition;
                this.prevServerSideRotation = this.serverSideRotation;
                return;
            }
        }, (Predicate<TickEvent.ClientTickEvent>[])new Predicate[0]);
        final EntityPlayerSP player2;
        this.renderEntityEventHeadListener = new Listener<RenderEntityEvent.Head>(event -> {
            player2 = this.getPlayer();
            if (player2 == null || player2.func_184218_aH() || event.getType() != RenderEntityEvent.Type.TEXTURE || event.getEntity() != player2) {
                return;
            }
            else {
                this.clientSidePitch = new Vec2f(player2.field_70127_C, player2.field_70125_A);
                player2.field_70127_C = this.prevServerSideRotation.field_189983_j;
                player2.field_70125_A = this.serverSideRotation.field_189983_j;
                return;
            }
        }, (Predicate<RenderEntityEvent.Head>[])new Predicate[0]);
        final EntityPlayerSP player3;
        this.renderEntityEventReturnListener = new Listener<RenderEntityEvent.Return>(event -> {
            player3 = this.getPlayer();
            if (player3 != null && !player3.func_184218_aH() && event.getType() == RenderEntityEvent.Type.TEXTURE && event.getEntity() == player3) {
                player3.field_70127_C = this.clientSidePitch.field_189982_i;
                player3.field_70125_A = this.clientSidePitch.field_189983_j;
            }
        }, (Predicate<RenderEntityEvent.Return>[])new Predicate[0]);
    }
    
    public void addPacket(final PlayerPacket packet) {
        this.packets.add(packet);
    }
    
    public Vec3d getPrevServerSidePosition() {
        return this.prevServerSidePosition;
    }
    
    public Vec3d getServerSidePosition() {
        return this.serverSidePosition;
    }
    
    public Vec2f getPrevServerSideRotation() {
        return this.prevServerSideRotation;
    }
    
    public Vec2f getServerSideRotation() {
        return this.serverSideRotation;
    }
}
