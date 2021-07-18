// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import java.util.function.Predicate;
import com.gamesense.api.util.render.GSColor;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "SkyColor", category = Category.Render)
public class SkyColor extends Module
{
    BooleanSetting fog;
    ColorSetting color;
    @EventHandler
    private final Listener<EntityViewRenderEvent.FogColors> fogColorsListener;
    @EventHandler
    private final Listener<EntityViewRenderEvent.FogDensity> fogDensityListener;
    
    public SkyColor() {
        this.fog = this.registerBoolean("Fog", true);
        this.color = this.registerColor("Color", new GSColor(0, 255, 0, 255));
        this.fogColorsListener = new Listener<EntityViewRenderEvent.FogColors>(event -> {
            event.setRed(this.color.getValue().getRed() / 255.0f);
            event.setGreen(this.color.getValue().getGreen() / 255.0f);
            event.setBlue(this.color.getValue().getBlue() / 255.0f);
            return;
        }, (Predicate<EntityViewRenderEvent.FogColors>[])new Predicate[0]);
        this.fogDensityListener = new Listener<EntityViewRenderEvent.FogDensity>(event -> {
            if (!this.fog.getValue()) {
                event.setDensity(0.0f);
                event.setCanceled(true);
            }
        }, (Predicate<EntityViewRenderEvent.FogDensity>[])new Predicate[0]);
    }
}
