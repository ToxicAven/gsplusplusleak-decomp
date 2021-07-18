// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import java.util.ArrayList;
import java.util.Objects;
import com.gamesense.api.util.misc.MessageBus;
import java.util.Iterator;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.Entity;
import java.util.function.Predicate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "AutoGG", category = Category.Misc)
public class AutoGG extends Module
{
    public static AutoGG INSTANCE;
    static List<String> AutoGgMessages;
    private ConcurrentHashMap targetedPlayers;
    int index;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    @EventHandler
    private final Listener<LivingDeathEvent> livingDeathEventListener;
    
    public AutoGG() {
        this.targetedPlayers = null;
        this.index = -1;
        CPacketUseEntity cPacketUseEntity;
        Entity targetEntity;
        this.sendListener = new Listener<PacketEvent.Send>(event -> {
            if (AutoGG.mc.field_71439_g != null) {
                if (this.targetedPlayers == null) {
                    this.targetedPlayers = new ConcurrentHashMap();
                }
                if (event.getPacket() instanceof CPacketUseEntity) {
                    cPacketUseEntity = (CPacketUseEntity)event.getPacket();
                    if (cPacketUseEntity.func_149565_c().equals((Object)CPacketUseEntity.Action.ATTACK)) {
                        targetEntity = cPacketUseEntity.func_149564_a((World)AutoGG.mc.field_71441_e);
                        if (targetEntity instanceof EntityPlayer) {
                            this.addTargetedPlayer(targetEntity.func_70005_c_());
                        }
                    }
                }
            }
            return;
        }, (Predicate<PacketEvent.Send>[])new Predicate[0]);
        EntityLivingBase entity;
        EntityPlayer player;
        String name;
        this.livingDeathEventListener = new Listener<LivingDeathEvent>(event -> {
            if (AutoGG.mc.field_71439_g != null) {
                if (this.targetedPlayers == null) {
                    this.targetedPlayers = new ConcurrentHashMap();
                }
                entity = event.getEntityLiving();
                if (entity != null && entity instanceof EntityPlayer) {
                    player = (EntityPlayer)entity;
                    if (player.func_110143_aJ() <= 0.0f) {
                        name = player.func_70005_c_();
                        if (this.shouldAnnounce(name)) {
                            this.doAnnounce(name);
                        }
                    }
                }
            }
            return;
        }, (Predicate<LivingDeathEvent>[])new Predicate[0]);
        AutoGG.INSTANCE = this;
    }
    
    public void onEnable() {
        this.targetedPlayers = new ConcurrentHashMap();
    }
    
    public void onDisable() {
        this.targetedPlayers = null;
    }
    
    @Override
    public void onUpdate() {
        if (this.targetedPlayers == null) {
            this.targetedPlayers = new ConcurrentHashMap();
        }
        for (final Entity entity : AutoGG.mc.field_71441_e.func_72910_y()) {
            if (entity instanceof EntityPlayer) {
                final EntityPlayer player = (EntityPlayer)entity;
                if (player.func_110143_aJ() > 0.0f) {
                    continue;
                }
                final String name = player.func_70005_c_();
                if (this.shouldAnnounce(name)) {
                    this.doAnnounce(name);
                    break;
                }
                continue;
            }
        }
        this.targetedPlayers.forEach((namex, timeout) -> {
            if (timeout <= 0) {
                this.targetedPlayers.remove(namex);
            }
            else {
                this.targetedPlayers.put(namex, timeout - 1);
            }
        });
    }
    
    private boolean shouldAnnounce(final String name) {
        return this.targetedPlayers.containsKey(name);
    }
    
    private void doAnnounce(final String name) {
        this.targetedPlayers.remove(name);
        if (this.index >= AutoGG.AutoGgMessages.size() - 1) {
            this.index = -1;
        }
        ++this.index;
        String message;
        if (AutoGG.AutoGgMessages.size() > 0) {
            message = AutoGG.AutoGgMessages.get(this.index);
        }
        else {
            message = "GG! GameSense p2.3.1.c4 is on top!";
        }
        String messageSanitized = message.replaceAll("\u0e22\u0e07", "").replace("{name}", name);
        if (messageSanitized.length() > 255) {
            messageSanitized = messageSanitized.substring(0, 255);
        }
        MessageBus.sendServerMessage(messageSanitized);
    }
    
    public void addTargetedPlayer(final String name) {
        if (!Objects.equals(name, AutoGG.mc.field_71439_g.func_70005_c_())) {
            if (this.targetedPlayers == null) {
                this.targetedPlayers = new ConcurrentHashMap();
            }
            this.targetedPlayers.put(name, 20);
        }
    }
    
    public static void addAutoGgMessage(final String s) {
        AutoGG.AutoGgMessages.add(s);
    }
    
    public static List<String> getAutoGgMessages() {
        return AutoGG.AutoGgMessages;
    }
    
    static {
        AutoGG.AutoGgMessages = new ArrayList<String>();
    }
}
