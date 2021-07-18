// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.setting.values;

import com.gamesense.client.module.Module;
import com.lukflug.panelstudio.settings.NumberSetting;
import com.gamesense.api.setting.Setting;

public class DoubleSetting extends Setting<Double> implements NumberSetting
{
    private final double min;
    private final double max;
    
    public DoubleSetting(final String name, final Module module, final double value, final double min, final double max) {
        super(value, name, module);
        this.min = min;
        this.max = max;
    }
    
    public double getMin() {
        return this.min;
    }
    
    public double getMax() {
        return this.max;
    }
    
    @Override
    public double getNumber() {
        return this.getValue();
    }
    
    @Override
    public void setNumber(final double value) {
        this.setValue(value);
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
        return 2;
    }
}
