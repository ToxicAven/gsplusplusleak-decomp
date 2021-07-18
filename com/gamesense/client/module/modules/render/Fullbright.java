// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.Potion;
import java.util.Arrays;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Fullbright", category = Category.Render)
public class Fullbright extends Module
{
    ModeSetting mode;
    float oldGamma;
    
    public Fullbright() {
        this.mode = this.registerMode("Mode", Arrays.asList("Gamma", "Potion"), "Gamma");
    }
    
    public void onEnable() {
        this.oldGamma = Fullbright.mc.field_71474_y.field_74333_Y;
    }
    
    @Override
    public void onUpdate() {
        if (this.mode.getValue().equalsIgnoreCase("Gamma")) {
            Fullbright.mc.field_71474_y.field_74333_Y = 666.0f;
            Fullbright.mc.field_71439_g.func_184589_d(Potion.func_188412_a(16));
        }
        else if (this.mode.getValue().equalsIgnoreCase("Potion")) {
            final PotionEffect potionEffect = new PotionEffect(Potion.func_188412_a(16), 123456789, 5);
            potionEffect.func_100012_b(true);
            Fullbright.mc.field_71439_g.func_70690_d(potionEffect);
        }
    }
    
    public void onDisable() {
        Fullbright.mc.field_71474_y.field_74333_Y = this.oldGamma;
        Fullbright.mc.field_71439_g.func_184589_d(Potion.func_188412_a(16));
    }
}
