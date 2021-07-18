// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import net.minecraft.item.ItemSword;
import net.minecraft.client.renderer.ItemRenderer;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "RenderTweaks", category = Category.Render)
public class RenderTweaks extends Module
{
    public BooleanSetting viewClip;
    BooleanSetting nekoAnimation;
    BooleanSetting lowOffhand;
    DoubleSetting lowOffhandSlider;
    BooleanSetting fovChanger;
    IntegerSetting fovChangerSlider;
    ItemRenderer itemRenderer;
    private float oldFOV;
    
    public RenderTweaks() {
        this.viewClip = this.registerBoolean("View Clip", false);
        this.nekoAnimation = this.registerBoolean("Neko Animation", false);
        this.lowOffhand = this.registerBoolean("Low Offhand", false);
        this.lowOffhandSlider = this.registerDouble("Offhand Height", 1.0, 0.1, 1.0);
        this.fovChanger = this.registerBoolean("FOV", false);
        this.fovChangerSlider = this.registerInteger("FOV Slider", 90, 70, 200);
        this.itemRenderer = RenderTweaks.mc.field_71460_t.field_78516_c;
    }
    
    @Override
    public void onUpdate() {
        if (this.nekoAnimation.getValue() && RenderTweaks.mc.field_71439_g.func_184614_ca().func_77973_b() instanceof ItemSword && RenderTweaks.mc.field_71460_t.field_78516_c.field_187470_g >= 0.9) {
            RenderTweaks.mc.field_71460_t.field_78516_c.field_187469_f = 1.0f;
            RenderTweaks.mc.field_71460_t.field_78516_c.field_187467_d = RenderTweaks.mc.field_71439_g.func_184614_ca();
        }
        if (this.lowOffhand.getValue()) {
            this.itemRenderer.field_187471_h = this.lowOffhandSlider.getValue().floatValue();
        }
        if (this.fovChanger.getValue()) {
            RenderTweaks.mc.field_71474_y.field_74334_X = this.fovChangerSlider.getValue();
        }
        if (!this.fovChanger.getValue()) {
            RenderTweaks.mc.field_71474_y.field_74334_X = this.oldFOV;
        }
    }
    
    public void onEnable() {
        this.oldFOV = RenderTweaks.mc.field_71474_y.field_74334_X;
    }
    
    public void onDisable() {
        RenderTweaks.mc.field_71474_y.field_74334_X = this.oldFOV;
    }
}
