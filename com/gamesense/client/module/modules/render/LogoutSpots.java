// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import com.gamesense.api.util.render.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import java.util.function.BiConsumer;
import com.gamesense.api.event.events.RenderEvent;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.function.Predicate;
import com.gamesense.api.util.misc.MessageBus;
import java.util.concurrent.ConcurrentHashMap;
import com.gamesense.api.util.render.GSColor;
import java.util.Arrays;
import net.minecraftforge.event.world.WorldEvent;
import com.gamesense.api.event.events.PlayerLeaveEvent;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.PlayerJoinEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.api.util.misc.Timer;
import net.minecraft.entity.player.EntityPlayer;
import java.util.Set;
import net.minecraft.entity.Entity;
import java.util.Map;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "LogoutSpots", category = Category.Render)
public class LogoutSpots extends Module
{
    IntegerSetting range;
    BooleanSetting chatMsg;
    BooleanSetting nameTag;
    IntegerSetting lineWidth;
    ModeSetting renderMode;
    ColorSetting color;
    Map<Entity, String> loggedPlayers;
    Set<EntityPlayer> worldPlayers;
    Timer timer;
    @EventHandler
    private final Listener<PlayerJoinEvent> playerJoinEventListener;
    @EventHandler
    private final Listener<PlayerLeaveEvent> playerLeaveEventListener;
    @EventHandler
    private final Listener<WorldEvent.Unload> unloadListener;
    @EventHandler
    private final Listener<WorldEvent.Load> loadListener;
    
    public LogoutSpots() {
        this.range = this.registerInteger("Range", 100, 10, 260);
        this.chatMsg = this.registerBoolean("Chat Msgs", true);
        this.nameTag = this.registerBoolean("Nametag", true);
        this.lineWidth = this.registerInteger("Width", 1, 1, 10);
        this.renderMode = this.registerMode("Render", Arrays.asList("Both", "Outline", "Fill"), "Both");
        this.color = this.registerColor("Color", new GSColor(255, 0, 0, 255));
        this.loggedPlayers = new ConcurrentHashMap<Entity, String>();
        this.worldPlayers = (Set<EntityPlayer>)ConcurrentHashMap.newKeySet();
        this.timer = new Timer();
        this.playerJoinEventListener = new Listener<PlayerJoinEvent>(event -> {
            if (LogoutSpots.mc.field_71441_e != null) {
                this.loggedPlayers.keySet().removeIf(entity -> {
                    if (entity.func_70005_c_().equalsIgnoreCase(event.getName())) {
                        if (this.chatMsg.getValue()) {
                            MessageBus.sendClientPrefixMessage(event.getName() + " reconnected!");
                        }
                        return true;
                    }
                    else {
                        return false;
                    }
                });
            }
            return;
        }, (Predicate<PlayerJoinEvent>[])new Predicate[0]);
        String date;
        String location;
        this.playerLeaveEventListener = new Listener<PlayerLeaveEvent>(event -> {
            if (LogoutSpots.mc.field_71441_e != null) {
                this.worldPlayers.removeIf(entity -> {
                    if (entity.func_70005_c_().equalsIgnoreCase(event.getName())) {
                        date = new SimpleDateFormat("k:mm").format(new Date());
                        this.loggedPlayers.put((Entity)entity, date);
                        if (this.chatMsg.getValue() && this.timer.getTimePassed() / 50L >= 5L) {
                            location = "(" + (int)entity.field_70165_t + "," + (int)entity.field_70163_u + "," + (int)entity.field_70161_v + ")";
                            MessageBus.sendClientPrefixMessage(event.getName() + " disconnected at " + location + "!");
                            this.timer.reset();
                        }
                        return true;
                    }
                    else {
                        return false;
                    }
                });
            }
            return;
        }, (Predicate<PlayerLeaveEvent>[])new Predicate[0]);
        this.unloadListener = new Listener<WorldEvent.Unload>(event -> {
            this.worldPlayers.clear();
            if (LogoutSpots.mc.field_71439_g == null || LogoutSpots.mc.field_71441_e == null) {
                this.loggedPlayers.clear();
            }
            return;
        }, (Predicate<WorldEvent.Unload>[])new Predicate[0]);
        this.loadListener = new Listener<WorldEvent.Load>(event -> {
            this.worldPlayers.clear();
            if (LogoutSpots.mc.field_71439_g == null || LogoutSpots.mc.field_71441_e == null) {
                this.loggedPlayers.clear();
            }
        }, (Predicate<WorldEvent.Load>[])new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        LogoutSpots.mc.field_71441_e.field_73010_i.stream().filter(entityPlayer -> entityPlayer != LogoutSpots.mc.field_71439_g).filter(entityPlayer -> entityPlayer.func_70032_d((Entity)LogoutSpots.mc.field_71439_g) <= this.range.getValue()).forEach(entityPlayer -> this.worldPlayers.add(entityPlayer));
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (LogoutSpots.mc.field_71439_g != null && LogoutSpots.mc.field_71441_e != null) {
            this.loggedPlayers.forEach(this::startFunction);
        }
    }
    
    public void onEnable() {
        this.loggedPlayers.clear();
        this.worldPlayers = (Set<EntityPlayer>)ConcurrentHashMap.newKeySet();
    }
    
    public void onDisable() {
        this.worldPlayers.clear();
    }
    
    private void startFunction(final Entity entity, final String string) {
        if (entity.func_70032_d((Entity)LogoutSpots.mc.field_71439_g) > this.range.getValue()) {
            return;
        }
        final int posX = (int)entity.field_70165_t;
        final int posY = (int)entity.field_70163_u;
        final int posZ = (int)entity.field_70161_v;
        final String[] nameTagMessage = { entity.func_70005_c_() + " (" + string + ")", "(" + posX + "," + posY + "," + posZ + ")" };
        GlStateManager.func_179094_E();
        if (this.nameTag.getValue()) {
            RenderUtil.drawNametag(entity, nameTagMessage, this.color.getValue(), 0);
        }
        final String s = this.renderMode.getValue();
        switch (s) {
            case "Both": {
                RenderUtil.drawBoundingBox(entity.func_184177_bl(), this.lineWidth.getValue(), this.color.getValue());
                RenderUtil.drawBox(entity.func_184177_bl(), true, -0.4, new GSColor(this.color.getValue(), 50), 63);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(entity.func_184177_bl(), this.lineWidth.getValue(), this.color.getValue());
                break;
            }
            case "Fill": {
                RenderUtil.drawBox(entity.func_184177_bl(), true, -0.4, new GSColor(this.color.getValue(), 50), 63);
                break;
            }
        }
        GlStateManager.func_179121_F();
    }
}
