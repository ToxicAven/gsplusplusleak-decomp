// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.movement;

import com.gamesense.api.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import java.text.DecimalFormat;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import com.gamesense.api.util.world.MotionUtil;
import net.minecraft.entity.Entity;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.client.module.ModuleManager;
import java.util.Arrays;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Step", category = Category.Movement)
public class Step extends Module
{
    DoubleSetting height;
    BooleanSetting timer;
    BooleanSetting reverse;
    ModeSetting mode;
    private int ticks;
    
    public Step() {
        this.height = this.registerDouble("Height", 2.5, 0.5, 2.5);
        this.timer = this.registerBoolean("Timer", false);
        this.reverse = this.registerBoolean("Reverse", false);
        this.mode = this.registerMode("Modes", Arrays.asList("Normal", "Vanilla"), "Normal");
        this.ticks = 0;
    }
    
    @Override
    public void onUpdate() {
        if (Step.mc.field_71441_e == null || Step.mc.field_71439_g == null) {
            return;
        }
        if (Step.mc.field_71439_g.func_70090_H() || Step.mc.field_71439_g.func_180799_ab() || Step.mc.field_71439_g.func_70617_f_() || Step.mc.field_71474_y.field_74314_A.func_151470_d()) {
            return;
        }
        if (ModuleManager.isModuleEnabled(Speed.class)) {
            return;
        }
        if (this.mode.getValue().equalsIgnoreCase("Normal")) {
            if (this.timer.getValue()) {
                if (this.ticks == 0) {
                    EntityUtil.resetTimer();
                }
                else {
                    --this.ticks;
                }
            }
            if (Step.mc.field_71439_g != null && Step.mc.field_71439_g.field_70122_E && !Step.mc.field_71439_g.func_70090_H() && !Step.mc.field_71439_g.func_70617_f_() && this.reverse.getValue()) {
                for (double y = 0.0; y < this.height.getValue() + 0.5; y += 0.01) {
                    if (!Step.mc.field_71441_e.func_184144_a((Entity)Step.mc.field_71439_g, Step.mc.field_71439_g.func_174813_aQ().func_72317_d(0.0, -y, 0.0)).isEmpty()) {
                        Step.mc.field_71439_g.field_70181_x = -10.0;
                        break;
                    }
                }
            }
            final double[] dir = MotionUtil.forward(0.1);
            boolean twofive = false;
            boolean two = false;
            boolean onefive = false;
            boolean one = false;
            if (Step.mc.field_71441_e.func_184144_a((Entity)Step.mc.field_71439_g, Step.mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 2.6, dir[1])).isEmpty() && !Step.mc.field_71441_e.func_184144_a((Entity)Step.mc.field_71439_g, Step.mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 2.4, dir[1])).isEmpty()) {
                twofive = true;
            }
            if (Step.mc.field_71441_e.func_184144_a((Entity)Step.mc.field_71439_g, Step.mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 2.1, dir[1])).isEmpty() && !Step.mc.field_71441_e.func_184144_a((Entity)Step.mc.field_71439_g, Step.mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 1.9, dir[1])).isEmpty()) {
                two = true;
            }
            if (Step.mc.field_71441_e.func_184144_a((Entity)Step.mc.field_71439_g, Step.mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 1.6, dir[1])).isEmpty() && !Step.mc.field_71441_e.func_184144_a((Entity)Step.mc.field_71439_g, Step.mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 1.4, dir[1])).isEmpty()) {
                onefive = true;
            }
            if (Step.mc.field_71441_e.func_184144_a((Entity)Step.mc.field_71439_g, Step.mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 1.0, dir[1])).isEmpty() && !Step.mc.field_71441_e.func_184144_a((Entity)Step.mc.field_71439_g, Step.mc.field_71439_g.func_174813_aQ().func_72317_d(dir[0], 0.6, dir[1])).isEmpty()) {
                one = true;
            }
            if (Step.mc.field_71439_g.field_70123_F && (Step.mc.field_71439_g.field_191988_bg != 0.0f || Step.mc.field_71439_g.field_70702_br != 0.0f) && Step.mc.field_71439_g.field_70122_E) {
                if (one && this.height.getValue() >= 1.0) {
                    final double[] oneOffset = { 0.42, 0.753 };
                    for (int i = 0; i < oneOffset.length; ++i) {
                        Step.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Step.mc.field_71439_g.field_70165_t, Step.mc.field_71439_g.field_70163_u + oneOffset[i], Step.mc.field_71439_g.field_70161_v, Step.mc.field_71439_g.field_70122_E));
                    }
                    if (this.timer.getValue()) {
                        EntityUtil.setTimer(0.6f);
                    }
                    Step.mc.field_71439_g.func_70107_b(Step.mc.field_71439_g.field_70165_t, Step.mc.field_71439_g.field_70163_u + 1.0, Step.mc.field_71439_g.field_70161_v);
                    this.ticks = 1;
                }
                if (onefive && this.height.getValue() >= 1.5) {
                    final double[] oneFiveOffset = { 0.42, 0.75, 1.0, 1.16, 1.23, 1.2 };
                    for (int i = 0; i < oneFiveOffset.length; ++i) {
                        Step.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Step.mc.field_71439_g.field_70165_t, Step.mc.field_71439_g.field_70163_u + oneFiveOffset[i], Step.mc.field_71439_g.field_70161_v, Step.mc.field_71439_g.field_70122_E));
                    }
                    if (this.timer.getValue()) {
                        EntityUtil.setTimer(0.35f);
                    }
                    Step.mc.field_71439_g.func_70107_b(Step.mc.field_71439_g.field_70165_t, Step.mc.field_71439_g.field_70163_u + 1.5, Step.mc.field_71439_g.field_70161_v);
                    this.ticks = 1;
                }
                if (two && this.height.getValue() >= 2.0) {
                    final double[] twoOffset = { 0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43 };
                    for (int i = 0; i < twoOffset.length; ++i) {
                        Step.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Step.mc.field_71439_g.field_70165_t, Step.mc.field_71439_g.field_70163_u + twoOffset[i], Step.mc.field_71439_g.field_70161_v, Step.mc.field_71439_g.field_70122_E));
                    }
                    if (this.timer.getValue()) {
                        EntityUtil.setTimer(0.25f);
                    }
                    Step.mc.field_71439_g.func_70107_b(Step.mc.field_71439_g.field_70165_t, Step.mc.field_71439_g.field_70163_u + 2.0, Step.mc.field_71439_g.field_70161_v);
                    this.ticks = 2;
                }
                if (twofive && this.height.getValue() >= 2.5) {
                    final double[] twoFiveOffset = { 0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907 };
                    for (int i = 0; i < twoFiveOffset.length; ++i) {
                        Step.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(Step.mc.field_71439_g.field_70165_t, Step.mc.field_71439_g.field_70163_u + twoFiveOffset[i], Step.mc.field_71439_g.field_70161_v, Step.mc.field_71439_g.field_70122_E));
                    }
                    if (this.timer.getValue()) {
                        EntityUtil.setTimer(0.15f);
                    }
                    Step.mc.field_71439_g.func_70107_b(Step.mc.field_71439_g.field_70165_t, Step.mc.field_71439_g.field_70163_u + 2.5, Step.mc.field_71439_g.field_70161_v);
                    this.ticks = 2;
                }
            }
        }
        if (this.mode.getValue().equalsIgnoreCase("Vanilla")) {
            final DecimalFormat df = new DecimalFormat("#");
            Step.mc.field_71439_g.field_70138_W = Float.parseFloat(df.format(((Setting<Object>)this.height).getValue()));
        }
    }
    
    public void onDisable() {
        Step.mc.field_71439_g.field_70138_W = 0.5f;
    }
    
    @Override
    public String getHudInfo() {
        String t = "";
        if (this.mode.getValue().equalsIgnoreCase("Normal")) {
            t = "[" + ChatFormatting.WHITE + "Normal" + ChatFormatting.GRAY + "]";
        }
        if (this.mode.getValue().equalsIgnoreCase("Vanilla")) {
            t = "[" + ChatFormatting.WHITE + "Vanilla" + ChatFormatting.GRAY + "]";
        }
        return t;
    }
}
