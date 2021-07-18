// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import org.lwjgl.input.Keyboard;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.util.MovementInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import java.util.function.Predicate;
import com.gamesense.api.event.events.WaterPushEvent;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.EntityCollisionEvent;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.client.event.InputUpdateEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "PlayerTweaks", category = Category.Movement)
public class PlayerTweaks extends Module
{
    public BooleanSetting guiMove;
    BooleanSetting noPush;
    BooleanSetting noFall;
    public BooleanSetting noSlow;
    BooleanSetting antiKnockBack;
    @EventHandler
    private final Listener<InputUpdateEvent> eventListener;
    @EventHandler
    private final Listener<EntityCollisionEvent> entityCollisionEventListener;
    @EventHandler
    private final Listener<PacketEvent.Receive> receiveListener;
    @EventHandler
    private final Listener<PacketEvent.Send> sendListener;
    @EventHandler
    private final Listener<WaterPushEvent> waterPushEventListener;
    
    public PlayerTweaks() {
        this.guiMove = this.registerBoolean("Gui Move", false);
        this.noPush = this.registerBoolean("No Push", false);
        this.noFall = this.registerBoolean("No Fall", false);
        this.noSlow = this.registerBoolean("No Slow", false);
        this.antiKnockBack = this.registerBoolean("Velocity", false);
        final MovementInput movementInput;
        final MovementInput movementInput2;
        this.eventListener = new Listener<InputUpdateEvent>(event -> {
            if (this.noSlow.getValue() && PlayerTweaks.mc.field_71439_g.func_184587_cr() && !PlayerTweaks.mc.field_71439_g.func_184218_aH()) {
                event.getMovementInput();
                movementInput.field_78902_a *= 5.0f;
                event.getMovementInput();
                movementInput2.field_192832_b *= 5.0f;
            }
            return;
        }, (Predicate<InputUpdateEvent>[])new Predicate[0]);
        this.entityCollisionEventListener = new Listener<EntityCollisionEvent>(event -> {
            if (this.noPush.getValue()) {
                event.cancel();
            }
            return;
        }, (Predicate<EntityCollisionEvent>[])new Predicate[0]);
        this.receiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (this.antiKnockBack.getValue()) {
                if (event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity)event.getPacket()).func_149412_c() == PlayerTweaks.mc.field_71439_g.func_145782_y()) {
                    event.cancel();
                }
                if (event.getPacket() instanceof SPacketExplosion) {
                    event.cancel();
                }
            }
            return;
        }, (Predicate<PacketEvent.Receive>[])new Predicate[0]);
        CPacketPlayer packet;
        this.sendListener = new Listener<PacketEvent.Send>(event -> {
            if (this.noFall.getValue() && event.getPacket() instanceof CPacketPlayer && PlayerTweaks.mc.field_71439_g.field_70143_R >= 3.0) {
                packet = (CPacketPlayer)event.getPacket();
                packet.field_149474_g = true;
            }
            return;
        }, (Predicate<PacketEvent.Send>[])new Predicate[0]);
        this.waterPushEventListener = new Listener<WaterPushEvent>(event -> {
            if (this.noPush.getValue()) {
                event.cancel();
            }
        }, (Predicate<WaterPushEvent>[])new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        if (this.guiMove.getValue() && PlayerTweaks.mc.field_71462_r != null && !(PlayerTweaks.mc.field_71462_r instanceof GuiChat)) {
            if (Keyboard.isKeyDown(200)) {
                final EntityPlayerSP field_71439_g = PlayerTweaks.mc.field_71439_g;
                field_71439_g.field_70125_A -= 5.0f;
            }
            if (Keyboard.isKeyDown(208)) {
                final EntityPlayerSP field_71439_g2 = PlayerTweaks.mc.field_71439_g;
                field_71439_g2.field_70125_A += 5.0f;
            }
            if (Keyboard.isKeyDown(205)) {
                final EntityPlayerSP field_71439_g3 = PlayerTweaks.mc.field_71439_g;
                field_71439_g3.field_70177_z += 5.0f;
            }
            if (Keyboard.isKeyDown(203)) {
                final EntityPlayerSP field_71439_g4 = PlayerTweaks.mc.field_71439_g;
                field_71439_g4.field_70177_z -= 5.0f;
            }
            if (PlayerTweaks.mc.field_71439_g.field_70125_A > 90.0f) {
                PlayerTweaks.mc.field_71439_g.field_70125_A = 90.0f;
            }
            if (PlayerTweaks.mc.field_71439_g.field_70125_A < -90.0f) {
                PlayerTweaks.mc.field_71439_g.field_70125_A = -90.0f;
            }
        }
    }
}
