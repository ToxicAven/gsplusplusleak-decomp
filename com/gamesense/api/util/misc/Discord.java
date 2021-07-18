// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;

public class Discord
{
    private static final String discordID = "840996509880680479";
    public static final DiscordRichPresence discordRichPresence;
    private static final DiscordRPC discordRPC;
    private static final String clientVersion = "p2.3.1.c4";
    
    public static void startRPC() {
        final DiscordEventHandlers eventHandlers = new DiscordEventHandlers();
        eventHandlers.disconnected = ((var1, var2) -> System.out.println("Discord RPC disconnected, var1: " + var1 + ", var2: " + var2));
        Discord.discordRPC.Discord_Initialize("840996509880680479", eventHandlers, true, null);
        Discord.discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;
        Discord.discordRichPresence.details = "p2.3.1.c4";
        Discord.discordRichPresence.largeImageKey = "gs";
        Discord.discordRichPresence.largeImageText = "gs++";
        Discord.discordRichPresence.state = null;
        Discord.discordRPC.Discord_UpdatePresence(Discord.discordRichPresence);
    }
    
    public static void stopRPC() {
        Discord.discordRPC.Discord_Shutdown();
        Discord.discordRPC.Discord_ClearPresence();
    }
    
    static {
        discordRichPresence = new DiscordRichPresence();
        discordRPC = DiscordRPC.INSTANCE;
    }
}
