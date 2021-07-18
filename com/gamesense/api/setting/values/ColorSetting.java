// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.setting.values;

import java.awt.Color;
import com.gamesense.client.module.Module;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.setting.Setting;

public class ColorSetting extends Setting<GSColor> implements com.lukflug.panelstudio.settings.ColorSetting
{
    private boolean rainbow;
    
    public ColorSetting(final String name, final Module module, final boolean rainbow, final GSColor value) {
        super(value, name, module);
        this.rainbow = rainbow;
    }
    
    @Override
    public GSColor getValue() {
        if (this.rainbow) {
            return getRainbowColor(0, 0);
        }
        return super.getValue();
    }
    
    public static GSColor getRainbowColor(final int incr, final int multiply) {
        return GSColor.fromHSB((System.currentTimeMillis() + incr * multiply) % 11520L / 11520.0f, 1.0f, 1.0f);
    }
    
    public static GSColor getRainbowSin(final int incr, final int multiply, final double height, final int multiplyHeight, final double millSin) {
        return GSColor.fromHSB((float)(height * multiplyHeight * Math.sin((System.currentTimeMillis() + incr / millSin * multiply) % 11520.0 / 11520.0)), 1.0f, 1.0f);
    }
    
    public static GSColor getRainbowTan(final int incr, final int multiply, final double height, final int multiplyHeight, final double millSin) {
        return GSColor.fromHSB((float)(height * multiplyHeight * Math.tan((System.currentTimeMillis() + incr / millSin * multiply % 11520.0) / 11520.0)), 1.0f, 1.0f);
    }
    
    public static GSColor getRainbowSec(final int incr, final int multiply, final double height, final int multiplyHeight, final double millSin) {
        return GSColor.fromHSB((float)(height * multiplyHeight * (1.0 / Math.sin((System.currentTimeMillis() + (float)incr / millSin * multiply) % 11520.0 / 11520.0))), 1.0f, 1.0f);
    }
    
    public static GSColor getRainbowCosec(final int incr, final int multiply, final double height, final int multiplyHeight, final double millSin) {
        return GSColor.fromHSB((float)(height * multiplyHeight * (1.0 / Math.cos((System.currentTimeMillis() + incr / millSin * multiply) % 11520.0 / 11520.0))), 1.0f, 1.0f);
    }
    
    public static GSColor getRainbowCoTan(final int incr, final int multiply, final double height, final int multiplyHeight, final double millSin) {
        return GSColor.fromHSB((float)(height * multiplyHeight * Math.tan((System.currentTimeMillis() + incr / millSin * multiply) % 11520.0 / 11520.0)), 1.0f, 1.0f);
    }
    
    public int toInteger() {
        return this.getValue().getRGB() & 16777215 + (this.rainbow ? 1 : 0) * 16777216;
    }
    
    public void fromInteger(final int number) {
        this.rainbow = ((number & 0x1000000) != 0x0);
        super.setValue(this.rainbow ? GSColor.fromHSB(System.currentTimeMillis() % 11520L / 11520.0f, 1.0f, 1.0f) : new GSColor(number & 0xFFFFFF));
    }
    
    @Override
    public void setValue(final Color value) {
        super.setValue(new GSColor(value));
    }
    
    @Override
    public Color getColor() {
        return super.getValue();
    }
    
    @Override
    public boolean getRainbow() {
        return this.rainbow;
    }
    
    @Override
    public void setRainbow(final boolean rainbow) {
        this.rainbow = rainbow;
    }
}
