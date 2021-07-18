// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import java.util.function.Predicate;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumHandSide;
import java.util.Arrays;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.TransformSideFirstPersonEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "ViewModel", category = Category.Render)
public class ViewModel extends Module
{
    ModeSetting type;
    public BooleanSetting cancelEating;
    DoubleSetting xLeft;
    DoubleSetting yLeft;
    DoubleSetting zLeft;
    DoubleSetting xRight;
    DoubleSetting yRight;
    DoubleSetting zRight;
    DoubleSetting fov;
    @EventHandler
    private final Listener<TransformSideFirstPersonEvent> eventListener;
    @EventHandler
    private final Listener<EntityViewRenderEvent.FOVModifier> fovModifierListener;
    
    public ViewModel() {
        this.type = this.registerMode("Type", Arrays.asList("Value", "FOV", "Both"), "Value");
        this.cancelEating = this.registerBoolean("No Eat", false);
        this.xLeft = this.registerDouble("Left X", 0.0, -2.0, 2.0);
        this.yLeft = this.registerDouble("Left Y", 0.2, -2.0, 2.0);
        this.zLeft = this.registerDouble("Left Z", -1.2, -2.0, 2.0);
        this.xRight = this.registerDouble("Right X", 0.0, -2.0, 2.0);
        this.yRight = this.registerDouble("Right Y", 0.2, -2.0, 2.0);
        this.zRight = this.registerDouble("Right Z", -1.2, -2.0, 2.0);
        this.fov = this.registerDouble("Item FOV", 130.0, 70.0, 200.0);
        this.eventListener = new Listener<TransformSideFirstPersonEvent>(event -> {
            if (this.type.getValue().equalsIgnoreCase("Value") || this.type.getValue().equalsIgnoreCase("Both")) {
                if (event.getEnumHandSide() == EnumHandSide.RIGHT) {
                    GlStateManager.func_179137_b((double)this.xRight.getValue(), (double)this.yRight.getValue(), (double)this.zRight.getValue());
                }
                else if (event.getEnumHandSide() == EnumHandSide.LEFT) {
                    GlStateManager.func_179137_b((double)this.xLeft.getValue(), (double)this.yLeft.getValue(), (double)this.zLeft.getValue());
                }
            }
            return;
        }, (Predicate<TransformSideFirstPersonEvent>[])new Predicate[0]);
        this.fovModifierListener = new Listener<EntityViewRenderEvent.FOVModifier>(event -> {
            if (this.type.getValue().equalsIgnoreCase("FOV") || this.type.getValue().equalsIgnoreCase("Both")) {
                event.setFOV(this.fov.getValue().floatValue());
            }
        }, (Predicate<EntityViewRenderEvent.FOVModifier>[])new Predicate[0]);
    }
}
