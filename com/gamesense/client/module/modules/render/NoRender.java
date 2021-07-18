// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import net.minecraft.init.MobEffects;
import net.minecraft.block.material.Material;
import java.util.function.Predicate;
import com.gamesense.api.event.events.BossbarEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "NoRender", category = Category.Render)
public class NoRender extends Module
{
    public BooleanSetting armor;
    BooleanSetting fire;
    BooleanSetting blind;
    BooleanSetting nausea;
    public BooleanSetting hurtCam;
    public BooleanSetting noSkylight;
    public BooleanSetting noOverlay;
    BooleanSetting noBossBar;
    public BooleanSetting noCluster;
    IntegerSetting maxNoClusterRender;
    public int currentClusterAmount;
    @EventHandler
    public Listener<RenderBlockOverlayEvent> blockOverlayEventListener;
    @EventHandler
    private final Listener<EntityViewRenderEvent.FogDensity> fogDensityListener;
    @EventHandler
    private final Listener<RenderBlockOverlayEvent> renderBlockOverlayEventListener;
    @EventHandler
    private final Listener<RenderGameOverlayEvent> renderGameOverlayEventListener;
    @EventHandler
    private final Listener<BossbarEvent> bossbarEventListener;
    
    public NoRender() {
        this.armor = this.registerBoolean("Armor", false);
        this.fire = this.registerBoolean("Fire", false);
        this.blind = this.registerBoolean("Blind", false);
        this.nausea = this.registerBoolean("Nausea", false);
        this.hurtCam = this.registerBoolean("HurtCam", false);
        this.noSkylight = this.registerBoolean("Skylight", false);
        this.noOverlay = this.registerBoolean("No Overlay", false);
        this.noBossBar = this.registerBoolean("No Boss Bar", false);
        this.noCluster = this.registerBoolean("No Cluster", false);
        this.maxNoClusterRender = this.registerInteger("No Cluster Max", 5, 1, 25);
        this.currentClusterAmount = 0;
        this.blockOverlayEventListener = new Listener<RenderBlockOverlayEvent>(event -> {
            if (this.fire.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.FIRE) {
                event.setCanceled(true);
            }
            if (this.noOverlay.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER) {
                event.setCanceled(true);
            }
            if (this.noOverlay.getValue() && event.getOverlayType() == RenderBlockOverlayEvent.OverlayType.BLOCK) {
                event.setCanceled(true);
            }
            return;
        }, (Predicate<RenderBlockOverlayEvent>[])new Predicate[0]);
        this.fogDensityListener = new Listener<EntityViewRenderEvent.FogDensity>(event -> {
            if (this.noOverlay.getValue() && (event.getState().func_185904_a().equals(Material.field_151586_h) || event.getState().func_185904_a().equals(Material.field_151587_i))) {
                event.setDensity(0.0f);
                event.setCanceled(true);
            }
            return;
        }, (Predicate<EntityViewRenderEvent.FogDensity>[])new Predicate[0]);
        this.renderBlockOverlayEventListener = new Listener<RenderBlockOverlayEvent>(event -> {
            if (this.noOverlay.getValue()) {
                event.setCanceled(true);
            }
            return;
        }, (Predicate<RenderBlockOverlayEvent>[])new Predicate[0]);
        this.renderGameOverlayEventListener = new Listener<RenderGameOverlayEvent>(event -> {
            if (this.noOverlay.getValue()) {
                if (event.getType().equals((Object)RenderGameOverlayEvent.ElementType.HELMET)) {
                    event.setCanceled(true);
                }
                if (event.getType().equals((Object)RenderGameOverlayEvent.ElementType.PORTAL)) {
                    event.setCanceled(true);
                }
            }
            return;
        }, (Predicate<RenderGameOverlayEvent>[])new Predicate[0]);
        this.bossbarEventListener = new Listener<BossbarEvent>(event -> {
            if (this.noBossBar.getValue()) {
                event.cancel();
            }
        }, (Predicate<BossbarEvent>[])new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        if (this.blind.getValue() && NoRender.mc.field_71439_g.func_70644_a(MobEffects.field_76440_q)) {
            NoRender.mc.field_71439_g.func_184589_d(MobEffects.field_76440_q);
        }
        if (this.nausea.getValue() && NoRender.mc.field_71439_g.func_70644_a(MobEffects.field_76431_k)) {
            NoRender.mc.field_71439_g.func_184589_d(MobEffects.field_76431_k);
        }
    }
    
    @Override
    public void onRender() {
        this.currentClusterAmount = 0;
    }
    
    public boolean incrementNoClusterRender() {
        ++this.currentClusterAmount;
        return this.currentClusterAmount <= this.maxNoClusterRender.getValue();
    }
    
    public boolean getNoClusterRender() {
        return this.currentClusterAmount <= this.maxNoClusterRender.getValue();
    }
}
