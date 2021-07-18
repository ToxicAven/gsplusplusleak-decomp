// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.entity.EntityPlayerSP;
import java.util.function.Predicate;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.MobEffects;
import com.gamesense.api.util.world.EntityUtil;
import net.minecraft.entity.EntityLivingBase;
import com.gamesense.api.util.world.MotionUtil;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.PlayerMoveEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.api.util.misc.Timer;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Speed", category = Category.Movement)
public class Speed extends Module
{
    ModeSetting mode;
    DoubleSetting yPortSpeed;
    DoubleSetting jumpHeight;
    DoubleSetting timerVal;
    private boolean slowDown;
    private double playerSpeed;
    private final Timer timer;
    @EventHandler
    private final Listener<PlayerMoveEvent> playerMoveEventListener;
    
    public Speed() {
        this.mode = this.registerMode("Mode", Arrays.asList("Strafe", "Fake", "YPort"), "Strafe");
        this.yPortSpeed = this.registerDouble("Y Port Speed", 0.06, 0.01, 0.15);
        this.jumpHeight = this.registerDouble("Jump Speed", 0.41, 0.0, 1.0);
        this.timerVal = this.registerDouble("Timer Speed", 1.15, 1.0, 1.5);
        this.timer = new Timer();
        double speedY;
        EntityPlayerSP field_71439_g;
        final double field_70181_x;
        double playerSpeed;
        Object o;
        final Object playerSpeed2;
        final Object o2;
        double[] dir;
        this.playerMoveEventListener = new Listener<PlayerMoveEvent>(event -> {
            if (!Speed.mc.field_71439_g.func_180799_ab() && !Speed.mc.field_71439_g.func_70090_H() && !Speed.mc.field_71439_g.func_70617_f_() && !Speed.mc.field_71439_g.field_70134_J) {
                if (this.mode.getValue().equalsIgnoreCase("Strafe")) {
                    speedY = this.jumpHeight.getValue();
                    if (Speed.mc.field_71439_g.field_70122_E && MotionUtil.isMoving((EntityLivingBase)Speed.mc.field_71439_g) && this.timer.hasReached(300L)) {
                        EntityUtil.setTimer(this.timerVal.getValue().floatValue());
                        if (Speed.mc.field_71439_g.func_70644_a(MobEffects.field_76430_j)) {
                            speedY += (Speed.mc.field_71439_g.func_70660_b(MobEffects.field_76430_j).func_76458_c() + 1) * 0.1f;
                        }
                        field_71439_g = Speed.mc.field_71439_g;
                        event.setY(field_71439_g.field_70181_x = field_70181_x);
                        this.playerSpeed = MotionUtil.getBaseMoveSpeed() * ((EntityUtil.isColliding(0.0, -0.5, 0.0) instanceof BlockLiquid && !EntityUtil.isInLiquid()) ? 0.9 : 1.901);
                        this.slowDown = true;
                        this.timer.reset();
                    }
                    else {
                        EntityUtil.resetTimer();
                        if (this.slowDown || Speed.mc.field_71439_g.field_70123_F) {
                            playerSpeed = this.playerSpeed;
                            if (EntityUtil.isColliding(0.0, -0.8, 0.0) instanceof BlockLiquid && !EntityUtil.isInLiquid()) {
                                o = 0.4;
                            }
                            else {
                                MotionUtil.getBaseMoveSpeed();
                                this.playerSpeed = (double)playerSpeed2;
                                o = o2 * playerSpeed2;
                            }
                            this.playerSpeed = (double)(playerSpeed - o);
                            this.slowDown = false;
                        }
                        else {
                            this.playerSpeed -= this.playerSpeed / 159.0;
                        }
                    }
                    this.playerSpeed = Math.max(this.playerSpeed, MotionUtil.getBaseMoveSpeed());
                    dir = MotionUtil.forward(this.playerSpeed);
                    event.setX(dir[0]);
                    event.setZ(dir[1]);
                }
            }
        }, (Predicate<PlayerMoveEvent>[])new Predicate[0]);
    }
    
    public void onEnable() {
        this.playerSpeed = MotionUtil.getBaseMoveSpeed();
    }
    
    public void onDisable() {
        this.timer.reset();
        EntityUtil.resetTimer();
    }
    
    @Override
    public void onUpdate() {
        if (Speed.mc.field_71439_g == null || Speed.mc.field_71441_e == null) {
            this.disable();
            return;
        }
        if (this.mode.getValue().equalsIgnoreCase("YPort")) {
            this.handleYPortSpeed();
        }
    }
    
    private void handleYPortSpeed() {
        if (!MotionUtil.isMoving((EntityLivingBase)Speed.mc.field_71439_g) || (Speed.mc.field_71439_g.func_70090_H() && Speed.mc.field_71439_g.func_180799_ab()) || Speed.mc.field_71439_g.field_70123_F) {
            return;
        }
        if (Speed.mc.field_71439_g.field_70122_E) {
            EntityUtil.setTimer(1.15f);
            Speed.mc.field_71439_g.func_70664_aZ();
            MotionUtil.setSpeed((EntityLivingBase)Speed.mc.field_71439_g, MotionUtil.getBaseMoveSpeed() + this.yPortSpeed.getValue());
        }
        else {
            Speed.mc.field_71439_g.field_70181_x = -1.0;
            EntityUtil.resetTimer();
        }
    }
    
    @Override
    public String getHudInfo() {
        String t = "";
        if (this.mode.getValue().equalsIgnoreCase("Strafe")) {
            t = "[" + ChatFormatting.WHITE + "Strafe" + ChatFormatting.GRAY + "]";
        }
        else if (this.mode.getValue().equalsIgnoreCase("YPort")) {
            t = "[" + ChatFormatting.WHITE + "YPort" + ChatFormatting.GRAY + "]";
        }
        else if (this.mode.getValue().equalsIgnoreCase("Fake")) {
            t = "[" + ChatFormatting.WHITE + "Fake" + ChatFormatting.GRAY + "]";
        }
        return t;
    }
}
