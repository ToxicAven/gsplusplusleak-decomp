// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.setting.values;

import com.gamesense.client.module.Module;
import java.util.List;
import com.lukflug.panelstudio.settings.EnumSetting;
import com.gamesense.api.setting.Setting;

public class ModeSetting extends Setting<String> implements EnumSetting
{
    private final List<String> modes;
    
    public ModeSetting(final String name, final Module module, final String value, final List<String> modes) {
        super(value, name, module);
        this.modes = modes;
    }
    
    public List<String> getModes() {
        return this.modes;
    }
    
    @Override
    public void increment() {
        int modeIndex = this.modes.indexOf(((Setting<Object>)this).getValue());
        modeIndex = (modeIndex + 1) % this.modes.size();
        this.setValue(this.modes.get(modeIndex));
    }
    
    @Override
    public String getValueName() {
        return this.getValue();
    }
}
