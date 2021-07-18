// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.setting.values;

import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.settings.Toggleable;
import com.gamesense.api.setting.Setting;

public class BooleanSetting extends Setting<Boolean> implements Toggleable
{
    public BooleanSetting(final String name, final Module module, final boolean value) {
        super(value, name, module);
    }
    
    @Override
    public void toggle() {
        this.setValue(!this.getValue());
    }
    
    @Override
    public boolean isOn() {
        return this.getValue();
    }
}
