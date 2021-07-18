// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.hud;

import com.lukflug.panelstudio.Interface;
import net.minecraft.util.NonNullList;
import java.awt.Color;
import com.gamesense.client.clickgui.GameSenseGUI;
import net.minecraft.item.ItemStack;
import net.minecraft.client.Minecraft;
import java.awt.Rectangle;
import java.awt.Dimension;
import com.lukflug.panelstudio.Context;
import com.lukflug.panelstudio.hud.HUDComponent;
import java.awt.Point;
import com.lukflug.panelstudio.theme.Theme;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.HUDModule;

@Module.Declaration(name = "InventoryViewer", category = Category.HUD)
@Declaration(posX = 0, posZ = 10)
public class InventoryViewer extends HUDModule
{
    ColorSetting fillColor;
    ColorSetting outlineColor;
    
    public InventoryViewer() {
        this.fillColor = this.registerColor("Fill", new GSColor(0, 0, 0, 100));
        this.outlineColor = this.registerColor("Outline", new GSColor(255, 0, 0, 255));
    }
    
    @Override
    public void populate(final Theme theme) {
        this.component = new InventoryViewerComponent(theme);
    }
    
    private class InventoryViewerComponent extends HUDComponent
    {
        public InventoryViewerComponent(final Theme theme) {
            super(InventoryViewer.this.getName(), theme.getPanelRenderer(), InventoryViewer.this.position);
        }
        
        @Override
        public void render(final Context context) {
            super.render(context);
            final Color bgcolor = new GSColor(InventoryViewer.this.fillColor.getValue(), 100);
            context.getInterface().fillRect(context.getRect(), bgcolor, bgcolor, bgcolor, bgcolor);
            final Color color = InventoryViewer.this.outlineColor.getValue();
            context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(context.getSize().width, 1)), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(context.getPos(), new Dimension(1, context.getSize().height)), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x + context.getSize().width - 1, context.getPos().y), new Dimension(1, context.getSize().height)), color, color, color, color);
            context.getInterface().fillRect(new Rectangle(new Point(context.getPos().x, context.getPos().y + context.getSize().height - 1), new Dimension(context.getSize().width, 1)), color, color, color, color);
            final NonNullList<ItemStack> items = (NonNullList<ItemStack>)Minecraft.func_71410_x().field_71439_g.field_71071_by.field_70462_a;
            for (int size = items.size(), item = 9; item < size; ++item) {
                final int slotX = context.getPos().x + item % 9 * 18;
                final int slotY = context.getPos().y + 2 + (item / 9 - 1) * 18;
                GameSenseGUI.renderItem((ItemStack)items.get(item), new Point(slotX, slotY));
            }
        }
        
        @Override
        public int getWidth(final Interface inter) {
            return 162;
        }
        
        @Override
        public void getHeight(final Context context) {
            context.setHeight(56);
        }
    }
}
