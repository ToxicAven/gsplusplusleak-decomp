// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import com.gamesense.api.util.render.ChamsUtil;
import java.util.function.Predicate;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.Entity;
import com.gamesense.api.util.render.GSColor;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.RenderEntityEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Chams", category = Category.Render)
public class Chams extends Module
{
    ModeSetting chamsType;
    IntegerSetting range;
    BooleanSetting player;
    BooleanSetting mob;
    BooleanSetting crystal;
    IntegerSetting lineWidth;
    IntegerSetting colorOpacity;
    IntegerSetting wireOpacity;
    ColorSetting playerColor;
    ColorSetting mobColor;
    ColorSetting crystalColor;
    @EventHandler
    private final Listener<RenderEntityEvent.Head> renderEntityHeadEventListener;
    @EventHandler
    private final Listener<RenderEntityEvent.Return> renderEntityReturnEventListener;
    
    public Chams() {
        this.chamsType = this.registerMode("Type", Arrays.asList("Texture", "Color", "WireFrame"), "Texture");
        this.range = this.registerInteger("Range", 100, 10, 260);
        this.player = this.registerBoolean("Player", true);
        this.mob = this.registerBoolean("Mob", false);
        this.crystal = this.registerBoolean("Crystal", false);
        this.lineWidth = this.registerInteger("Line Width", 1, 1, 5);
        this.colorOpacity = this.registerInteger("Color Opacity", 100, 0, 255);
        this.wireOpacity = this.registerInteger("Wire Opacity", 200, 0, 255);
        this.playerColor = this.registerColor("Player Color", new GSColor(0, 255, 255, 255));
        this.mobColor = this.registerColor("Mob Color", new GSColor(255, 255, 0, 255));
        this.crystalColor = this.registerColor("Crystal Color", new GSColor(0, 255, 0, 255));
        Entity entity1;
        this.renderEntityHeadEventListener = new Listener<RenderEntityEvent.Head>(event -> {
            if (event.getType() == RenderEntityEvent.Type.COLOR && this.chamsType.getValue().equalsIgnoreCase("Texture")) {
                return;
            }
            else if (event.getType() == RenderEntityEvent.Type.TEXTURE && (this.chamsType.getValue().equalsIgnoreCase("Color") || this.chamsType.getValue().equalsIgnoreCase("WireFrame"))) {
                return;
            }
            else if (Chams.mc.field_71439_g == null || Chams.mc.field_71441_e == null) {
                return;
            }
            else {
                entity1 = event.getEntity();
                if (entity1.func_70032_d((Entity)Chams.mc.field_71439_g) > this.range.getValue()) {
                    return;
                }
                else {
                    if (this.player.getValue() && entity1 instanceof EntityPlayer && entity1 != Chams.mc.field_71439_g) {
                        this.renderChamsPre(new GSColor(this.playerColor.getValue(), 255), true);
                    }
                    if (this.mob.getValue() && (entity1 instanceof EntityCreature || entity1 instanceof EntitySlime || entity1 instanceof EntitySquid)) {
                        this.renderChamsPre(new GSColor(this.mobColor.getValue(), 255), false);
                    }
                    if (this.crystal.getValue() && entity1 instanceof EntityEnderCrystal) {
                        this.renderChamsPre(new GSColor(this.crystalColor.getValue(), 255), false);
                    }
                    return;
                }
            }
        }, (Predicate<RenderEntityEvent.Head>[])new Predicate[0]);
        Entity entity2;
        this.renderEntityReturnEventListener = new Listener<RenderEntityEvent.Return>(event -> {
            if (event.getType() != RenderEntityEvent.Type.COLOR || !this.chamsType.getValue().equalsIgnoreCase("Texture")) {
                if (event.getType() != RenderEntityEvent.Type.TEXTURE || (!this.chamsType.getValue().equalsIgnoreCase("Color") && !this.chamsType.getValue().equalsIgnoreCase("WireFrame"))) {
                    if (Chams.mc.field_71439_g != null && Chams.mc.field_71441_e != null) {
                        entity2 = event.getEntity();
                        if (entity2.func_70032_d((Entity)Chams.mc.field_71439_g) <= this.range.getValue()) {
                            if (this.player.getValue() && entity2 instanceof EntityPlayer && entity2 != Chams.mc.field_71439_g) {
                                this.renderChamsPost(true);
                            }
                            if (this.mob.getValue() && (entity2 instanceof EntityCreature || entity2 instanceof EntitySlime || entity2 instanceof EntitySquid)) {
                                this.renderChamsPost(false);
                            }
                            if (this.crystal.getValue() && entity2 instanceof EntityEnderCrystal) {
                                this.renderChamsPost(false);
                            }
                        }
                    }
                }
            }
        }, (Predicate<RenderEntityEvent.Return>[])new Predicate[0]);
    }
    
    private void renderChamsPre(final GSColor color, final boolean isPlayer) {
        final String s = this.chamsType.getValue();
        switch (s) {
            case "Texture": {
                ChamsUtil.createChamsPre();
                break;
            }
            case "Color": {
                ChamsUtil.createColorPre(new GSColor(color, this.colorOpacity.getValue()), isPlayer);
                break;
            }
            case "WireFrame": {
                ChamsUtil.createWirePre(new GSColor(color, this.wireOpacity.getValue()), this.lineWidth.getValue(), isPlayer);
                break;
            }
        }
    }
    
    private void renderChamsPost(final boolean isPlayer) {
        final String s = this.chamsType.getValue();
        switch (s) {
            case "Texture": {
                ChamsUtil.createChamsPost();
                break;
            }
            case "Color": {
                ChamsUtil.createColorPost(isPlayer);
                break;
            }
            case "WireFrame": {
                ChamsUtil.createWirePost(isPlayer);
                break;
            }
        }
    }
}
