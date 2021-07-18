// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.setting.values;

import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.settings.NumberSetting;
import com.gamesense.api.setting.Setting;

public class IntegerSetting extends Setting<Integer> implements NumberSetting
{
    private final int min;
    private final int max;
    
    public IntegerSetting(final String name, final Module module, final int value, final int min, final int max) {
        super(value, name, module);
        this.min = min;
        this.max = max;
    }
    
    public int getMin() {
        return this.min;
    }
    
    public int getMax() {
        return this.max;
    }
    
    @Override
    public double getNumber() {
        return this.getValue();
    }
    
    @Override
    public void setNumber(final double value) {
        this.setValue((int)Math.round(value));
    }
    
    @Override
    public double getMaximumValue() {
        return this.getMax();
    }
    
    @Override
    public double getMinimumValue() {
        return this.getMin();
    }
    
    @Override
    public int getPrecision() {
        return 0;
    }
}
