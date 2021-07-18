// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import java.util.Iterator;
import net.minecraft.init.MobEffects;
import net.minecraft.entity.item.EntityEnderPearl;
import com.gamesense.api.util.misc.MessageBus;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import net.minecraft.entity.player.EntityPlayer;
import com.gamesense.client.manager.managers.TotemPopManager;
import java.util.ArrayList;
import com.gamesense.api.util.misc.ColorUtil;
import net.minecraft.entity.Entity;
import java.util.List;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "PvPInfo", category = Category.Misc)
public class PvPInfo extends Module
{
    BooleanSetting visualRange;
    BooleanSetting pearlAlert;
    BooleanSetting burrowAlert;
    BooleanSetting strengthDetect;
    BooleanSetting weaknessDetect;
    BooleanSetting popCounter;
    ModeSetting chatColor;
    List<Entity> knownPlayers;
    List<Entity> antiPearlList;
    List<Entity> players;
    List<Entity> pearls;
    List<Entity> burrowedPlayers;
    List<Entity> strengthPlayers;
    List<Entity> weaknessPlayers;
    
    public PvPInfo() {
        this.visualRange = this.registerBoolean("Visual Range", false);
        this.pearlAlert = this.registerBoolean("Pearl Alert", false);
        this.burrowAlert = this.registerBoolean("Burrow Alert", false);
        this.strengthDetect = this.registerBoolean("Strength Detect", false);
        this.weaknessDetect = this.registerBoolean("Weakness Detect", false);
        this.popCounter = this.registerBoolean("Pop Counter", false);
        this.chatColor = this.registerMode("Color", ColorUtil.colors, "Light Purple");
        this.knownPlayers = new ArrayList<Entity>();
        this.antiPearlList = new ArrayList<Entity>();
        this.burrowedPlayers = new ArrayList<Entity>();
        this.strengthPlayers = new ArrayList<Entity>();
        this.weaknessPlayers = new ArrayList<Entity>();
    }
    
    @Override
    public void onUpdate() {
        if (PvPInfo.mc.field_71439_g == null || PvPInfo.mc.field_71441_e == null) {
            return;
        }
        TotemPopManager.INSTANCE.sendMsgs = this.popCounter.getValue();
        if (this.popCounter.getValue()) {
            TotemPopManager.INSTANCE.chatFormatting = ColorUtil.textToChatFormatting(this.chatColor);
        }
        if (this.visualRange.getValue()) {
            this.players = (List<Entity>)PvPInfo.mc.field_71441_e.field_72996_f.stream().filter(e -> e instanceof EntityPlayer).collect(Collectors.toList());
            try {
                for (final Entity e2 : this.players) {
                    if (e2 instanceof EntityPlayer && !e2.func_70005_c_().equalsIgnoreCase(PvPInfo.mc.field_71439_g.func_70005_c_()) && !this.knownPlayers.contains(e2)) {
                        this.knownPlayers.add(e2);
                        MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + e2.func_70005_c_() + " has been spotted thanks to GameSense!");
                    }
                }
            }
            catch (Exception ex) {}
            try {
                for (final Entity e2 : this.knownPlayers) {
                    if (e2 instanceof EntityPlayer && !e2.func_70005_c_().equalsIgnoreCase(PvPInfo.mc.field_71439_g.func_70005_c_()) && !this.players.contains(e2)) {
                        this.knownPlayers.remove(e2);
                    }
                }
            }
            catch (Exception ex2) {}
        }
        if (this.burrowAlert.getValue()) {
            for (final Entity entity : (List)PvPInfo.mc.field_71441_e.field_72996_f.stream().filter(e -> e instanceof EntityPlayer).collect(Collectors.toList())) {
                if (!(entity instanceof EntityPlayer)) {
                    continue;
                }
                if (!this.burrowedPlayers.contains(entity) && this.isBurrowed(entity)) {
                    this.burrowedPlayers.add(entity);
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + entity.func_70005_c_() + " has just burrowed!");
                }
                else {
                    if (!this.burrowedPlayers.contains(entity) || this.isBurrowed(entity)) {
                        continue;
                    }
                    this.burrowedPlayers.remove(entity);
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + entity.func_70005_c_() + " is no longer burrowed!");
                }
            }
        }
        if (this.pearlAlert.getValue()) {
            this.pearls = (List<Entity>)PvPInfo.mc.field_71441_e.field_72996_f.stream().filter(e -> e instanceof EntityEnderPearl).collect(Collectors.toList());
            try {
                for (final Entity e2 : this.pearls) {
                    if (e2 instanceof EntityEnderPearl && !this.antiPearlList.contains(e2)) {
                        this.antiPearlList.add(e2);
                        MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + e2.func_130014_f_().func_72890_a(e2, 3.0).func_70005_c_() + " has just thrown a pearl!");
                    }
                }
            }
            catch (Exception ex3) {}
        }
        if (this.strengthDetect.getValue() || this.weaknessDetect.getValue()) {
            for (final EntityPlayer player : PvPInfo.mc.field_71441_e.field_73010_i) {
                if (player.func_70644_a(MobEffects.field_76420_g) && !this.strengthPlayers.contains(player)) {
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + player.func_70005_c_() + " has (drank) strength!");
                    this.strengthPlayers.add((Entity)player);
                }
                if (player.func_70644_a(MobEffects.field_76437_t) && !this.weaknessPlayers.contains(player)) {
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + player.func_70005_c_() + " has (drank) wealness!");
                    this.weaknessPlayers.add((Entity)player);
                }
                if (!player.func_70644_a(MobEffects.field_76420_g) && this.strengthPlayers.contains(player)) {
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + player.func_70005_c_() + " no longer has strength!");
                    this.strengthPlayers.remove(player);
                }
                if (!player.func_70644_a(MobEffects.field_76437_t) && this.weaknessPlayers.contains(player)) {
                    MessageBus.sendClientPrefixMessage(ColorUtil.textToChatFormatting(this.chatColor) + player.func_70005_c_() + " no longer has weakness!");
                    this.weaknessPlayers.remove(player);
                }
            }
        }
    }
    
    private boolean isBurrowed(final Entity entity) {
        final BlockPos entityPos = new BlockPos(this.roundValueToCenter(entity.field_70165_t), entity.field_70163_u + 0.2, this.roundValueToCenter(entity.field_70161_v));
        return PvPInfo.mc.field_71441_e.func_180495_p(entityPos).func_177230_c() == Blocks.field_150343_Z || PvPInfo.mc.field_71441_e.func_180495_p(entityPos).func_177230_c() == Blocks.field_150477_bB;
    }
    
    private double roundValueToCenter(final double inputVal) {
        double roundVal = (double)Math.round(inputVal);
        if (roundVal > inputVal) {
            roundVal -= 0.5;
        }
        else if (roundVal <= inputVal) {
            roundVal += 0.5;
        }
        return roundVal;
    }
    
    public void onDisable() {
        this.knownPlayers.clear();
        TotemPopManager.INSTANCE.sendMsgs = false;
    }
}
