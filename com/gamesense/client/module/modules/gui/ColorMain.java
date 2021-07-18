// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.gui;

import com.gamesense.api.util.render.GSColor;
import net.minecraft.util.text.TextFormatting;
import java.util.Arrays;
import com.gamesense.api.util.misc.ColorUtil;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Colors", category = Category.GUI, drawn = false)
public class ColorMain extends Module
{
    public BooleanSetting customFont;
    public BooleanSetting textFont;
    public ModeSetting friendColor;
    public ModeSetting enemyColor;
    public ModeSetting chatEnableColor;
    public ModeSetting chatDisableColor;
    public ModeSetting colorModel;
    
    public ColorMain() {
        this.customFont = this.registerBoolean("Custom Font", true);
        this.textFont = this.registerBoolean("Custom Text", false);
        this.friendColor = this.registerMode("Friend Color", ColorUtil.colors, "Blue");
        this.enemyColor = this.registerMode("Enemy Color", ColorUtil.colors, "Red");
        this.chatEnableColor = this.registerMode("Msg Enbl", ColorUtil.colors, "Green");
        this.chatDisableColor = this.registerMode("Msg Dsbl", ColorUtil.colors, "Red");
        this.colorModel = this.registerMode("Color Model", Arrays.asList("RGB", "HSB"), "HSB");
    }
    
    public void onEnable() {
        this.disable();
    }
    
    public TextFormatting getFriendColor() {
        return ColorUtil.settingToTextFormatting(this.friendColor);
    }
    
    public TextFormatting getEnemyColor() {
        return ColorUtil.settingToTextFormatting(this.enemyColor);
    }
    
    public TextFormatting getEnabledColor() {
        return ColorUtil.settingToTextFormatting(this.chatEnableColor);
    }
    
    public TextFormatting getDisabledColor() {
        return ColorUtil.settingToTextFormatting(this.chatDisableColor);
    }
    
    public GSColor getFriendGSColor() {
        return new GSColor(ColorUtil.settingToColor(this.friendColor));
    }
    
    public GSColor getEnemyGSColor() {
        return new GSColor(ColorUtil.settingToColor(this.enemyColor));
    }
}
