// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import net.minecraft.item.Item;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "NoEntityTrace", category = Category.Misc)
public class NoEntityTrace extends Module
{
    BooleanSetting pickaxe;
    BooleanSetting obsidian;
    BooleanSetting eChest;
    BooleanSetting block;
    BooleanSetting all;
    boolean isHoldingPickaxe;
    boolean isHoldingObsidian;
    boolean isHoldingEChest;
    boolean isHoldingBlock;
    
    public NoEntityTrace() {
        this.pickaxe = this.registerBoolean("Pickaxe", true);
        this.obsidian = this.registerBoolean("Obsidian", false);
        this.eChest = this.registerBoolean("EnderChest", false);
        this.block = this.registerBoolean("Blocks", false);
        this.all = this.registerBoolean("All", false);
        this.isHoldingPickaxe = false;
        this.isHoldingObsidian = false;
        this.isHoldingEChest = false;
        this.isHoldingBlock = false;
    }
    
    @Override
    public void onUpdate() {
        final Item item = NoEntityTrace.mc.field_71439_g.func_184614_ca().func_77973_b();
        this.isHoldingPickaxe = (item instanceof ItemPickaxe);
        this.isHoldingBlock = (item instanceof ItemBlock);
        if (this.isHoldingBlock) {
            this.isHoldingObsidian = (((ItemBlock)item).func_179223_d() instanceof BlockObsidian);
            this.isHoldingEChest = (((ItemBlock)item).func_179223_d() instanceof BlockEnderChest);
        }
        else {
            this.isHoldingObsidian = false;
            this.isHoldingEChest = false;
        }
    }
    
    public boolean noTrace() {
        if (this.pickaxe.getValue() && this.isHoldingPickaxe) {
            return this.isEnabled();
        }
        if (this.obsidian.getValue() && this.isHoldingObsidian) {
            return this.isEnabled();
        }
        if (this.eChest.getValue() && this.isHoldingEChest) {
            return this.isEnabled();
        }
        if (this.block.getValue() && this.isHoldingBlock) {
            return this.isEnabled();
        }
        return this.all.getValue() && this.isEnabled();
    }
}
