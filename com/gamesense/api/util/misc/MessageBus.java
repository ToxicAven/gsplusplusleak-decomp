// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.misc;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketChatMessage;
import com.gamesense.client.module.Module;
import net.minecraft.util.text.ITextComponent;
import com.gamesense.client.module.modules.hud.Notifications;
import net.minecraft.util.text.TextComponentString;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.ChatModifier;
import net.minecraft.client.Minecraft;
import com.mojang.realmsclient.gui.ChatFormatting;

public class MessageBus
{
    public static String watermark;
    public static ChatFormatting messageFormatting;
    protected static final Minecraft mc;
    
    public static void sendClientPrefixMessage(final String message) {
        final TextComponentString string1 = new TextComponentString((ModuleManager.getModule(ChatModifier.class).watermarkSpecial.getValue() ? "\u2063[gs++]" : MessageBus.watermark) + MessageBus.messageFormatting + message);
        final TextComponentString string2 = new TextComponentString(MessageBus.messageFormatting + message);
        final Notifications notifications = ModuleManager.getModule(Notifications.class);
        notifications.addMessage(string2);
        if (notifications.isEnabled() && notifications.disableChat.getValue()) {
            return;
        }
        MessageBus.mc.field_71439_g.func_145747_a((ITextComponent)string1);
    }
    
    public static void sendCommandMessage(final String message, final boolean prefix) {
        final String watermark1 = prefix ? MessageBus.watermark : "";
        final TextComponentString string = new TextComponentString(watermark1 + MessageBus.messageFormatting + message);
        MessageBus.mc.field_71439_g.func_145747_a((ITextComponent)string);
    }
    
    public static void sendClientRawMessage(final String message) {
        final TextComponentString string = new TextComponentString(MessageBus.messageFormatting + message);
        final Notifications notifications = ModuleManager.getModule(Notifications.class);
        notifications.addMessage(string);
        if (ModuleManager.isModuleEnabled(Notifications.class) && notifications.disableChat.getValue()) {
            return;
        }
        MessageBus.mc.field_71439_g.func_145747_a((ITextComponent)string);
    }
    
    public static void sendServerMessage(final String message) {
        MessageBus.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage(message));
    }
    
    static {
        MessageBus.watermark = ChatFormatting.GRAY + "[" + ChatFormatting.WHITE + "g" + ChatFormatting.GREEN + "s++" + ChatFormatting.GRAY + "] " + ChatFormatting.RESET;
        MessageBus.messageFormatting = ChatFormatting.GRAY;
        mc = Minecraft.func_71410_x();
    }
}
