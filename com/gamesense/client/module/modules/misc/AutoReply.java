// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import java.util.function.Predicate;
import com.gamesense.api.util.misc.MessageBus;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "AutoReply", category = Category.Misc)
public class AutoReply extends Module
{
    private static String reply;
    @EventHandler
    private final Listener<ClientChatReceivedEvent> listener;
    
    public AutoReply() {
        this.listener = new Listener<ClientChatReceivedEvent>(event -> {
            if (event.getMessage().func_150260_c().contains("whispers: ") && !event.getMessage().func_150260_c().startsWith(AutoReply.mc.field_71439_g.func_70005_c_())) {
                if (!event.getMessage().func_150260_c().contains("I don't speak to newfags!")) {
                    MessageBus.sendServerMessage("/r " + AutoReply.reply);
                }
            }
        }, (Predicate<ClientChatReceivedEvent>[])new Predicate[0]);
    }
    
    public static String getReply() {
        return AutoReply.reply;
    }
    
    public static void setReply(final String r) {
        AutoReply.reply = r;
    }
    
    static {
        AutoReply.reply = "I don't speak to newfags!";
    }
}
