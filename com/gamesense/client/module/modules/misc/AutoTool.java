// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import com.gamesense.api.util.player.InventoryUtil;
import java.util.function.Predicate;
import com.gamesense.api.event.events.DestroyBlockEvent;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.DamageBlockEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.BlockPos;
import java.util.HashMap;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "AutoTool", category = Category.Misc)
public class AutoTool extends Module
{
    BooleanSetting switchBack;
    private final HashMap<BlockPos, Integer> blockPosIntegerHashMap;
    @EventHandler
    private final Listener<DamageBlockEvent> damageBlockEventListener;
    @EventHandler
    private final Listener<DestroyBlockEvent> destroyBlockEventListener;
    
    public AutoTool() {
        this.switchBack = this.registerBoolean("Switch Back", true);
        this.blockPosIntegerHashMap = new HashMap<BlockPos, Integer>();
        this.damageBlockEventListener = new Listener<DamageBlockEvent>(event -> this.runAutoTool(event.getBlockPos(), this.blockPosIntegerHashMap.getOrDefault(event.getBlockPos(), -1)), (Predicate<DamageBlockEvent>[])new Predicate[0]);
        this.destroyBlockEventListener = new Listener<DestroyBlockEvent>(event -> {
            if (AutoTool.mc.field_71439_g != null && AutoTool.mc.field_71441_e != null) {
                if (this.switchBack.getValue() && this.blockPosIntegerHashMap.containsKey(event.getBlockPos()) && AutoTool.mc.field_71439_g.field_71071_by.field_70461_c != this.blockPosIntegerHashMap.get(event.getBlockPos())) {
                    AutoTool.mc.field_71439_g.field_71071_by.field_70461_c = this.blockPosIntegerHashMap.get(event.getBlockPos());
                }
                if (!this.switchBack.getValue() || this.blockPosIntegerHashMap.size() >= 10) {
                    this.blockPosIntegerHashMap.clear();
                }
            }
        }, (Predicate<DestroyBlockEvent>[])new Predicate[0]);
    }
    
    private void runAutoTool(final BlockPos blockPos, final int switchSlot) {
        final int toolSlot = InventoryUtil.findToolForBlockState(AutoTool.mc.field_71441_e.func_180495_p(blockPos), 0, 9);
        if (toolSlot != -1) {
            this.blockPosIntegerHashMap.put(blockPos, (switchSlot != -1) ? switchSlot : AutoTool.mc.field_71439_g.field_71071_by.field_70461_c);
            AutoTool.mc.field_71439_g.field_71071_by.field_70461_c = toolSlot;
        }
    }
}
