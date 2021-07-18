// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import com.gamesense.api.util.player.social.Enemy;
import java.util.Iterator;
import com.gamesense.api.util.player.social.Friend;
import com.gamesense.api.util.player.social.SocialManager;
import net.minecraft.util.text.ITextComponent;
import com.gamesense.client.command.CommandManager;
import net.minecraft.network.play.client.CPacketChatMessage;
import java.util.function.Predicate;
import net.minecraft.util.text.TextFormatting;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.util.text.TextComponentString;
import java.util.Arrays;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.event.events.PacketEvent;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "ChatModifier", category = Category.Misc)
public class ChatModifier extends Module
{
    public ColorSetting backColor;
    public ColorSetting normalColor;
    public ColorSetting specialColor;
    public BooleanSetting desyncRainbowNormal;
    public BooleanSetting desyncRainbowSpecial;
    public ModeSetting rainbowType;
    public IntegerSetting rainbowDesyncSmooth;
    public DoubleSetting heightSin;
    public IntegerSetting multiplyHeight;
    public DoubleSetting millSin;
    public IntegerSetting alphaColor;
    public IntegerSetting upPosition;
    public IntegerSetting leftPosition;
    public DoubleSetting yScale;
    public DoubleSetting xScale;
    public IntegerSetting maxH;
    public IntegerSetting maxW;
    BooleanSetting greenText;
    BooleanSetting unFormattedText;
    BooleanSetting chatTimeStamps;
    ModeSetting format;
    ModeSetting decoration;
    public ColorSetting timeColor;
    BooleanSetting specialTime;
    BooleanSetting customName;
    public ColorSetting friendColor;
    BooleanSetting specialFriend;
    public ColorSetting enemyColor;
    public ColorSetting playerColor;
    BooleanSetting space;
    public BooleanSetting watermarkSpecial;
    public ColorSetting aqua;
    public ColorSetting black;
    public ColorSetting blue;
    public ColorSetting dark_aqua;
    public ColorSetting dark_blue;
    public ColorSetting dark_cyan;
    public ColorSetting dark_gray;
    public ColorSetting dark_green;
    public ColorSetting dark_purple;
    public ColorSetting dark_red;
    public ColorSetting gray;
    public ColorSetting green;
    public ColorSetting gold;
    public ColorSetting yellow;
    public ColorSetting purple;
    public ColorSetting red;
    public ColorSetting white;
    @EventHandler
    private final Listener<ClientChatReceivedEvent> chatReceivedEventListener;
    @EventHandler
    private final Listener<PacketEvent.Send> listener;
    
    public ChatModifier() {
        this.backColor = this.registerColor("Background Color", new GSColor(0, 0, 0));
        this.normalColor = this.registerColor("Normal Color", new GSColor(0, 0, 0));
        this.specialColor = this.registerColor("Special Color", new GSColor(0, 125, 125));
        this.desyncRainbowNormal = this.registerBoolean("Desync Rainbow Normal", false);
        this.desyncRainbowSpecial = this.registerBoolean("Desync Rainbow Special", false);
        this.rainbowType = this.registerMode("Rainbow Type", Arrays.asList("Slow", "Sin", "Tan", "Secant", "Cosecant", "Cotangent"), "Tan");
        this.rainbowDesyncSmooth = this.registerInteger("Rainbow Desync Smooth", 50, 1, 1000);
        this.heightSin = this.registerDouble("Height Sin", 1.0, 0.1, 20.0);
        this.multiplyHeight = this.registerInteger("Multiply Height Sin", 1, 1, 10);
        this.millSin = this.registerDouble("Mill Sin", 10.0, 0.1, 15.0);
        this.alphaColor = this.registerInteger("Alpha Color", 255, 0, 255);
        this.upPosition = this.registerInteger("Up Translation", -1, -100, 700);
        this.leftPosition = this.registerInteger("Left Translation", -1, -1, 700);
        this.yScale = this.registerDouble("Height Scale", 1.0, 0.0, 3.0);
        this.xScale = this.registerDouble("Width Scale", 1.0, 0.0, 3.0);
        this.maxH = this.registerInteger("Max Height", -1, -1, 500);
        this.maxW = this.registerInteger("Max Width", -1, -1, 500);
        this.greenText = this.registerBoolean("Green Text", false);
        this.unFormattedText = this.registerBoolean("Unformatted Text", false);
        this.chatTimeStamps = this.registerBoolean("Chat Time Stamp", false);
        this.format = this.registerMode("Format", Arrays.asList("H24:mm", "H12:mm", "H12:mm a", "H24:mm:ss", "H12:mm:ss", "H12:mm:ss a"), "H24:mm");
        this.decoration = this.registerMode("Deco", Arrays.asList("< >", "[ ]", "{ }", " "), "[ ]");
        this.timeColor = this.registerColor("Time Color", new GSColor(85, 255, 255));
        this.specialTime = this.registerBoolean("Special Color Time", false);
        this.customName = this.registerBoolean("Custom Name", true);
        this.friendColor = this.registerColor("Friend Color", new GSColor(85, 255, 255));
        this.specialFriend = this.registerBoolean("Special Color Friend", false);
        this.enemyColor = this.registerColor("Enemy Color", new GSColor(85, 255, 255));
        this.playerColor = this.registerColor("Player Color", new GSColor(85, 255, 255));
        this.space = this.registerBoolean("Space", false);
        this.watermarkSpecial = this.registerBoolean("Watermark Special", true);
        this.aqua = this.registerColor("Aqua", new GSColor(85, 255, 255));
        this.black = this.registerColor("Black", new GSColor(0, 0, 0));
        this.blue = this.registerColor("Blue", new GSColor(85, 85, 255));
        this.dark_aqua = this.registerColor("Dark Aqua", new GSColor(0, 170, 170));
        this.dark_blue = this.registerColor("Dark Blue", new GSColor(0, 0, 170));
        this.dark_cyan = this.registerColor("Dark Cyan", new GSColor(0, 170, 170));
        this.dark_gray = this.registerColor("Dark Gray", new GSColor(85, 85, 85));
        this.dark_green = this.registerColor("Dark Green", new GSColor(0, 170, 0));
        this.dark_purple = this.registerColor("Dark Purple", new GSColor(170, 0, 170));
        this.dark_red = this.registerColor("Dark Red", new GSColor(170, 0, 0));
        this.gray = this.registerColor("Gray", new GSColor(170, 170, 170));
        this.green = this.registerColor("Green", new GSColor(85, 255, 85));
        this.gold = this.registerColor("Gold", new GSColor(255, 170, 0));
        this.yellow = this.registerColor("Yellow", new GSColor(255, 255, 85));
        this.purple = this.registerColor("Purple", new GSColor(255, 85, 255));
        this.red = this.registerColor("Red", new GSColor(255, 85, 85));
        this.white = this.registerColor("White", new GSColor(255, 255, 255));
        ITextComponent output;
        String name;
        String nome;
        final TextComponentString textComponentString;
        String decoLeft;
        String decoRight;
        String dateFormat;
        String date;
        final TextComponentString textComponentString2;
        TextComponentString time;
        this.chatReceivedEventListener = new Listener<ClientChatReceivedEvent>(event -> {
            output = event.getMessage();
            if (this.customName.getValue()) {
                name = event.getMessage().func_150260_c().split(" ")[0];
                nome = output.func_150254_d().split(" ")[0];
                new TextComponentString((this.isFriend(name) ? (this.specialFriend.getValue() ? "\u2063" : "\u2064") : (this.isEnemy(name) ? "\u2065" : "\u2066")) + name + ChatFormatting.RESET + output.func_150254_d().substring(output.func_150254_d().split(" ")[0].length()));
                output = (ITextComponent)textComponentString;
            }
            if (this.chatTimeStamps.getValue()) {
                decoLeft = (this.decoration.getValue().equalsIgnoreCase(" ") ? "" : this.decoration.getValue().split(" ")[0]);
                decoRight = (this.decoration.getValue().equalsIgnoreCase(" ") ? "" : this.decoration.getValue().split(" ")[1]);
                if (this.space.getValue()) {
                    decoRight += " ";
                }
                dateFormat = this.format.getValue().replace("H24", "k").replace("H12", "h");
                date = new SimpleDateFormat(dateFormat).format(new Date());
                new TextComponentString((this.specialTime.getValue() ? "\u2063" : "\u2067") + decoLeft + date + decoRight + TextFormatting.RESET);
                time = textComponentString2;
                output = time.func_150257_a(output);
            }
            if (this.unFormattedText.getValue()) {
                output = (ITextComponent)new TextComponentString(output.func_150260_c());
            }
            event.setMessage(output);
            return;
        }, (Predicate<ClientChatReceivedEvent>[])new Predicate[0]);
        String message;
        String prefix;
        String prefix2;
        String s;
        this.listener = new Listener<PacketEvent.Send>(event -> {
            if (this.greenText.getValue() && event.getPacket() instanceof CPacketChatMessage) {
                if (!((CPacketChatMessage)event.getPacket()).func_149439_c().startsWith("/") && !((CPacketChatMessage)event.getPacket()).func_149439_c().startsWith(CommandManager.getCommandPrefix())) {
                    message = ((CPacketChatMessage)event.getPacket()).func_149439_c();
                    prefix = "";
                    prefix2 = ">";
                    s = prefix2 + message;
                    if (s.length() <= 255) {
                        ((CPacketChatMessage)event.getPacket()).field_149440_a = s;
                    }
                }
            }
        }, (Predicate<PacketEvent.Send>[])new Predicate[0]);
    }
    
    private boolean isFriend(String name) {
        name = name.toLowerCase();
        for (final Friend friend : SocialManager.getFriends()) {
            if (name.contains(friend.getName().toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isEnemy(String name) {
        name = name.toLowerCase();
        for (final Enemy enemy : SocialManager.getEnemies()) {
            if (name.contains(enemy.getName().toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
