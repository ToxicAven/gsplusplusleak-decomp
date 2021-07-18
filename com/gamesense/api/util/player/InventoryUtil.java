// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.player;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.init.Enchantments;
import net.minecraft.block.state.IBlockState;
import java.util.ArrayList;
import net.minecraft.item.Item;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSkull;
import net.minecraft.block.Block;
import java.util.List;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.OffHand;
import net.minecraft.client.Minecraft;

public class InventoryUtil
{
    private static final Minecraft mc;
    
    public static int findObsidianSlot(final boolean offHandActived, final boolean activeBefore) {
        int slot = -1;
        final List<ItemStack> mainInventory = (List<ItemStack>)InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        if (offHandActived && ModuleManager.isModuleEnabled(OffHand.class)) {
            if (!activeBefore) {
                OffHand.requestItems(0);
            }
            return 9;
        }
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.field_190927_a) {
                if (stack.func_77973_b() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
                    if (block instanceof BlockObsidian) {
                        slot = i;
                        break;
                    }
                }
            }
        }
        return slot;
    }
    
    public static int findSkullSlot(final boolean offHandActived, final boolean activeBefore) {
        final int slot = -1;
        final List<ItemStack> mainInventory = (List<ItemStack>)InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        if (offHandActived) {
            if (!activeBefore) {
                OffHand.requestItems(1);
            }
            return 9;
        }
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.field_190927_a && stack.func_77973_b() instanceof ItemSkull) {
                return i;
            }
        }
        return slot;
    }
    
    public static int findTotemSlot(final int lower, final int upper) {
        int slot = -1;
        final List<ItemStack> mainInventory = (List<ItemStack>)InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        for (int i = lower; i <= upper; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.field_190927_a && stack.func_77973_b() == Items.field_190929_cY) {
                slot = i;
                break;
            }
        }
        return slot;
    }
    
    public static int findFirstItemSlot(final Class<? extends Item> itemToFind, final int lower, final int upper) {
        int slot = -1;
        final List<ItemStack> mainInventory = (List<ItemStack>)InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        for (int i = lower; i <= upper; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.field_190927_a) {
                if (itemToFind.isInstance(stack.func_77973_b())) {
                    if (itemToFind.isInstance(stack.func_77973_b())) {
                        slot = i;
                        break;
                    }
                }
            }
        }
        return slot;
    }
    
    public static int findFirstBlockSlot(final Class<? extends Block> blockToFind, final int lower, final int upper) {
        int slot = -1;
        final List<ItemStack> mainInventory = (List<ItemStack>)InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        for (int i = lower; i <= upper; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.field_190927_a) {
                if (stack.func_77973_b() instanceof ItemBlock) {
                    if (blockToFind.isInstance(((ItemBlock)stack.func_77973_b()).func_179223_d())) {
                        slot = i;
                        break;
                    }
                }
            }
        }
        return slot;
    }
    
    public static List<Integer> findAllItemSlots(final Class<? extends Item> itemToFind) {
        final List<Integer> slots = new ArrayList<Integer>();
        final List<ItemStack> mainInventory = (List<ItemStack>)InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        for (int i = 0; i < 36; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.field_190927_a) {
                if (itemToFind.isInstance(stack.func_77973_b())) {
                    slots.add(i);
                }
            }
        }
        return slots;
    }
    
    public static List<Integer> findAllBlockSlots(final Class<? extends Block> blockToFind) {
        final List<Integer> slots = new ArrayList<Integer>();
        final List<ItemStack> mainInventory = (List<ItemStack>)InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        for (int i = 0; i < 36; ++i) {
            final ItemStack stack = mainInventory.get(i);
            if (stack != ItemStack.field_190927_a) {
                if (stack.func_77973_b() instanceof ItemBlock) {
                    if (blockToFind.isInstance(((ItemBlock)stack.func_77973_b()).func_179223_d())) {
                        slots.add(i);
                    }
                }
            }
        }
        return slots;
    }
    
    public static int findToolForBlockState(final IBlockState iBlockState, final int lower, final int upper) {
        int slot = -1;
        final List<ItemStack> mainInventory = (List<ItemStack>)InventoryUtil.mc.field_71439_g.field_71071_by.field_70462_a;
        double foundMaxSpeed = 0.0;
        for (int i = lower; i <= upper; ++i) {
            final ItemStack itemStack = mainInventory.get(i);
            if (itemStack != ItemStack.field_190927_a) {
                float breakSpeed = itemStack.func_150997_a(iBlockState);
                final int efficiencySpeed = EnchantmentHelper.func_77506_a(Enchantments.field_185305_q, itemStack);
                if (breakSpeed > 1.0f) {
                    breakSpeed += (float)((efficiencySpeed > 0) ? (Math.pow(efficiencySpeed, 2.0) + 1.0) : 0.0);
                    if (breakSpeed > foundMaxSpeed) {
                        foundMaxSpeed = breakSpeed;
                        slot = i;
                    }
                }
            }
        }
        return slot;
    }
    
    static {
        mc = Minecraft.func_71410_x();
    }
}
