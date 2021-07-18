// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.render;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;

public class ChamsUtil
{
    private static final Minecraft mc;
    
    public static void createChamsPre() {
        ChamsUtil.mc.func_175598_ae().func_178633_a(false);
        ChamsUtil.mc.func_175598_ae().func_178632_c(false);
        GlStateManager.func_179094_E();
        GlStateManager.func_179132_a(true);
        OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0f, 240.0f);
        GL11.glEnable(32823);
        GL11.glDepthRange(0.0, 0.01);
        GlStateManager.func_179121_F();
    }
    
    public static void createChamsPost() {
        final boolean shadow = ChamsUtil.mc.func_175598_ae().func_178627_a();
        ChamsUtil.mc.func_175598_ae().func_178633_a(shadow);
        GlStateManager.func_179094_E();
        GlStateManager.func_179132_a(false);
        GL11.glDisable(32823);
        GL11.glDepthRange(0.0, 1.0);
        GlStateManager.func_179121_F();
    }
    
    public static void createColorPre(final GSColor color, final boolean isPlayer) {
        ChamsUtil.mc.func_175598_ae().func_178633_a(false);
        ChamsUtil.mc.func_175598_ae().func_178632_c(false);
        GlStateManager.func_179094_E();
        GlStateManager.func_179132_a(true);
        OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0f, 240.0f);
        GL11.glEnable(32823);
        GL11.glDepthRange(0.0, 0.01);
        GL11.glDisable(3553);
        if (!isPlayer) {
            GlStateManager.func_187408_a(GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        color.glColor();
        GlStateManager.func_179121_F();
    }
    
    public static void createColorPost(final boolean isPlayer) {
        final boolean shadow = ChamsUtil.mc.func_175598_ae().func_178627_a();
        ChamsUtil.mc.func_175598_ae().func_178633_a(shadow);
        GlStateManager.func_179094_E();
        GlStateManager.func_179132_a(false);
        if (!isPlayer) {
            GlStateManager.func_187440_b(GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        GL11.glDisable(32823);
        GL11.glDepthRange(0.0, 1.0);
        GL11.glEnable(3553);
        GlStateManager.func_179121_F();
    }
    
    public static void createWirePre(final GSColor color, final int lineWidth, final boolean isPlayer) {
        ChamsUtil.mc.func_175598_ae().func_178633_a(false);
        ChamsUtil.mc.func_175598_ae().func_178632_c(false);
        GlStateManager.func_179094_E();
        GlStateManager.func_179132_a(true);
        OpenGlHelper.func_77475_a(OpenGlHelper.field_77476_b, 240.0f, 240.0f);
        GL11.glPolygonMode(1032, 6913);
        GL11.glEnable(10754);
        GL11.glDepthRange(0.0, 0.01);
        GL11.glDisable(3553);
        GL11.glDisable(2896);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        if (!isPlayer) {
            GlStateManager.func_187408_a(GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        GL11.glLineWidth((float)lineWidth);
        color.glColor();
        GlStateManager.func_179121_F();
    }
    
    public static void createWirePost(final boolean isPlayer) {
        final boolean shadow = ChamsUtil.mc.func_175598_ae().func_178627_a();
        ChamsUtil.mc.func_175598_ae().func_178633_a(shadow);
        GlStateManager.func_179094_E();
        GlStateManager.func_179132_a(false);
        if (!isPlayer) {
            GlStateManager.func_187440_b(GlStateManager.Profile.TRANSPARENT_MODEL);
        }
        GL11.glPolygonMode(1032, 6914);
        GL11.glDisable(10754);
        GL11.glDepthRange(0.0, 1.0);
        GL11.glEnable(3553);
        GL11.glEnable(2896);
        GL11.glDisable(2848);
        GlStateManager.func_179121_F();
    }
    
    static {
        mc = Minecraft.func_71410_x();
    }
}
