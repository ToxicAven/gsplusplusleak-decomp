// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import com.gamesense.api.setting.values.IntegerSetting;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "DiscordRPC", category = Category.Misc, drawn = false)
public class DiscordRPCModule extends Module
{
    private static final String discordID = "";
    private static final DiscordRichPresence discordRichPresence;
    private static final DiscordRPC discordRPC;
    private int curImg;
    private final int maxImg = 4;
    IntegerSetting msChange;
    IntegerSetting hpChange;
    private long prevTimeImg;
    private long prevTimeHp;
    
    public DiscordRPCModule() {
        this.curImg = -1;
        this.msChange = this.registerInteger("Image Change", 2000, 250, 5000);
        this.hpChange = this.registerInteger("Hp Change", 100, 50, 2000);
    }
    
    public void onEnable() {
        final DiscordEventHandlers eventHandlers = new DiscordEventHandlers();
        eventHandlers.disconnected = ((var1, var2) -> System.out.println("Discord RPC disconnected, var1: " + var1 + ", var2: " + var2));
        DiscordRPCModule.discordRPC.Discord_Initialize("", eventHandlers, true, null);
        DiscordRPCModule.discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;
        DiscordRPCModule.discordRichPresence.largeImageText = "gs++";
        final long currentTimeMillis = System.currentTimeMillis();
        this.prevTimeHp = currentTimeMillis;
        this.prevTimeImg = currentTimeMillis;
        this.changeImage();
    }
    
    @Override
    public void onUpdate() {
        if (this.prevTimeImg + this.msChange.getValue() < System.currentTimeMillis()) {
            this.changeImage();
            this.prevTimeImg = System.currentTimeMillis();
        }
        if (this.prevTimeHp + this.hpChange.getValue() < System.currentTimeMillis()) {
            this.changeStatus(false);
            this.prevTimeImg = System.currentTimeMillis();
        }
    }
    
    private void changeStatus(final boolean called) {
        if (DiscordRPCModule.mc.field_71439_g == null) {
            DiscordRPCModule.discordRichPresence.state = "On the main menu";
            return;
        }
        DiscordRPCModule.discordRichPresence.state = "Cracked Version ";
        DiscordRPCModule.discordRichPresence.details = "p2.3.1.c4 | " + DiscordRPCModule.mc.field_71439_g.func_70005_c_();
        DiscordRPCModule.discordRichPresence.largeImageKey = "gs" + this.curImg;
        if (!called) {
            DiscordRPCModule.discordRPC.Discord_UpdatePresence(DiscordRPCModule.discordRichPresence);
        }
    }
    
    private void changeImage() {
        this.changeStatus(true);
        DiscordRPCModule.discordRichPresence.largeImageKey = "gs";
        DiscordRPCModule.discordRPC.Discord_UpdatePresence(DiscordRPCModule.discordRichPresence);
    }
    
    public void onDisable() {
        DiscordRPCModule.discordRPC.Discord_Shutdown();
        DiscordRPCModule.discordRPC.Discord_ClearPresence();
    }
    
    static {
        discordRichPresence = new DiscordRichPresence();
        discordRPC = DiscordRPC.INSTANCE;
    }
}
