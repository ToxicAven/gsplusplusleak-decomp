// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import java.util.function.Predicate;
import com.gamesense.client.command.CommandManager;
import net.minecraft.network.play.client.CPacketChatMessage;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "ChatSuffix", category = Category.Misc)
public class ChatSuffix extends Module
{
    ModeSetting Separator;
    BooleanSetting noUnicode;
    @EventHandler
    private final Listener<PacketEvent.Send> listener;
    
    public ChatSuffix() {
        this.Separator = this.registerMode("Separator", Arrays.asList(">>", "<<", "|"), "|");
        this.noUnicode = this.registerBoolean("No Unicode", false);
        String string;
        String Separator2;
        String old;
        String suffix;
        String s;
        this.listener = new Listener<PacketEvent.Send>(event -> {
            if (event.getPacket() instanceof CPacketChatMessage) {
                if (!((CPacketChatMessage)event.getPacket()).func_149439_c().startsWith("/") && !((CPacketChatMessage)event.getPacket()).func_149439_c().startsWith(CommandManager.getCommandPrefix())) {
                    if (this.noUnicode.getValue()) {
                        string = " " + this.Separator.getValue();
                    }
                    else {
                        string = (this.Separator.getValue().equalsIgnoreCase(">>") ? " \u300b" : (this.Separator.getValue().equalsIgnoreCase("<<") ? " \u300a" : " \u23d0 "));
                    }
                    Separator2 = string;
                    old = ((CPacketChatMessage)event.getPacket()).func_149439_c();
                    suffix = Separator2 + (this.noUnicode.getValue() ? "gs++" : this.toUnicode("gs++"));
                    s = old + suffix;
                    if (s.length() <= 255) {
                        ((CPacketChatMessage)event.getPacket()).field_149440_a = s;
                    }
                }
            }
        }, (Predicate<PacketEvent.Send>[])new Predicate[0]);
    }
    
    private String toUnicode(final String s) {
        return s.toLowerCase().replace("a", "\u1d00").replace("b", "\u0299").replace("c", "\u1d04").replace("d", "\u1d05").replace("e", "\u1d07").replace("f", "\ua730").replace("g", "\u0262").replace("h", "\u029c").replace("i", "\u026a").replace("j", "\u1d0a").replace("k", "\u1d0b").replace("l", "\u029f").replace("m", "\u1d0d").replace("n", "\u0274").replace("o", "\u1d0f").replace("p", "\u1d18").replace("q", "\u01eb").replace("r", "\u0280").replace("s", "\ua731").replace("t", "\u1d1b").replace("u", "\u1d1c").replace("v", "\u1d20").replace("w", "\u1d21").replace("x", "\u02e3").replace("y", "\u028f").replace("z", "\u1d22");
    }
}
