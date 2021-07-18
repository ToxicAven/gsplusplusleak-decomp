// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.manager.managers;

import net.minecraft.entity.Entity;
import java.util.Iterator;
import com.gamesense.client.GameSense;
import net.minecraft.world.World;
import net.minecraft.network.play.server.SPacketEntityStatus;
import java.util.function.Predicate;
import com.gamesense.api.util.misc.MessageBus;
import net.minecraft.entity.player.EntityPlayer;
import com.gamesense.api.event.events.TotemPopEvent;
import com.gamesense.api.event.events.PacketEvent;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import me.zero.alpine.listener.Listener;
import java.util.HashMap;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.gamesense.client.manager.Manager;

public enum TotemPopManager implements Manager
{
    INSTANCE;
    
    public boolean sendMsgs;
    public ChatFormatting chatFormatting;
    private final HashMap<String, Integer> playerPopCount;
    @EventHandler
    private final Listener<TickEvent.ClientTickEvent> clientTickEventListener;
    @EventHandler
    private final Listener<PacketEvent.Receive> packetEventListener;
    @EventHandler
    private final Listener<TotemPopEvent> totemPopEventListener;
    
    private TotemPopManager() {
        this.sendMsgs = false;
        this.chatFormatting = ChatFormatting.WHITE;
        this.playerPopCount = new HashMap<String, Integer>();
        final Iterator<EntityPlayer> iterator;
        EntityPlayer entityPlayer;
        this.clientTickEventListener = new Listener<TickEvent.ClientTickEvent>(event -> {
            if (event.phase != TickEvent.Phase.START) {
                return;
            }
            else if (this.getPlayer() == null || this.getWorld() == null) {
                this.playerPopCount.clear();
                return;
            }
            else {
                this.getWorld().field_73010_i.iterator();
                while (iterator.hasNext()) {
                    entityPlayer = iterator.next();
                    if (entityPlayer.func_110143_aJ() <= 0.0f && this.playerPopCount.containsKey(entityPlayer.func_70005_c_())) {
                        if (this.sendMsgs) {
                            MessageBus.sendClientPrefixMessage(this.chatFormatting + entityPlayer.func_70005_c_() + " died after popping " + ChatFormatting.GREEN + this.getPlayerPopCount(entityPlayer.func_70005_c_()) + this.chatFormatting + " totems!");
                        }
                        this.playerPopCount.remove(entityPlayer.func_70005_c_());
                    }
                }
                return;
            }
        }, (Predicate<TickEvent.ClientTickEvent>[])new Predicate[0]);
        SPacketEntityStatus packet;
        Entity entity;
        this.packetEventListener = new Listener<PacketEvent.Receive>(event -> {
            if (this.getPlayer() == null || this.getWorld() == null) {
                return;
            }
            else {
                if (event.getPacket() instanceof SPacketEntityStatus) {
                    packet = (SPacketEntityStatus)event.getPacket();
                    entity = packet.func_149161_a((World)this.getWorld());
                    if (packet.func_149160_c() == 35) {
                        GameSense.EVENT_BUS.post(new TotemPopEvent(entity));
                    }
                }
                return;
            }
        }, (Predicate<PacketEvent.Receive>[])new Predicate[0]);
        String entityName;
        int popCounter;
        this.totemPopEventListener = new Listener<TotemPopEvent>(event -> {
            if (this.getPlayer() != null && this.getWorld() != null) {
                if (event.getEntity() != null) {
                    entityName = event.getEntity().func_70005_c_();
                    if (this.playerPopCount.get(entityName) == null) {
                        this.playerPopCount.put(entityName, 1);
                        if (this.sendMsgs) {
                            MessageBus.sendClientPrefixMessage(this.chatFormatting + entityName + " popped " + ChatFormatting.RED + 1 + this.chatFormatting + " totem!");
                        }
                    }
                    else {
                        popCounter = this.playerPopCount.get(entityName) + 1;
                        this.playerPopCount.put(entityName, popCounter);
                        if (this.sendMsgs) {
                            MessageBus.sendClientPrefixMessage(this.chatFormatting + entityName + " popped " + ChatFormatting.RED + popCounter + this.chatFormatting + " totems!");
                        }
                    }
                }
            }
        }, (Predicate<TotemPopEvent>[])new Predicate[0]);
    }
    
    public int getPlayerPopCount(final String name) {
        if (this.playerPopCount.containsKey(name)) {
            return this.playerPopCount.get(name);
        }
        return 0;
    }
}
