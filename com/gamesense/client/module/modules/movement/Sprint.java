// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.movement;

import net.minecraft.client.entity.EntityPlayerSP;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Sprint", category = Category.Movement)
public class Sprint extends Module
{
    BooleanSetting multiDirection;
    
    public Sprint() {
        this.multiDirection = this.registerBoolean("Multi Direction", true);
    }
    
    @Override
    public void onUpdate() {
        final EntityPlayerSP player = Sprint.mc.field_71439_g;
        if (player != null) {
            player.func_70031_b(this.shouldSprint(player));
        }
    }
    
    public boolean shouldSprint(final EntityPlayerSP player) {
        return !Sprint.mc.field_71474_y.field_74311_E.func_151470_d() && player.func_71024_bL().func_75116_a() > 6 && !player.func_184613_cA() && !Sprint.mc.field_71439_g.field_71075_bZ.field_75100_b && this.checkMovementInput(player);
    }
    
    private boolean checkMovementInput(final EntityPlayerSP player) {
        return this.multiDirection.getValue() ? (player.field_191988_bg != 0.0f || player.field_70702_br != 0.0f) : (player.field_191988_bg > 0.0f);
    }
}
