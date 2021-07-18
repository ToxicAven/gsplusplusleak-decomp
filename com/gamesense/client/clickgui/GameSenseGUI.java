// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.clickgui;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import com.lukflug.panelstudio.settings.KeybindSetting;
import com.gamesense.client.clickgui.components.GameSenseKeybind;
import com.gamesense.client.clickgui.components.GameSenseToggleMessage;
import com.gamesense.client.clickgui.components.GameSenseColor;
import com.lukflug.panelstudio.settings.EnumSetting;
import com.lukflug.panelstudio.settings.EnumComponent;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.lukflug.panelstudio.settings.NumberComponent;
import com.gamesense.api.setting.values.IntegerSetting;
import com.lukflug.panelstudio.settings.BooleanComponent;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.setting.SettingsManager;
import com.lukflug.panelstudio.Component;
import com.lukflug.panelstudio.theme.ColorScheme;
import com.lukflug.panelstudio.CollapsibleContainer;
import com.lukflug.panelstudio.theme.Renderer;
import com.lukflug.panelstudio.DraggableContainer;
import com.lukflug.panelstudio.settings.SimpleToggleable;
import com.gamesense.client.module.Category;
import com.lukflug.panelstudio.Animation;
import com.lukflug.panelstudio.hud.HUDPanel;
import com.lukflug.panelstudio.SettingsAnimation;
import com.gamesense.client.module.HUDModule;
import com.gamesense.client.module.Module;
import java.util.Iterator;
import com.lukflug.panelstudio.FixedComponent;
import com.lukflug.panelstudio.theme.DescriptionRenderer;
import com.lukflug.panelstudio.Interface;
import com.gamesense.api.util.font.FontUtil;
import com.gamesense.api.util.render.GSColor;
import com.lukflug.panelstudio.mc12.GLInterface;
import java.awt.Color;
import java.awt.Point;
import com.lukflug.panelstudio.theme.ThemeMultiplexer;
import com.lukflug.panelstudio.theme.ClearTheme;
import com.lukflug.panelstudio.theme.GameSenseTheme;
import com.lukflug.panelstudio.settings.NumberSetting;
import com.lukflug.panelstudio.settings.ColorSetting;
import com.lukflug.panelstudio.theme.SettingsColorScheme;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ClickGuiModule;
import com.lukflug.panelstudio.theme.Theme;
import com.lukflug.panelstudio.hud.HUDClickGUI;
import com.lukflug.panelstudio.mc12.MinecraftGUI;
import com.lukflug.panelstudio.settings.Toggleable;
import com.lukflug.panelstudio.mc12.MinecraftHUDGUI;

public class GameSenseGUI extends MinecraftHUDGUI
{
    public static final int WIDTH = 100;
    public static final int HEIGHT = 12;
    public static final int DISTANCE = 10;
    public static final int HUD_BORDER = 2;
    private final Toggleable colorToggle;
    public final GUIInterface guiInterface;
    public final HUDClickGUI gui;
    private final Theme theme;
    private final Theme gameSenseTheme;
    private final Theme clearTheme;
    private final Theme clearGradientTheme;
    
    public GameSenseGUI() {
        final ClickGuiModule clickGuiModule = ModuleManager.getModule(ClickGuiModule.class);
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        final ColorScheme scheme = new SettingsColorScheme(clickGuiModule.enabledColor, clickGuiModule.backgroundColor, clickGuiModule.settingBackgroundColor, clickGuiModule.outlineColor, clickGuiModule.fontColor, clickGuiModule.opacity);
        this.gameSenseTheme = new GameSenseTheme(scheme, 12, 2, 5);
        this.clearTheme = new ClearTheme(scheme, false, 12, 1);
        this.clearGradientTheme = new ClearTheme(scheme, true, 12, 1);
        this.theme = new ThemeMultiplexer() {
            @Override
            protected Theme getTheme() {
                if (clickGuiModule.theme.getValue().equals("2.0")) {
                    return GameSenseGUI.this.clearTheme;
                }
                if (clickGuiModule.theme.getValue().equals("2.1.2")) {
                    return GameSenseGUI.this.clearGradientTheme;
                }
                return GameSenseGUI.this.gameSenseTheme;
            }
        };
        this.colorToggle = new Toggleable() {
            @Override
            public void toggle() {
                colorMain.colorModel.increment();
            }
            
            @Override
            public boolean isOn() {
                return colorMain.colorModel.getValue().equals("HSB");
            }
        };
        this.guiInterface = new GUIInterface(true) {
            @Override
            public void drawString(final Point pos, final String s, final Color c) {
                GLInterface.end();
                int x = pos.x + 2;
                int y = pos.y + 1;
                if (!colorMain.customFont.getValue()) {
                    ++x;
                    ++y;
                }
                FontUtil.drawStringWithShadow(colorMain.customFont.getValue(), s, x, y, new GSColor(c));
                GLInterface.begin();
            }
            
            @Override
            public int getFontWidth(final String s) {
                return Math.round((float)FontUtil.getStringWidth(colorMain.customFont.getValue(), s)) + 4;
            }
            
            @Override
            public int getFontHeight() {
                return Math.round((float)FontUtil.getFontHeight(colorMain.customFont.getValue())) + 2;
            }
            
            public String getResourcePrefix() {
                return "gamesense:gui/";
            }
        };
        this.gui = new HUDClickGUI(this.guiInterface, null) {
            @Override
            public void handleScroll(final int diff) {
                super.handleScroll(diff);
                if (clickGuiModule.scrolling.getValue().equals("Screen")) {
                    for (final FixedComponent component : this.components) {
                        if (!this.hudComponents.contains(component)) {
                            final Point p = component.getPosition(GameSenseGUI.this.guiInterface);
                            p.translate(0, -diff);
                            component.setPosition(GameSenseGUI.this.guiInterface, p);
                        }
                    }
                }
            }
        };
        final Toggleable hudToggle = new Toggleable() {
            @Override
            public void toggle() {
            }
            
            @Override
            public boolean isOn() {
                return (GameSenseGUI.this.gui.isOn() && clickGuiModule.showHUD.isOn()) || GameSenseGUI.this.hudEditor;
            }
        };
        for (final Module module : ModuleManager.getModules()) {
            if (module instanceof HUDModule) {
                ((HUDModule)module).populate(this.theme);
                this.gui.addHUDComponent(new HUDPanel(((HUDModule)module).getComponent(), this.theme.getPanelRenderer(), module, new SettingsAnimation(clickGuiModule.animationSpeed), hudToggle, 2));
            }
        }
        final Point pos = new Point(10, 10);
        for (final Category category : Category.values()) {
            final DraggableContainer panel = new DraggableContainer(category.name(), null, this.theme.getPanelRenderer(), new SimpleToggleable(false), new SettingsAnimation(clickGuiModule.animationSpeed), null, new Point(pos), 100) {
                @Override
                protected int getScrollHeight(final int childHeight) {
                    if (clickGuiModule.scrolling.getValue().equals("Screen")) {
                        return childHeight;
                    }
                    return Math.min(childHeight, Math.max(48, GameSenseGUI.this.field_146295_m - this.getPosition(GameSenseGUI.this.guiInterface).y - this.renderer.getHeight(this.open.getValue() != 0.0) - 12));
                }
            };
            this.gui.addComponent(panel);
            pos.translate(110, 0);
            for (final Module module2 : ModuleManager.getModulesInCategory(category)) {
                this.addModule(panel, module2);
            }
        }
    }
    
    private void addModule(final CollapsibleContainer panel, final Module module) {
        final ClickGuiModule clickGuiModule = ModuleManager.getModule(ClickGuiModule.class);
        final CollapsibleContainer container = new CollapsibleContainer(module.getName(), null, this.theme.getContainerRenderer(), new SimpleToggleable(false), new SettingsAnimation(clickGuiModule.animationSpeed), module);
        panel.addComponent(container);
        for (final Setting property : SettingsManager.getSettingsForModule(module)) {
            if (property instanceof BooleanSetting) {
                container.addComponent(new BooleanComponent(property.getName(), null, this.theme.getComponentRenderer(), (Toggleable)property));
            }
            else if (property instanceof IntegerSetting) {
                container.addComponent(new NumberComponent(property.getName(), null, this.theme.getComponentRenderer(), (NumberSetting)property, ((IntegerSetting)property).getMin(), ((IntegerSetting)property).getMax()));
            }
            else if (property instanceof DoubleSetting) {
                container.addComponent(new NumberComponent(property.getName(), null, this.theme.getComponentRenderer(), (NumberSetting)property, ((DoubleSetting)property).getMin(), ((DoubleSetting)property).getMax()));
            }
            else if (property instanceof ModeSetting) {
                container.addComponent(new EnumComponent(property.getName(), null, this.theme.getComponentRenderer(), (EnumSetting)property));
            }
            else {
                if (!(property instanceof com.gamesense.api.setting.values.ColorSetting)) {
                    continue;
                }
                container.addComponent(new GameSenseColor(this.theme, (com.gamesense.api.setting.values.ColorSetting)property, this.colorToggle, new SettingsAnimation(clickGuiModule.animationSpeed)));
            }
        }
        container.addComponent(new GameSenseToggleMessage(this.theme.getComponentRenderer(), module));
        container.addComponent(new GameSenseKeybind(this.theme.getComponentRenderer(), module));
    }
    
    public static void renderItem(final ItemStack item, final Point pos) {
        GlStateManager.func_179098_w();
        GlStateManager.func_179132_a(true);
        GL11.glPushAttrib(524288);
        GL11.glDisable(3089);
        GlStateManager.func_179086_m(256);
        GL11.glPopAttrib();
        GlStateManager.func_179126_j();
        GlStateManager.func_179118_c();
        GlStateManager.func_179094_E();
        Minecraft.func_71410_x().func_175599_af().field_77023_b = -150.0f;
        RenderHelper.func_74520_c();
        Minecraft.func_71410_x().func_175599_af().func_180450_b(item, pos.x, pos.y);
        Minecraft.func_71410_x().func_175599_af().func_175030_a(Minecraft.func_71410_x().field_71466_p, item, pos.x, pos.y);
        RenderHelper.func_74518_a();
        Minecraft.func_71410_x().func_175599_af().field_77023_b = 0.0f;
        GlStateManager.func_179121_F();
        GlStateManager.func_179097_i();
        GlStateManager.func_179132_a(false);
        GLInterface.begin();
    }
    
    public static void renderEntity(final EntityLivingBase entity, final Point pos, final int scale) {
        GlStateManager.func_179098_w();
        GlStateManager.func_179132_a(true);
        GL11.glPushAttrib(524288);
        GL11.glDisable(3089);
        GlStateManager.func_179086_m(256);
        GL11.glPopAttrib();
        GlStateManager.func_179126_j();
        GlStateManager.func_179118_c();
        GlStateManager.func_179094_E();
        GlStateManager.func_179131_c(1.0f, 1.0f, 1.0f, 1.0f);
        GuiInventory.func_147046_a(pos.x, pos.y, scale, 28.0f, 60.0f, entity);
        GlStateManager.func_179121_F();
        GlStateManager.func_179097_i();
        GlStateManager.func_179132_a(false);
        GLInterface.begin();
    }
    
    @Override
    protected HUDClickGUI getHUDGUI() {
        return this.gui;
    }
    
    @Override
    protected GUIInterface getInterface() {
        return this.guiInterface;
    }
    
    @Override
    protected int getScrollSpeed() {
        return ModuleManager.getModule(ClickGuiModule.class).scrollSpeed.getValue();
    }
}
