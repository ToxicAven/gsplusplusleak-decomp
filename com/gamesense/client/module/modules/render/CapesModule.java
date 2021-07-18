// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import java.util.Arrays;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Capes", category = Category.Render, drawn = false)
public class CapesModule extends Module
{
    public ModeSetting capeMode;
    
    public CapesModule() {
        this.capeMode = this.registerMode("Type", Arrays.asList("Old", "New"), "New");
    }
    
    public static String getUsName() {
        return CapesModule.mc.field_71439_g.func_70005_c_();
    }
}
