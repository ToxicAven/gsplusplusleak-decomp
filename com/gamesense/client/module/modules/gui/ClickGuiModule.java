// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.gui;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.Announcer;
import com.gamesense.client.GameSense;
import java.util.Arrays;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "ClickGUI", category = Category.GUI, bind = 24, drawn = false)
public class ClickGuiModule extends Module
{
    public IntegerSetting opacity;
    public IntegerSetting scrollSpeed;
    public ColorSetting outlineColor;
    public ColorSetting enabledColor;
    public ColorSetting backgroundColor;
    public ColorSetting settingBackgroundColor;
    public ColorSetting fontColor;
    public IntegerSetting animationSpeed;
    public ModeSetting scrolling;
    public BooleanSetting showHUD;
    public ModeSetting theme;
    
    public ClickGuiModule() {
        this.opacity = this.registerInteger("Opacity", 150, 50, 255);
        this.scrollSpeed = this.registerInteger("Scroll Speed", 10, 1, 20);
        this.outlineColor = this.registerColor("Outline", new GSColor(255, 0, 0, 255));
        this.enabledColor = this.registerColor("Enabled", new GSColor(255, 0, 0, 255));
        this.backgroundColor = this.registerColor("Background", new GSColor(0, 0, 0, 255));
        this.settingBackgroundColor = this.registerColor("Setting", new GSColor(30, 30, 30, 255));
        this.fontColor = this.registerColor("Font", new GSColor(255, 255, 255, 255));
        this.animationSpeed = this.registerInteger("Animation Speed", 200, 0, 1000);
        this.scrolling = this.registerMode("Scrolling", Arrays.asList("Screen", "Container"), "Screen");
        this.showHUD = this.registerBoolean("Show HUD Panels", false);
        this.theme = this.registerMode("Skin", Arrays.asList("2.2", "2.1.2", "2.0"), "2.2");
    }
    
    public void onEnable() {
        GameSense.INSTANCE.gameSenseGUI.enterGUI();
        final Announcer announcer = ModuleManager.getModule(Announcer.class);
        if (announcer.clickGui.getValue() && announcer.isEnabled() && ClickGuiModule.mc.field_71439_g != null) {
            if (announcer.clientSide.getValue()) {
                MessageBus.sendClientPrefixMessage(Announcer.guiMessage);
            }
            else {
                MessageBus.sendServerMessage(Announcer.guiMessage);
            }
        }
        this.disable();
    }
}
