// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import net.minecraft.network.play.client.CPacketChatMessage;
import java.util.function.Predicate;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.client.gui.GuiGameOver;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.client.event.GuiOpenEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "AutoRespawn", category = Category.Misc)
public class AutoRespawn extends Module
{
    BooleanSetting respawnMessage;
    IntegerSetting respawnMessageDelay;
    private static String AutoRespawnMessage;
    private boolean isDead;
    private boolean sentRespawnMessage;
    long timeSinceRespawn;
    @EventHandler
    private final Listener<GuiOpenEvent> livingDeathEventListener;
    
    public AutoRespawn() {
        this.respawnMessage = this.registerBoolean("Respawn Message", false);
        this.respawnMessageDelay = this.registerInteger("Msg Delay(ms)", 0, 0, 5000);
        this.sentRespawnMessage = true;
        this.livingDeathEventListener = new Listener<GuiOpenEvent>(event -> {
            if (event.getGui() instanceof GuiGameOver) {
                event.setCanceled(true);
                this.isDead = true;
                this.sentRespawnMessage = true;
                AutoRespawn.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketClientStatus(CPacketClientStatus.State.PERFORM_RESPAWN));
            }
        }, (Predicate<GuiOpenEvent>[])new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        if (AutoRespawn.mc.field_71439_g == null) {
            return;
        }
        if (this.isDead && AutoRespawn.mc.field_71439_g.func_70089_S()) {
            if (this.respawnMessage.getValue()) {
                this.sentRespawnMessage = false;
                this.timeSinceRespawn = System.currentTimeMillis();
            }
            this.isDead = false;
        }
        if (!this.sentRespawnMessage && System.currentTimeMillis() - this.timeSinceRespawn > this.respawnMessageDelay.getValue()) {
            AutoRespawn.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketChatMessage(AutoRespawn.AutoRespawnMessage));
            this.sentRespawnMessage = true;
        }
    }
    
    public static void setAutoRespawnMessage(final String string) {
        AutoRespawn.AutoRespawnMessage = string;
    }
    
    public static String getAutoRespawnMessages() {
        return AutoRespawn.AutoRespawnMessage;
    }
    
    static {
        AutoRespawn.AutoRespawnMessage = "/kit";
    }
}
