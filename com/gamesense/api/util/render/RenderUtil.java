// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.render;

import org.lwjgl.opengl.GL11;
import com.gamesense.api.util.font.FontUtil;
import com.gamesense.client.module.modules.render.Nametags;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.util.math.Vec3d;
import com.gamesense.api.util.world.EntityUtil;
import net.minecraft.entity.Entity;
import org.lwjgl.util.glu.Sphere;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.Minecraft;

public class RenderUtil
{
    private static final Minecraft mc;
    
    public static void drawLine(final double posx, final double posy, final double posz, final double posx2, final double posy2, final double posz2, final GSColor color) {
        drawLine(posx, posy, posz, posx2, posy2, posz2, color, 1.0f);
    }
    
    public static void drawLine(final double posx, final double posy, final double posz, final double posx2, final double posy2, final double posz2, final GSColor color, final float width) {
        final Tessellator tessellator = Tessellator.func_178181_a();
        final BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_187441_d(width);
        color.glColor();
        bufferbuilder.func_181668_a(1, DefaultVertexFormats.field_181705_e);
        vertex(posx, posy, posz, bufferbuilder);
        vertex(posx2, posy2, posz2, bufferbuilder);
        tessellator.func_78381_a();
    }
    
    public static void draw2DRect(final int posX, final int posY, final int width, final int height, final int zHeight, final GSColor color) {
        final Tessellator tessellator = Tessellator.func_178181_a();
        final BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_179147_l();
        GlStateManager.func_179090_x();
        GlStateManager.func_187428_a(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        color.glColor();
        bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        bufferbuilder.func_181662_b((double)posX, (double)(posY + height), (double)zHeight).func_181675_d();
        bufferbuilder.func_181662_b((double)(posX + width), (double)(posY + height), (double)zHeight).func_181675_d();
        bufferbuilder.func_181662_b((double)(posX + width), (double)posY, (double)zHeight).func_181675_d();
        bufferbuilder.func_181662_b((double)posX, (double)posY, (double)zHeight).func_181675_d();
        tessellator.func_78381_a();
        GlStateManager.func_179098_w();
        GlStateManager.func_179084_k();
    }
    
    private static void drawBorderedRect(final double x, final double y, final double x1, final double y1, final float lineWidth, final GSColor inside, final GSColor border) {
        final Tessellator tessellator = Tessellator.func_178181_a();
        final BufferBuilder bufferbuilder = tessellator.func_178180_c();
        inside.glColor();
        bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181705_e);
        bufferbuilder.func_181662_b(x, y1, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(x1, y1, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(x1, y, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(x, y, 0.0).func_181675_d();
        tessellator.func_78381_a();
        border.glColor();
        GlStateManager.func_187441_d(lineWidth);
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181705_e);
        bufferbuilder.func_181662_b(x, y, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(x, y1, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(x1, y1, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(x1, y, 0.0).func_181675_d();
        bufferbuilder.func_181662_b(x, y, 0.0).func_181675_d();
        tessellator.func_78381_a();
    }
    
    public static void drawBox(final BlockPos blockPos, final double height, final GSColor color, final int sides) {
        drawBox(blockPos.func_177958_n(), blockPos.func_177956_o(), blockPos.func_177952_p(), 1.0, height, 1.0, color, color.getAlpha(), sides);
    }
    
    public static void drawBox(final AxisAlignedBB bb, final boolean check, final double height, final GSColor color, final int sides) {
        drawBox(bb, check, height, color, color.getAlpha(), sides);
    }
    
    public static void drawBox(final AxisAlignedBB bb, final boolean check, final double height, final GSColor color, final int alpha, final int sides) {
        if (check) {
            drawBox(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, bb.field_72336_d - bb.field_72340_a, bb.field_72337_e - bb.field_72338_b, bb.field_72334_f - bb.field_72339_c, color, alpha, sides);
        }
        else {
            drawBox(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, bb.field_72336_d - bb.field_72340_a, height, bb.field_72334_f - bb.field_72339_c, color, alpha, sides);
        }
    }
    
    public static void drawBox(final double x, final double y, final double z, final double w, final double h, final double d, final GSColor color, final int alpha, final int sides) {
        GlStateManager.func_179118_c();
        final Tessellator tessellator = Tessellator.func_178181_a();
        final BufferBuilder bufferbuilder = tessellator.func_178180_c();
        color.glColor();
        bufferbuilder.func_181668_a(7, DefaultVertexFormats.field_181706_f);
        doVerticies(new AxisAlignedBB(x, y, z, x + w, y + h, z + d), color, alpha, bufferbuilder, sides, false);
        tessellator.func_78381_a();
        GlStateManager.func_179141_d();
    }
    
    public static void drawBoundingBox(final BlockPos bp, final double height, final float width, final GSColor color) {
        drawBoundingBox(getBoundingBox(bp, 1.0, height, 1.0), width, color, color.getAlpha());
    }
    
    public static void drawBoundingBox(final AxisAlignedBB bb, final double width, final GSColor color) {
        drawBoundingBox(bb, width, color, color.getAlpha());
    }
    
    public static void drawBoundingBox(final AxisAlignedBB bb, final double width, final GSColor color, final int alpha) {
        final Tessellator tessellator = Tessellator.func_178181_a();
        final BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_187441_d((float)width);
        color.glColor();
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
        colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
        colorVertex(bb.field_72340_a, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72334_f, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
        colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
        colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72334_f, color, alpha, bufferbuilder);
        colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
        colorVertex(bb.field_72336_d, bb.field_72338_b, bb.field_72339_c, color, color.getAlpha(), bufferbuilder);
        colorVertex(bb.field_72336_d, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
        colorVertex(bb.field_72340_a, bb.field_72337_e, bb.field_72339_c, color, alpha, bufferbuilder);
        tessellator.func_78381_a();
    }
    
    public static void drawBoundingBoxWithSides(final BlockPos blockPos, final int width, final GSColor color, final int sides) {
        drawBoundingBoxWithSides(getBoundingBox(blockPos, 1.0, 1.0, 1.0), width, color, color.getAlpha(), sides);
    }
    
    public static void drawBoundingBoxWithSides(final BlockPos blockPos, final int width, final GSColor color, final int alpha, final int sides) {
        drawBoundingBoxWithSides(getBoundingBox(blockPos, 1.0, 1.0, 1.0), width, color, alpha, sides);
    }
    
    public static void drawBoundingBoxWithSides(final AxisAlignedBB axisAlignedBB, final int width, final GSColor color, final int sides) {
        drawBoundingBoxWithSides(axisAlignedBB, width, color, color.getAlpha(), sides);
    }
    
    public static void drawBoundingBoxWithSides(final AxisAlignedBB axisAlignedBB, final int width, final GSColor color, final int alpha, final int sides) {
        final Tessellator tessellator = Tessellator.func_178181_a();
        final BufferBuilder bufferbuilder = tessellator.func_178180_c();
        GlStateManager.func_187441_d((float)width);
        bufferbuilder.func_181668_a(3, DefaultVertexFormats.field_181706_f);
        doVerticies(axisAlignedBB, color, alpha, bufferbuilder, sides, true);
        tessellator.func_78381_a();
    }
    
    public static void drawBoxWithDirection(final AxisAlignedBB bb, final GSColor color, final float rotation, final float width, final int mode) {
        final double xCenter = bb.field_72340_a + (bb.field_72336_d - bb.field_72340_a) / 2.0;
        final double zCenter = bb.field_72339_c + (bb.field_72334_f - bb.field_72339_c) / 2.0;
        final Points square = new Points(bb.field_72338_b, bb.field_72337_e, xCenter, zCenter, rotation);
        if (mode == 0) {
            square.addPoints(bb.field_72340_a, bb.field_72339_c);
            square.addPoints(bb.field_72340_a, bb.field_72334_f);
            square.addPoints(bb.field_72336_d, bb.field_72334_f);
            square.addPoints(bb.field_72336_d, bb.field_72339_c);
        }
        switch (mode) {
            case 0: {
                drawDirection(square, color, width);
                break;
            }
        }
    }
    
    public static void drawDirection(final Points square, final GSColor color, final float width) {
        for (int i = 0; i < 4; ++i) {
            drawLine(square.getPoint(i)[0], square.yMin, square.getPoint(i)[1], square.getPoint((i + 1) % 4)[0], square.yMin, square.getPoint((i + 1) % 4)[1], color, width);
        }
        for (int i = 0; i < 4; ++i) {
            drawLine(square.getPoint(i)[0], square.yMax, square.getPoint(i)[1], square.getPoint((i + 1) % 4)[0], square.yMax, square.getPoint((i + 1) % 4)[1], color, width);
        }
        for (int i = 0; i < 4; ++i) {
            drawLine(square.getPoint(i)[0], square.yMin, square.getPoint(i)[1], square.getPoint(i)[0], square.yMax, square.getPoint(i)[1], color, width);
        }
    }
    
    public static void drawSphere(final double x, final double y, final double z, final float size, final int slices, final int stacks, final float lineWidth, final GSColor color) {
        final Sphere sphere = new Sphere();
        GlStateManager.func_187441_d(lineWidth);
        color.glColor();
        sphere.setDrawStyle(100013);
        GlStateManager.func_179094_E();
        GlStateManager.func_179137_b(x - RenderUtil.mc.func_175598_ae().field_78730_l, y - RenderUtil.mc.func_175598_ae().field_78731_m, z - RenderUtil.mc.func_175598_ae().field_78728_n);
        sphere.draw(size, slices, stacks);
        GlStateManager.func_179121_F();
    }
    
    public static void drawNametag(final Entity entity, final String[] text, final GSColor color, final int type) {
        final Vec3d pos = EntityUtil.getInterpolatedPos(entity, RenderUtil.mc.func_184121_ak());
        drawNametag(pos.field_72450_a, pos.field_72448_b + entity.field_70131_O, pos.field_72449_c, text, color, type);
    }
    
    public static void drawNametag(final double x, final double y, final double z, final String[] text, final GSColor color, final int type) {
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        final double dist = RenderUtil.mc.field_71439_g.func_70011_f(x, y, z);
        double scale = 1.0;
        double offset = 0.0;
        int start = 0;
        switch (type) {
            case 0: {
                scale = dist / 20.0 * Math.pow(1.2589254, 0.1 / ((dist < 25.0) ? 0.5 : 2.0));
                scale = Math.min(Math.max(scale, 0.5), 5.0);
                offset = ((scale > 2.0) ? (scale / 2.0) : scale);
                scale /= 40.0;
                start = 10;
                break;
            }
            case 1: {
                scale = -(int)dist / 6.0;
                if (scale < 1.0) {
                    scale = 1.0;
                }
                scale *= 0.02666666666666667;
                break;
            }
            case 2: {
                scale = 0.0018 + 0.003 * dist;
                if (dist <= 8.0) {
                    scale = 0.0245;
                }
                start = -8;
                break;
            }
        }
        GlStateManager.func_179094_E();
        GlStateManager.func_179137_b(x - RenderUtil.mc.func_175598_ae().field_78730_l, y + offset - RenderUtil.mc.func_175598_ae().field_78731_m, z - RenderUtil.mc.func_175598_ae().field_78728_n);
        GlStateManager.func_179114_b(-RenderUtil.mc.func_175598_ae().field_78735_i, 0.0f, 1.0f, 0.0f);
        GlStateManager.func_179114_b(RenderUtil.mc.func_175598_ae().field_78732_j, (RenderUtil.mc.field_71474_y.field_74320_O == 2) ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.func_179139_a(-scale, -scale, scale);
        if (type == 2) {
            double width = 0.0;
            GSColor bcolor = new GSColor(0, 0, 0, 51);
            final Nametags nametags = ModuleManager.getModule(Nametags.class);
            if (nametags.customColor.getValue()) {
                bcolor = nametags.borderColor.getValue();
            }
            for (int i = 0; i < text.length; ++i) {
                final double w = FontUtil.getStringWidth(colorMain.customFont.getValue(), text[i]) / 2;
                if (w > width) {
                    width = w;
                }
            }
            drawBorderedRect(-width - 1.0, -RenderUtil.mc.field_71466_p.field_78288_b, width + 2.0, 1.0, 1.8f, new GSColor(0, 4, 0, 85), bcolor);
        }
        GlStateManager.func_179098_w();
        for (int j = 0; j < text.length; ++j) {
            FontUtil.drawStringWithShadow(colorMain.customFont.getValue(), text[j], -FontUtil.getStringWidth(colorMain.customFont.getValue(), text[j]) / 2, j * (RenderUtil.mc.field_71466_p.field_78288_b + 1) + start, color);
        }
        GlStateManager.func_179090_x();
        if (type != 2) {
            GlStateManager.func_179121_F();
        }
    }
    
    private static void vertex(final double x, final double y, final double z, final BufferBuilder bufferbuilder) {
        bufferbuilder.func_181662_b(x - RenderUtil.mc.func_175598_ae().field_78730_l, y - RenderUtil.mc.func_175598_ae().field_78731_m, z - RenderUtil.mc.func_175598_ae().field_78728_n).func_181675_d();
    }
    
    private static void colorVertex(final double x, final double y, final double z, final GSColor color, final int alpha, final BufferBuilder bufferbuilder) {
        bufferbuilder.func_181662_b(x - RenderUtil.mc.func_175598_ae().field_78730_l, y - RenderUtil.mc.func_175598_ae().field_78731_m, z - RenderUtil.mc.func_175598_ae().field_78728_n).func_181669_b(color.getRed(), color.getGreen(), color.getBlue(), alpha).func_181675_d();
    }
    
    private static AxisAlignedBB getBoundingBox(final BlockPos bp, final double width, final double height, final double depth) {
        final double x = bp.func_177958_n();
        final double y = bp.func_177956_o();
        final double z = bp.func_177952_p();
        return new AxisAlignedBB(x, y, z, x + width, y + height, z + depth);
    }
    
    private static void doVerticies(final AxisAlignedBB axisAlignedBB, final GSColor color, final int alpha, final BufferBuilder bufferbuilder, final int sides, final boolean five) {
        if ((sides & 0x20) != 0x0) {
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x10) != 0x0) {
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x4) != 0x0) {
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x8) != 0x0) {
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            }
        }
        if ((sides & 0x2) != 0x0) {
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72334_f, color, alpha, bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72337_e, axisAlignedBB.field_72339_c, color, alpha, bufferbuilder);
            }
        }
        if ((sides & 0x1) != 0x0) {
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72334_f, color, color.getAlpha(), bufferbuilder);
            colorVertex(axisAlignedBB.field_72340_a, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            if (five) {
                colorVertex(axisAlignedBB.field_72336_d, axisAlignedBB.field_72338_b, axisAlignedBB.field_72339_c, color, color.getAlpha(), bufferbuilder);
            }
        }
    }
    
    public static void prepare() {
        GL11.glHint(3154, 4354);
        GlStateManager.func_179120_a(770, 771, 0, 1);
        GlStateManager.func_179103_j(7425);
        GlStateManager.func_179132_a(false);
        GlStateManager.func_179147_l();
        GlStateManager.func_179097_i();
        GlStateManager.func_179090_x();
        GlStateManager.func_179140_f();
        GlStateManager.func_179129_p();
        GlStateManager.func_179141_d();
        GL11.glEnable(2848);
        GL11.glEnable(34383);
    }
    
    public static void release() {
        GL11.glDisable(34383);
        GL11.glDisable(2848);
        GlStateManager.func_179141_d();
        GlStateManager.func_179089_o();
        GlStateManager.func_179098_w();
        GlStateManager.func_179126_j();
        GlStateManager.func_179084_k();
        GlStateManager.func_179132_a(true);
        GlStateManager.func_187441_d(1.0f);
        GlStateManager.func_179103_j(7424);
        GL11.glHint(3154, 4352);
    }
    
    static {
        mc = Minecraft.func_71410_x();
    }
    
    private static class Points
    {
        double[][] point;
        private int count;
        private final double xCenter;
        private final double zCenter;
        public final double yMin;
        public final double yMax;
        private final float rotation;
        
        public Points(final double yMin, final double yMax, final double xCenter, final double zCenter, final float rotation) {
            this.point = new double[10][2];
            this.count = 0;
            this.yMin = yMin;
            this.yMax = yMax;
            this.xCenter = xCenter;
            this.zCenter = zCenter;
            this.rotation = rotation;
        }
        
        public void addPoints(double x, double z) {
            x -= this.xCenter;
            z -= this.zCenter;
            double rotateX = x * Math.cos(this.rotation) - z * Math.sin(this.rotation);
            double rotateZ = x * Math.sin(this.rotation) + z * Math.cos(this.rotation);
            rotateX += this.xCenter;
            rotateZ += this.zCenter;
            this.point[this.count++] = new double[] { rotateX, rotateZ };
        }
        
        public double[] getPoint(final int index) {
            return this.point[index];
        }
    }
}
