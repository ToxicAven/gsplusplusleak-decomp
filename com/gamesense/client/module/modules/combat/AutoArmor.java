// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.combat;

import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.init.Items;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import java.util.HashMap;
import net.minecraft.item.Item;
import com.gamesense.api.util.player.InventoryUtil;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.gui.inventory.GuiContainer;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "AutoArmor", category = Category.Combat)
public class AutoArmor extends Module
{
    BooleanSetting noThorns;
    BooleanSetting lastResortThorns;
    
    public AutoArmor() {
        this.noThorns = this.registerBoolean("No Thorns", false);
        this.lastResortThorns = this.registerBoolean("No Other Thorns", false);
    }
    
    @Override
    public void onUpdate() {
        if (AutoArmor.mc.field_71439_g.field_70173_aa % 2 == 0) {
            return;
        }
        if (AutoArmor.mc.field_71462_r instanceof GuiContainer && !(AutoArmor.mc.field_71462_r instanceof InventoryEffectRenderer)) {
            return;
        }
        final List<ItemStack> armorInventory = (List<ItemStack>)AutoArmor.mc.field_71439_g.field_71071_by.field_70460_b;
        final List<ItemStack> inventory = (List<ItemStack>)AutoArmor.mc.field_71439_g.field_71071_by.field_70462_a;
        final int[] bestArmorSlots = { -1, -1, -1, -1 };
        final int[] bestArmorValues = { -1, -1, -1, -1 };
        for (int i = 0; i < 4; ++i) {
            final ItemStack oldArmour = armorInventory.get(i);
            if (oldArmour.func_77973_b() instanceof ItemArmor) {
                bestArmorValues[i] = ((ItemArmor)oldArmour.func_77973_b()).field_77879_b;
            }
        }
        final List<Integer> slots = InventoryUtil.findAllItemSlots((Class<? extends Item>)ItemArmor.class);
        final HashMap<Integer, ItemStack> armour = new HashMap<Integer, ItemStack>();
        final HashMap<Integer, ItemStack> thorns = new HashMap<Integer, ItemStack>();
        for (final Integer slot : slots) {
            final ItemStack item = inventory.get(slot);
            if (this.noThorns.getValue() && EnchantmentHelper.func_82781_a(item).containsKey(Enchantment.func_185262_c(7))) {
                thorns.put(slot, item);
            }
            else {
                armour.put(slot, item);
            }
        }
        final ItemArmor itemArmor;
        final int armorType;
        int armorValue;
        final Object o;
        final Object o2;
        armour.forEach((integer, itemStack) -> {
            itemArmor = (ItemArmor)itemStack.func_77973_b();
            armorType = itemArmor.field_77881_a.ordinal() - 2;
            if (armorType == 2 && AutoArmor.mc.field_71439_g.field_71071_by.func_70440_f(armorType).func_77973_b().equals(Items.field_185160_cR)) {
                return;
            }
            else {
                armorValue = itemArmor.field_77879_b;
                if (armorValue > o[armorType]) {
                    o2[armorType] = (int)integer;
                    o[armorType] = armorValue;
                }
                return;
            }
        });
        if (this.noThorns.getValue() && this.lastResortThorns.getValue()) {
            final ItemArmor itemArmor2;
            final int armorType2;
            final List list;
            final Object o3;
            int armorValue2;
            final Object o4;
            thorns.forEach((integer, itemStack) -> {
                itemArmor2 = (ItemArmor)itemStack.func_77973_b();
                armorType2 = itemArmor2.field_77881_a.ordinal() - 2;
                if (list.get(armorType2) != ItemStack.field_190927_a || o3[armorType2] != -1) {
                    return;
                }
                else if (armorType2 == 2 && AutoArmor.mc.field_71439_g.field_71071_by.func_70440_f(armorType2).func_77973_b().equals(Items.field_185160_cR)) {
                    return;
                }
                else {
                    armorValue2 = itemArmor2.field_77879_b;
                    if (armorValue2 > o4[armorType2]) {
                        o3[armorType2] = (int)integer;
                        o4[armorType2] = armorValue2;
                    }
                    return;
                }
            });
        }
        for (int j = 0; j < 4; ++j) {
            int slot2 = bestArmorSlots[j];
            if (slot2 != -1) {
                if (slot2 < 9) {
                    slot2 += 36;
                }
                AutoArmor.mc.field_71442_b.func_187098_a(0, slot2, 0, ClickType.PICKUP, (EntityPlayer)AutoArmor.mc.field_71439_g);
                AutoArmor.mc.field_71442_b.func_187098_a(0, 8 - j, 0, ClickType.PICKUP, (EntityPlayer)AutoArmor.mc.field_71439_g);
                AutoArmor.mc.field_71442_b.func_187098_a(0, slot2, 0, ClickType.PICKUP, (EntityPlayer)AutoArmor.mc.field_71439_g);
            }
        }
    }
}
