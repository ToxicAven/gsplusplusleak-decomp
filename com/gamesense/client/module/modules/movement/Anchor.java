// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.movement;

import java.util.Map;
import java.util.HashMap;
import com.gamesense.api.util.world.HoleUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Anchor", category = Category.Movement)
public class Anchor extends Module
{
    BooleanSetting guarantee;
    IntegerSetting activateHeight;
    BlockPos playerPos;
    
    public Anchor() {
        this.guarantee = this.registerBoolean("Guarantee Hole", true);
        this.activateHeight = this.registerInteger("Activate Height", 2, 1, 5);
    }
    
    @Override
    public void onUpdate() {
        if (Anchor.mc.field_71439_g == null) {
            return;
        }
        if (Anchor.mc.field_71439_g.field_70163_u < 0.0) {
            return;
        }
        final double blockX = Math.floor(Anchor.mc.field_71439_g.field_70165_t);
        final double blockZ = Math.floor(Anchor.mc.field_71439_g.field_70161_v);
        final double offsetX = Math.abs(Anchor.mc.field_71439_g.field_70165_t - blockX);
        final double offsetZ = Math.abs(Anchor.mc.field_71439_g.field_70161_v - blockZ);
        if (this.guarantee.getValue() && (offsetX < 0.30000001192092896 || offsetX > 0.699999988079071 || offsetZ < 0.30000001192092896 || offsetZ > 0.699999988079071)) {
            return;
        }
        this.playerPos = new BlockPos(blockX, Anchor.mc.field_71439_g.field_70163_u, blockZ);
        if (Anchor.mc.field_71441_e.func_180495_p(this.playerPos).func_177230_c() != Blocks.field_150350_a) {
            return;
        }
        BlockPos currentBlock = this.playerPos.func_177977_b();
        for (int i = 0; i < this.activateHeight.getValue(); ++i) {
            currentBlock = currentBlock.func_177977_b();
            if (Anchor.mc.field_71441_e.func_180495_p(currentBlock).func_177230_c() != Blocks.field_150350_a) {
                final HashMap<HoleUtil.BlockOffset, HoleUtil.BlockSafety> sides = HoleUtil.getUnsafeSides(currentBlock.func_177984_a());
                sides.entrySet().removeIf(entry -> entry.getValue() == HoleUtil.BlockSafety.RESISTANT);
                if (sides.size() == 0) {
                    Anchor.mc.field_71439_g.field_70159_w = 0.0;
                    Anchor.mc.field_71439_g.field_70179_y = 0.0;
                }
            }
        }
    }
}
