// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import net.minecraft.block.Block;
import java.util.Iterator;
import net.minecraft.item.Item;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Comparator;
import com.gamesense.api.util.player.InventoryUtil;
import net.minecraft.item.ItemBlock;
import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import com.gamesense.api.util.misc.Pair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.client.gui.inventory.GuiContainer;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "HotbarRefill", category = Category.Misc)
public class HotbarRefill extends Module
{
    IntegerSetting threshold;
    IntegerSetting tickDelay;
    private int delayStep;
    
    public HotbarRefill() {
        this.threshold = this.registerInteger("Threshold", 32, 1, 63);
        this.tickDelay = this.registerInteger("Tick Delay", 2, 1, 10);
        this.delayStep = 0;
    }
    
    @Override
    public void onUpdate() {
        if (HotbarRefill.mc.field_71439_g == null) {
            return;
        }
        if (HotbarRefill.mc.field_71462_r instanceof GuiContainer) {
            return;
        }
        if (this.delayStep < this.tickDelay.getValue()) {
            ++this.delayStep;
            return;
        }
        this.delayStep = 0;
        final Pair<Integer, Integer> slots = this.findReplenishableHotbarSlot();
        if (slots == null) {
            return;
        }
        final int inventorySlot = slots.getKey();
        final int hotbarSlot = slots.getValue();
        HotbarRefill.mc.field_71442_b.func_187098_a(0, inventorySlot, 0, ClickType.PICKUP, (EntityPlayer)HotbarRefill.mc.field_71439_g);
        HotbarRefill.mc.field_71442_b.func_187098_a(0, hotbarSlot + 36, 0, ClickType.PICKUP, (EntityPlayer)HotbarRefill.mc.field_71439_g);
        HotbarRefill.mc.field_71442_b.func_187098_a(0, inventorySlot, 0, ClickType.PICKUP, (EntityPlayer)HotbarRefill.mc.field_71439_g);
    }
    
    private Pair<Integer, Integer> findReplenishableHotbarSlot() {
        final List<ItemStack> inventory = (List<ItemStack>)HotbarRefill.mc.field_71439_g.field_71071_by.field_70462_a;
        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            final ItemStack stack = inventory.get(hotbarSlot);
            if (stack.func_77985_e()) {
                if (!stack.field_190928_g) {
                    if (stack.func_77973_b() != Items.field_190931_a) {
                        if (stack.field_77994_a < stack.func_77976_d()) {
                            if (stack.field_77994_a <= this.threshold.getValue()) {
                                final int inventorySlot = this.findCompatibleInventorySlot(stack);
                                if (inventorySlot != -1) {
                                    return new Pair<Integer, Integer>(inventorySlot, hotbarSlot);
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
    
    private int findCompatibleInventorySlot(final ItemStack hotbarStack) {
        final Item item = hotbarStack.func_77973_b();
        List<Integer> potentialSlots;
        if (item instanceof ItemBlock) {
            potentialSlots = InventoryUtil.findAllBlockSlots(((ItemBlock)item).func_179223_d().getClass());
        }
        else {
            potentialSlots = InventoryUtil.findAllItemSlots(item.getClass());
        }
        potentialSlots = potentialSlots.stream().filter(integer -> integer > 8 && integer < 36).sorted(Comparator.comparingInt(interger -> -interger)).collect((Collector<? super Object, ?, List<Integer>>)Collectors.toList());
        for (final int slot : potentialSlots) {
            if (this.isCompatibleStacks(hotbarStack, HotbarRefill.mc.field_71439_g.field_71071_by.func_70301_a(slot))) {
                return slot;
            }
        }
        return -1;
    }
    
    private boolean isCompatibleStacks(final ItemStack stack1, final ItemStack stack2) {
        if (!stack1.func_77973_b().equals(stack2.func_77973_b())) {
            return false;
        }
        if (stack1.func_77973_b() instanceof ItemBlock && stack2.func_77973_b() instanceof ItemBlock) {
            final Block block1 = ((ItemBlock)stack1.func_77973_b()).func_179223_d();
            final Block block2 = ((ItemBlock)stack2.func_77973_b()).func_179223_d();
            if (!block1.field_149764_J.equals(block2.field_149764_J)) {
                return false;
            }
        }
        return stack1.func_82833_r().equals(stack2.func_82833_r()) && stack1.func_77952_i() == stack2.func_77952_i();
    }
}
