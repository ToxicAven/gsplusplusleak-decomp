// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;
import org.lwjgl.input.Keyboard;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.setting.values.ModeSetting;
import java.util.List;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.setting.SettingsManager;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.client.GameSense;
import com.gamesense.api.event.events.RenderEvent;
import net.minecraft.client.Minecraft;
import com.lukflug.panelstudio.settings.KeybindSetting;
import com.lukflug.panelstudio.settings.Toggleable;

public abstract class Module implements Toggleable, KeybindSetting
{
    protected static final Minecraft mc;
    private final String name;
    private final Category category;
    private final int priority;
    private int bind;
    private boolean enabled;
    private boolean drawn;
    private boolean toggleMsg;
    private String disabledMessage;
    
    public Module() {
        this.name = this.getDeclaration().name();
        this.category = this.getDeclaration().category();
        this.priority = this.getDeclaration().priority();
        this.bind = this.getDeclaration().bind();
        this.enabled = this.getDeclaration().enabled();
        this.drawn = this.getDeclaration().drawn();
        this.toggleMsg = this.getDeclaration().toggleMsg();
        this.disabledMessage = this.name + " turned OFF!";
    }
    
    private Declaration getDeclaration() {
        return this.getClass().getAnnotation(Declaration.class);
    }
    
    protected void onEnable() {
    }
    
    protected void onDisable() {
    }
    
    public void onUpdate() {
    }
    
    public void onRender() {
    }
    
    public void onWorldRender(final RenderEvent event) {
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setDisabledMessage(final String message) {
        this.disabledMessage = message;
    }
    
    public void enable() {
        this.setEnabled(true);
        GameSense.EVENT_BUS.subscribe(this);
        this.onEnable();
        if (this.toggleMsg && Module.mc.field_71439_g != null) {
            MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + this.name + " turned ON!");
        }
    }
    
    public void disable() {
        this.setEnabled(false);
        GameSense.EVENT_BUS.unsubscribe(this);
        this.onDisable();
        if (this.toggleMsg && Module.mc.field_71439_g != null) {
            MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + this.disabledMessage);
        }
        this.setDisabledMessage(this.name + " turned OFF!");
    }
    
    @Override
    public void toggle() {
        if (this.isEnabled()) {
            this.disable();
        }
        else if (!this.isEnabled()) {
            this.enable();
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public Category getCategory() {
        return this.category;
    }
    
    public int getPriority() {
        return this.priority;
    }
    
    public int getBind() {
        return this.bind;
    }
    
    public void setBind(final int bind) {
        if (bind >= 0 && bind <= 255) {
            this.bind = bind;
        }
    }
    
    public String getHudInfo() {
        return "";
    }
    
    public boolean isDrawn() {
        return this.drawn;
    }
    
    public void setDrawn(final boolean drawn) {
        this.drawn = drawn;
    }
    
    public boolean isToggleMsg() {
        return this.toggleMsg;
    }
    
    public void setToggleMsg(final boolean toggleMsg) {
        this.toggleMsg = toggleMsg;
    }
    
    protected IntegerSetting registerInteger(final String name, final int value, final int min, final int max) {
        final IntegerSetting integerSetting = new IntegerSetting(name, this, value, min, max);
        SettingsManager.addSetting(integerSetting);
        return integerSetting;
    }
    
    protected DoubleSetting registerDouble(final String name, final double value, final double min, final double max) {
        final DoubleSetting doubleSetting = new DoubleSetting(name, this, value, min, max);
        SettingsManager.addSetting(doubleSetting);
        return doubleSetting;
    }
    
    protected BooleanSetting registerBoolean(final String name, final boolean value) {
        final BooleanSetting booleanSetting = new BooleanSetting(name, this, value);
        SettingsManager.addSetting(booleanSetting);
        return booleanSetting;
    }
    
    protected ModeSetting registerMode(final String name, final List<String> modes, final String value) {
        final ModeSetting modeSetting = new ModeSetting(name, this, value, modes);
        SettingsManager.addSetting(modeSetting);
        return modeSetting;
    }
    
    protected ColorSetting registerColor(final String name, final GSColor color) {
        final ColorSetting colorSetting = new ColorSetting(name, this, false, color);
        SettingsManager.addSetting(colorSetting);
        return colorSetting;
    }
    
    protected ColorSetting registerColor(final String name) {
        return this.registerColor(name, new GSColor(90, 145, 240));
    }
    
    @Override
    public boolean isOn() {
        return this.enabled;
    }
    
    @Override
    public int getKey() {
        return this.getBind();
    }
    
    @Override
    public void setKey(final int key) {
        this.setBind(key);
    }
    
    @Override
    public String getKeyName() {
        if (this.bind <= 0 || this.bind > 255) {
            return "NONE";
        }
        return Keyboard.getKeyName(this.bind);
    }
    
    static {
        mc = Minecraft.func_71410_x();
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface Declaration {
        String name();
        
        Category category();
        
        int priority() default 0;
        
        int bind() default 0;
        
        boolean enabled() default false;
        
        boolean drawn() default true;
        
        boolean toggleMsg() default false;
    }
}
