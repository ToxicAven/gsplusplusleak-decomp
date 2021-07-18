// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.hud;

import com.gamesense.api.setting.Setting;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import java.util.function.Predicate;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.HUDModule;

@Module.Declaration(name = "Coordinates", category = Category.HUD)
@Declaration(posX = 0, posZ = 0)
public class Coordinates extends HUDModule
{
    BooleanSetting showNetherOverworld;
    BooleanSetting thousandsSeparator;
    IntegerSetting decimalPlaces;
    private final String[] coordinateString;
    @EventHandler
    private final Listener<TickEvent.ClientTickEvent> listener;
    
    public Coordinates() {
        this.showNetherOverworld = this.registerBoolean("Show Nether", true);
        this.thousandsSeparator = this.registerBoolean("Thousands Separator", true);
        this.decimalPlaces = this.registerInteger("Decimal Places", 1, 0, 5);
        this.coordinateString = new String[] { "", "" };
        Entity viewEntity;
        EntityPlayerSP player;
        int dimension;
        this.listener = new Listener<TickEvent.ClientTickEvent>(event -> {
            if (event.phase == TickEvent.Phase.END) {
                viewEntity = Coordinates.mc.func_175606_aa();
                player = Coordinates.mc.field_71439_g;
                if (viewEntity == null) {
                    if (player != null) {
                        viewEntity = (Entity)player;
                    }
                    else {
                        return;
                    }
                }
                dimension = viewEntity.field_71093_bK;
                this.coordinateString[0] = "XYZ " + this.getFormattedCoords(viewEntity.field_70165_t, viewEntity.field_70163_u, viewEntity.field_70161_v);
                switch (dimension) {
                    case -1: {
                        this.coordinateString[1] = "Overworld " + this.getFormattedCoords(viewEntity.field_70165_t * 8.0, viewEntity.field_70163_u, viewEntity.field_70161_v * 8.0);
                        break;
                    }
                    case 0: {
                        this.coordinateString[1] = "Nether " + this.getFormattedCoords(viewEntity.field_70165_t / 8.0, viewEntity.field_70163_u, viewEntity.field_70161_v / 8.0);
                        break;
                    }
                }
            }
        }, (Predicate<TickEvent.ClientTickEvent>[])new Predicate[0]);
    }
    
    private String getFormattedCoords(final double x, final double y, final double z) {
        return this.roundOrInt(x) + ", " + this.roundOrInt(y) + ", " + this.roundOrInt(z);
    }
    
    private String roundOrInt(final double input) {
        String separatorFormat;
        if (this.thousandsSeparator.getValue()) {
            separatorFormat = ",";
        }
        else {
            separatorFormat = "";
        }
        return String.format('%' + separatorFormat + '.' + ((Setting<Object>)this.decimalPlaces).getValue() + 'f', input);
    }
    
    @Override
    public void populate(final Theme theme) {
        this.component = new ListComponent(this.getName(), theme.getPanelRenderer(), this.position, new CoordinateLabel());
    }
    
    private class CoordinateLabel implements HUDList
    {
        @Override
        public int getSize() {
            final EntityPlayerSP player = Coordinates.mc.field_71439_g;
            final int dimension = (player != null) ? player.field_71093_bK : 1;
            if (Coordinates.this.showNetherOverworld.getValue() && (dimension == -1 || dimension == 0)) {
                return 2;
            }
            return 1;
        }
        
        @Override
        public String getItem(final int index) {
            return Coordinates.this.coordinateString[index];
        }
        
        @Override
        public Color getItemColor(final int index) {
            return new Color(255, 255, 255);
        }
        
        @Override
        public boolean sortUp() {
            return false;
        }
        
        @Override
        public boolean sortRight() {
            return false;
        }
    }
}
