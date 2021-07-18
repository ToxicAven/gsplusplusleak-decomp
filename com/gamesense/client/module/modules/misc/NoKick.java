// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntitySlime;
import java.util.function.Predicate;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "NoKick", category = Category.Misc)
public class NoKick extends Module
{
    public BooleanSetting noPacketKick;
    BooleanSetting noSlimeCrash;
    BooleanSetting noOffhandCrash;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    
    public NoKick() {
        this.noPacketKick = this.registerBoolean("Packet", true);
        this.noSlimeCrash = this.registerBoolean("Slime", false);
        this.noOffhandCrash = this.registerBoolean("Offhand", false);
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (this.noOffhandCrash.getValue() && event.getPacket() instanceof SPacketSoundEffect && ((SPacketSoundEffect)event.getPacket()).func_186978_a() == SoundEvents.field_187719_p) {
                event.cancel();
            }
        }, (Predicate<PacketEvent.Receive>[])new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        if (NoKick.mc.field_71441_e != null && this.noSlimeCrash.getValue()) {
            EntitySlime slime;
            NoKick.mc.field_71441_e.field_72996_f.forEach(entity -> {
                if (entity instanceof EntitySlime) {
                    slime = entity;
                    if (slime.func_70809_q() > 4) {
                        NoKick.mc.field_71441_e.func_72900_e((Entity)entity);
                    }
                }
            });
        }
    }
}
