// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import net.minecraft.init.Items;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "FastPlace", category = Category.Misc)
public class FastPlace extends Module
{
    BooleanSetting exp;
    BooleanSetting crystals;
    BooleanSetting offhandCrystal;
    BooleanSetting everything;
    
    public FastPlace() {
        this.exp = this.registerBoolean("Exp", false);
        this.crystals = this.registerBoolean("Crystals", false);
        this.offhandCrystal = this.registerBoolean("Offhand Crystal", false);
        this.everything = this.registerBoolean("Everything", false);
    }
    
    @Override
    public void onUpdate() {
        if ((this.exp.getValue() && FastPlace.mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_151062_by) || FastPlace.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_151062_by) {
            FastPlace.mc.field_71467_ac = 0;
        }
        if (this.crystals.getValue() && FastPlace.mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP) {
            FastPlace.mc.field_71467_ac = 0;
        }
        if (this.offhandCrystal.getValue() && FastPlace.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP) {
            FastPlace.mc.field_71467_ac = 0;
        }
        if (this.everything.getValue()) {
            FastPlace.mc.field_71467_ac = 0;
        }
        FastPlace.mc.field_71442_b.field_78781_i = 0;
    }
}
