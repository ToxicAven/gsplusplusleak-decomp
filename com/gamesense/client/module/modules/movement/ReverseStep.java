// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.movement;

import net.minecraft.entity.Entity;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "ReverseStep", category = Category.Movement)
public class ReverseStep extends Module
{
    DoubleSetting height;
    
    public ReverseStep() {
        this.height = this.registerDouble("Height", 2.5, 0.5, 2.5);
    }
    
    @Override
    public void onUpdate() {
        if (ReverseStep.mc.field_71441_e == null || ReverseStep.mc.field_71439_g == null || ReverseStep.mc.field_71439_g.func_70090_H() || ReverseStep.mc.field_71439_g.func_180799_ab() || ReverseStep.mc.field_71439_g.func_70617_f_() || ReverseStep.mc.field_71474_y.field_74314_A.func_151470_d()) {
            return;
        }
        if (ModuleManager.isModuleEnabled(Speed.class)) {
            return;
        }
        if (ReverseStep.mc.field_71439_g != null && ReverseStep.mc.field_71439_g.field_70122_E && !ReverseStep.mc.field_71439_g.func_70090_H() && !ReverseStep.mc.field_71439_g.func_70617_f_()) {
            for (double y = 0.0; y < this.height.getValue() + 0.5; y += 0.01) {
                if (!ReverseStep.mc.field_71441_e.func_184144_a((Entity)ReverseStep.mc.field_71439_g, ReverseStep.mc.field_71439_g.func_174813_aQ().func_72317_d(0.0, -y, 0.0)).isEmpty()) {
                    ReverseStep.mc.field_71439_g.field_70181_x = -10.0;
                    break;
                }
            }
        }
    }
}
