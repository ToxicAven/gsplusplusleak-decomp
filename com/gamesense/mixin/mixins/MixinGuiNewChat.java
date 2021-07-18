// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.gamesense.api.setting.values.ColorSetting;
import java.util.Iterator;
import java.util.Collection;
import java.util.Arrays;
import com.gamesense.api.util.player.social.SocialManager;
import java.util.ArrayList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import net.minecraft.client.gui.Gui;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.ChatModifier;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ GuiNewChat.class })
public abstract class MixinGuiNewChat
{
    ChatModifier chatModifier;
    String[] specialWords;
    
    public MixinGuiNewChat() {
        this.chatModifier = ModuleManager.getModule(ChatModifier.class);
        this.specialWords = null;
    }
    
    @Redirect(method = { "drawChat" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    private void drawRectBackgroundClean(final int left, final int top, final int right, final int bottom, final int color) {
        if (this.chatModifier.isEnabled()) {
            if (left != 0 && top != 0) {
                Gui.func_73734_a(left, top, right, bottom, new GSColor(this.chatModifier.backColor.getValue(), this.chatModifier.alphaColor.getValue()).getRGB());
            }
        }
        else {
            Gui.func_73734_a(left, top, right, bottom, color);
        }
    }
    
    @Redirect(method = { "drawChat" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V"))
    private void customSize(final float x, final float y, final float z) {
        if (this.chatModifier.isEnabled()) {
            GlStateManager.func_179139_a((double)this.chatModifier.xScale.getValue(), (double)this.chatModifier.yScale.getValue(), 1.0);
            GlStateManager.func_179109_b((this.chatModifier.leftPosition.getValue() != -1) ? ((float)this.chatModifier.leftPosition.getValue()) : x, (this.chatModifier.upPosition.getValue() != -100) ? ((float)(-this.chatModifier.upPosition.getValue())) : y, z);
        }
        else {
            GlStateManager.func_179109_b(x, y, z);
        }
    }
    
    @Redirect(method = { "drawChat" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int drawStringWithShadow(final FontRenderer fontRenderer, final String text, final float x, final float y, final int color) {
        if (this.chatModifier.isEnabled()) {
            this.displayText(text, x, y);
        }
        else {
            Minecraft.func_71410_x().field_71466_p.func_175063_a(text, x, y, color);
        }
        return 0;
    }
    
    private void displayText(final String word, final float x, final float y) {
        final StringBuilder outputstring = new StringBuilder();
        final ArrayList<ArrayList<String>> toWrite = new ArrayList<ArrayList<String>>();
        String nowColor = "";
        boolean before$ = false;
        if (this.specialWords == null) {
            this.specialWords = SocialManager.getSpecialNamesString().toArray(new String[0]);
        }
        for (final char c : word.toCharArray()) {
            if (c == '§') {
                before$ = true;
                if (outputstring.length() != 0) {
                    final String lowercase = outputstring.toString();
                    boolean found = false;
                    int i = 0;
                    while (i < this.specialWords.length) {
                        if (lowercase.toLowerCase().contains(this.specialWords[i])) {
                            found = true;
                            final StringBuilder newOutput = new StringBuilder();
                            for (final String part : lowercase.split(" ")) {
                                boolean foundSpecial = false;
                                for (int j = i; j < this.specialWords.length; ++j) {
                                    if (part.toLowerCase().contains(this.specialWords[j])) {
                                        foundSpecial = true;
                                        break;
                                    }
                                }
                                if (foundSpecial) {
                                    if (newOutput.length() != 0) {
                                        toWrite.add(new ArrayList<String>(Arrays.asList(nowColor, newOutput.toString())));
                                    }
                                    toWrite.add(new ArrayList<String>(Arrays.asList("\u200especial", part + " ")));
                                    newOutput.setLength(0);
                                }
                                else {
                                    newOutput.append(part).append(" ");
                                }
                            }
                            if (newOutput.length() != 0) {
                                toWrite.add(new ArrayList<String>(Arrays.asList(nowColor, newOutput.toString())));
                                break;
                            }
                            break;
                        }
                        else {
                            ++i;
                        }
                    }
                    if (!found) {
                        toWrite.add(new ArrayList<String>(Arrays.asList(nowColor, outputstring.toString())));
                    }
                    outputstring.setLength(0);
                    nowColor = "";
                }
            }
            else {
                if (c == '\u2064') {
                    nowColor = Integer.toString(-new GSColor(this.chatModifier.friendColor.getValue(), 255).getRGB());
                }
                else if (c == '\u2065') {
                    nowColor = Integer.toString(-new GSColor(this.chatModifier.enemyColor.getValue(), 255).getRGB());
                }
                else if (c == '\u2066') {
                    nowColor = Integer.toString(-new GSColor(this.chatModifier.playerColor.getValue(), 255).getRGB());
                }
                else if (c == '\u2067') {
                    nowColor = Integer.toString(-new GSColor(this.chatModifier.timeColor.getValue(), 255).getRGB());
                }
                if (before$) {
                    if (c == 'k' || c == 'l' || c == 'm' || c == 'n' || c == 'o') {
                        outputstring.append("§").append(c);
                    }
                    else if (c == 'r') {
                        nowColor = "";
                    }
                    else {
                        nowColor = Integer.toString(this.getIntFromat(c));
                    }
                    before$ = false;
                }
                else {
                    outputstring.append(c);
                }
            }
        }
        int width = 0;
        int rainbowColor = 0;
        final int rainbowDesyncSmooth = this.chatModifier.rainbowDesyncSmooth.getValue();
        final double heightSin = this.chatModifier.heightSin.getValue();
        final int multiplyHeight = this.chatModifier.multiplyHeight.getValue();
        final double millSin = this.chatModifier.millSin.getValue();
        for (final ArrayList<String> strings : toWrite) {
            final String s = strings.get(0);
            switch (s) {
                case "": {
                    if (this.chatModifier.desyncRainbowNormal.getValue()) {
                        final int[] temp = this.writeDesync(strings.get(1), width, x, y, rainbowColor, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin);
                        width = temp[0];
                        rainbowColor = temp[1];
                        continue;
                    }
                    width += this.writeCustom(strings.get(1), width, x, y, -new GSColor(this.chatModifier.normalColor.getValue(), 255).getRGB());
                    continue;
                }
                case "\u200especial": {
                    if (this.chatModifier.desyncRainbowSpecial.getValue()) {
                        final int[] temp = this.writeDesync(strings.get(1).replace("\u2063", ""), width, x, y, rainbowColor, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin);
                        width = temp[0];
                        rainbowColor = temp[1];
                        continue;
                    }
                    width += this.writeCustom(strings.get(1), width, x, y, -new GSColor(this.chatModifier.normalColor.getValue(), 255).getRGB());
                    continue;
                }
                default: {
                    width += this.writeCustom(strings.get(1), width, x, y, Integer.parseInt(strings.get(0)));
                    continue;
                }
            }
        }
    }
    
    private int getIntFromat(final char value) {
        switch (value) {
            case 'b': {
                return -new GSColor(this.chatModifier.aqua.getValue(), 255).getRGB();
            }
            case 'd': {
                return -new GSColor(this.chatModifier.purple.getValue(), 255).getRGB();
            }
            case '5': {
                return -new GSColor(this.chatModifier.dark_purple.getValue(), 255).getRGB();
            }
            case '9': {
                return -new GSColor(this.chatModifier.blue.getValue(), 255).getRGB();
            }
            case '7': {
                return -new GSColor(this.chatModifier.gray.getValue(), 255).getRGB();
            }
            case '3': {
                return -new GSColor(this.chatModifier.dark_aqua.getValue(), 255).getRGB();
            }
            case '1': {
                return -new GSColor(this.chatModifier.dark_blue.getValue(), 255).getRGB();
            }
            case 'e': {
                return -new GSColor(this.chatModifier.yellow.getValue(), 255).getRGB();
            }
            case 'c': {
                return -new GSColor(this.chatModifier.red.getValue(), 255).getRGB();
            }
            case 'a': {
                return -new GSColor(this.chatModifier.green.getValue(), 255).getRGB();
            }
            case '8': {
                return -new GSColor(this.chatModifier.dark_gray.getValue(), 255).getRGB();
            }
            case '6': {
                return -new GSColor(this.chatModifier.gold.getValue(), 255).getRGB();
            }
            case '4': {
                return -new GSColor(this.chatModifier.dark_red.getValue(), 255).getRGB();
            }
            case '2': {
                return -new GSColor(this.chatModifier.dark_green.getValue(), 255).getRGB();
            }
            case '0': {
                return -new GSColor(this.chatModifier.black.getValue(), 255).getRGB();
            }
            default: {
                return -new GSColor(this.chatModifier.white.getValue(), 255).getRGB();
            }
        }
    }
    
    private int writeCustom(final String text, final int width, final float x, final float y, final int color) {
        Minecraft.func_71410_x().field_71466_p.func_175063_a(text, x + width, y, -color);
        return Minecraft.func_71410_x().field_71466_p.func_78256_a(text);
    }
    
    private int[] writeDesync(final String text, int width, final float x, final float y, int rainbowColor, final int rainbowDesyncSmooth, final double heightSin, final int multiplyHeight, final double millSin) {
        boolean skip = false;
        for (final String character : text.split("")) {
            if (skip) {
                skip = false;
            }
            else if (character.equals("§")) {
                skip = true;
            }
            else {
                GSColor colorOut = null;
                final String lowerCase = this.chatModifier.rainbowType.getValue().toLowerCase();
                switch (lowerCase) {
                    case "sin": {
                        colorOut = this.getSinRainbow(rainbowColor, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin);
                        break;
                    }
                    case "tan": {
                        colorOut = this.getTanRainbow(rainbowColor, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin);
                        break;
                    }
                    case "secant": {
                        colorOut = this.getSecantRainbow(rainbowColor, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin);
                        break;
                    }
                    case "cosecant": {
                        colorOut = this.getCosecRainbow(rainbowColor, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin);
                        break;
                    }
                    case "cotangent": {
                        colorOut = this.getCoTanRainbow(rainbowColor, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin);
                        break;
                    }
                    default: {
                        colorOut = this.getRainbow(rainbowColor);
                        break;
                    }
                }
                Minecraft.func_71410_x().field_71466_p.func_175063_a(character, x + width, y, new GSColor(colorOut.getRGB()).getRGB());
                width += Minecraft.func_71410_x().field_71466_p.func_78256_a(character);
                ++rainbowColor;
            }
        }
        return new int[] { width, rainbowColor };
    }
    
    private GSColor getRainbow(final int incr) {
        final GSColor color = ColorSetting.getRainbowColor(incr, this.chatModifier.rainbowDesyncSmooth.getValue());
        return new GSColor(color.getRed(), color.getBlue(), color.getGreen(), 255);
    }
    
    private GSColor getSinRainbow(final int incr, final int rainbowDesyncSmooth, final double heightSin, final int multiplyHeight, final double millSin) {
        final GSColor color = ColorSetting.getRainbowSin(incr, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin);
        return new GSColor(color.getRed(), color.getBlue(), color.getGreen(), 255);
    }
    
    private GSColor getTanRainbow(final int incr, final int rainbowDesyncSmooth, final double heightSin, final int multiplyHeight, final double millSin) {
        final GSColor color = ColorSetting.getRainbowTan(incr, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin);
        return new GSColor(color.getRed(), color.getBlue(), color.getGreen(), 255);
    }
    
    private GSColor getCosecRainbow(final int incr, final int rainbowDesyncSmooth, final double heightSin, final int multiplyHeight, final double millSin) {
        final GSColor color = ColorSetting.getRainbowCosec(incr, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin);
        return new GSColor(color.getRed(), color.getBlue(), color.getGreen(), 255);
    }
    
    private GSColor getSecantRainbow(final int incr, final int rainbowDesyncSmooth, final double heightSin, final int multiplyHeight, final double millSin) {
        final GSColor color = ColorSetting.getRainbowSec(incr, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin);
        return new GSColor(color.getRed(), color.getBlue(), color.getGreen(), 255);
    }
    
    private GSColor getCoTanRainbow(final int incr, final int rainbowDesyncSmooth, final double heightSin, final int multiplyHeight, final double millSin) {
        final GSColor color = ColorSetting.getRainbowCoTan(incr, rainbowDesyncSmooth, heightSin, multiplyHeight, millSin);
        return new GSColor(color.getRed(), color.getBlue(), color.getGreen(), 255);
    }
    
    @Inject(method = { "getChatHeight" }, at = { @At("HEAD") }, cancellable = true)
    public void getChatHeight(final CallbackInfoReturnable<Integer> cir) {
        if (this.chatModifier.isEnabled() && this.chatModifier.maxH.getValue() != -1) {
            cir.setReturnValue(this.chatModifier.maxH.getValue());
        }
    }
    
    @Inject(method = { "getChatWidth" }, at = { @At("HEAD") }, cancellable = true)
    public void getChatWidth(final CallbackInfoReturnable<Integer> cir) {
        if (this.chatModifier.isEnabled() && this.chatModifier.maxW.getValue() != -1) {
            cir.setReturnValue(this.chatModifier.maxW.getValue());
        }
    }
}
