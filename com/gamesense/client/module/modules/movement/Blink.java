// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Iterator;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.client.entity.EntityPlayerSP;
import java.util.function.Predicate;
import net.minecraft.network.play.client.CPacketPlayer;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.network.Packet;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Blink", category = Category.Movement)
public class Blink extends Module
{
    BooleanSetting ghostPlayer;
    private EntityOtherPlayerMP entity;
    private final ConcurrentLinkedQueue<Packet<?>> packets;
    @EventHandler
    private final Listener<PacketEvent.Send> packetSendListener;
    
    public Blink() {
        this.ghostPlayer = this.registerBoolean("Ghost Player", true);
        this.packets = new ConcurrentLinkedQueue<Packet<?>>();
        final Packet packet;
        final EntityPlayerSP player;
        this.packetSendListener = new Listener<PacketEvent.Send>(event -> {
            packet = event.getPacket();
            player = Blink.mc.field_71439_g;
            if (player != null && player.func_70089_S() && packet instanceof CPacketPlayer) {
                this.packets.add((Packet<?>)packet);
                event.cancel();
            }
        }, (Predicate<PacketEvent.Send>[])new Predicate[0]);
    }
    
    public void onEnable() {
        final EntityPlayerSP player = Blink.mc.field_71439_g;
        final WorldClient world = Blink.mc.field_71441_e;
        if (player == null || world == null) {
            this.disable();
        }
        else if (this.ghostPlayer.getValue()) {
            (this.entity = new EntityOtherPlayerMP((World)world, Blink.mc.func_110432_I().func_148256_e())).func_82149_j((Entity)player);
            this.entity.field_71071_by.func_70455_b(player.field_71071_by);
            this.entity.field_70177_z = player.field_70177_z;
            this.entity.field_70759_as = player.field_70759_as;
            world.func_73027_a(667, (Entity)this.entity);
        }
    }
    
    @Override
    public void onUpdate() {
        final Entity entity = (Entity)this.entity;
        final WorldClient world = Blink.mc.field_71441_e;
        if (!this.ghostPlayer.getValue() && entity != null && world != null) {
            world.func_72900_e(entity);
        }
    }
    
    public void onDisable() {
        final Entity entity = (Entity)this.entity;
        final WorldClient world = Blink.mc.field_71441_e;
        if (entity != null && world != null) {
            world.func_72900_e(entity);
        }
        final EntityPlayerSP player = Blink.mc.field_71439_g;
        if (this.packets.size() > 0 && player != null) {
            for (final Packet<?> packet : this.packets) {
                player.field_71174_a.func_147297_a((Packet)packet);
            }
            this.packets.clear();
        }
    }
    
    @Override
    public String getHudInfo() {
        return "[" + ChatFormatting.WHITE + this.packets.size() + ChatFormatting.GRAY + "]";
    }
}
