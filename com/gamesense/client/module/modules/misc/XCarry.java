// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import java.util.function.Predicate;
import net.minecraft.network.play.client.CPacketCloseWindow;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "XCarry", category = Category.Misc)
public class XCarry extends Module
{
    @EventHandler
    private final Listener<PacketEvent.Send> listener;
    
    public XCarry() {
        this.listener = new Listener<PacketEvent.Send>(event -> {
            if (event.getPacket() instanceof CPacketCloseWindow && ((CPacketCloseWindow)event.getPacket()).field_149556_a == XCarry.mc.field_71439_g.field_71069_bz.field_75152_c) {
                event.cancel();
            }
        }, (Predicate<PacketEvent.Send>[])new Predicate[0]);
    }
}
