// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.gamesense.api.util.font.FontUtil;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.client.gui.FontRenderer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ FontRenderer.class })
public class MixinFontRenderer
{
    @Redirect(method = { "drawStringWithShadow" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawString(Ljava/lang/String;FFIZ)I"))
    public int drawCustomFontStringWithShadow(final FontRenderer fontRenderer, final String text, final float x, final float y, final int color, final boolean dropShadow) {
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        return colorMain.textFont.getValue() ? ((int)FontUtil.drawStringWithShadow(true, text, (int)x, (int)y, new GSColor(color))) : fontRenderer.func_175065_a(text, x, y, color, true);
    }
}
