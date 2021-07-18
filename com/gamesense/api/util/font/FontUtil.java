// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.font;

import com.gamesense.client.GameSense;
import com.gamesense.api.util.render.GSColor;
import net.minecraft.client.Minecraft;

public class FontUtil
{
    private static final Minecraft mc;
    
    public static float drawStringWithShadow(final boolean customFont, final String text, final int x, final int y, final GSColor color) {
        if (customFont) {
            return GameSense.INSTANCE.cFontRenderer.drawStringWithShadow(text, x, y, color);
        }
        return (float)FontUtil.mc.field_71466_p.func_175063_a(text, (float)x, (float)y, color.getRGB());
    }
    
    public static int getStringWidth(final boolean customFont, final String string) {
        if (customFont) {
            return GameSense.INSTANCE.cFontRenderer.getStringWidth(string);
        }
        return FontUtil.mc.field_71466_p.func_78256_a(string);
    }
    
    public static int getFontHeight(final boolean customFont) {
        if (customFont) {
            return GameSense.INSTANCE.cFontRenderer.getHeight();
        }
        return FontUtil.mc.field_71466_p.field_78288_b;
    }
    
    static {
        mc = Minecraft.func_71410_x();
    }
}
