// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import java.util.Optional;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import java.util.Objects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.ContainerShulkerBox;
import net.minecraft.inventory.ContainerChest;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import com.gamesense.client.command.commands.AutoGearCommand;
import java.util.ArrayList;
import java.util.HashMap;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "AutoGear", category = Category.Misc)
public class AutoGear extends Module
{
    IntegerSetting tickDelay;
    IntegerSetting switchForTick;
    BooleanSetting enderChest;
    BooleanSetting confirmSort;
    BooleanSetting invasive;
    BooleanSetting closeAfter;
    BooleanSetting infoMsgs;
    BooleanSetting debugMode;
    private HashMap<Integer, String> planInventory;
    private final HashMap<Integer, String> containerInv;
    private ArrayList<Integer> sortItems;
    private int delayTimeTicks;
    private int stepNow;
    private boolean openedBefore;
    private boolean finishSort;
    private boolean doneBefore;
    
    public AutoGear() {
        this.tickDelay = this.registerInteger("Tick Delay", 0, 0, 20);
        this.switchForTick = this.registerInteger("Switch Per Tick", 1, 1, 100);
        this.enderChest = this.registerBoolean("EnderChest", false);
        this.confirmSort = this.registerBoolean("Confirm Sort", true);
        this.invasive = this.registerBoolean("Invasive", false);
        this.closeAfter = this.registerBoolean("Close After", false);
        this.infoMsgs = this.registerBoolean("Info Msgs", true);
        this.debugMode = this.registerBoolean("Debug Mode", false);
        this.planInventory = new HashMap<Integer, String>();
        this.containerInv = new HashMap<Integer, String>();
        this.sortItems = new ArrayList<Integer>();
    }
    
    public void onEnable() {
        final String curConfigName = AutoGearCommand.getCurrentSet();
        if (curConfigName.equals("")) {
            this.disable();
            return;
        }
        if (this.infoMsgs.getValue()) {
            PistonCrystal.printDebug("Config " + curConfigName + " actived", false);
        }
        final String inventoryConfig = AutoGearCommand.getInventoryKit(curConfigName);
        if (inventoryConfig.equals("")) {
            this.disable();
            return;
        }
        final String[] inventoryDivided = inventoryConfig.split(" ");
        this.planInventory = new HashMap<Integer, String>();
        final HashMap<String, Integer> nItems = new HashMap<String, Integer>();
        for (int i = 0; i < inventoryDivided.length; ++i) {
            if (!inventoryDivided[i].contains("air")) {
                this.planInventory.put(i, inventoryDivided[i]);
                if (nItems.containsKey(inventoryDivided[i])) {
                    nItems.put(inventoryDivided[i], nItems.get(inventoryDivided[i]) + 1);
                }
                else {
                    nItems.put(inventoryDivided[i], 1);
                }
            }
        }
        this.delayTimeTicks = 0;
        final boolean b = false;
        this.doneBefore = b;
        this.openedBefore = b;
    }
    
    public void onDisable() {
        if (this.infoMsgs.getValue() && this.planInventory.size() > 0) {
            PistonCrystal.printDebug("AutoSort Turned Off!", true);
        }
    }
    
    @Override
    public void onUpdate() {
        if (this.delayTimeTicks < this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
            return;
        }
        this.delayTimeTicks = 0;
        if (this.planInventory.size() == 0) {
            this.disable();
        }
        if ((AutoGear.mc.field_71439_g.field_71070_bA instanceof ContainerChest && (this.enderChest.getValue() || !((ContainerChest)AutoGear.mc.field_71439_g.field_71070_bA).func_85151_d().func_145748_c_().func_150260_c().equals("Ender Chest"))) || AutoGear.mc.field_71439_g.field_71070_bA instanceof ContainerShulkerBox) {
            this.sortInventoryAlgo();
        }
        else {
            this.openedBefore = false;
        }
    }
    
    private void sortInventoryAlgo() {
        if (!this.openedBefore) {
            if (this.infoMsgs.getValue() && !this.doneBefore) {
                PistonCrystal.printDebug("Start sorting inventory...", false);
            }
            final int maxValue = (AutoGear.mc.field_71439_g.field_71070_bA instanceof ContainerChest) ? ((ContainerChest)AutoGear.mc.field_71439_g.field_71070_bA).func_85151_d().func_70302_i_() : 27;
            for (int i = 0; i < maxValue; ++i) {
                final ItemStack item = (ItemStack)AutoGear.mc.field_71439_g.field_71070_bA.func_75138_a().get(i);
                this.containerInv.put(i, Objects.requireNonNull(item.func_77973_b().getRegistryName()).toString() + item.func_77960_j());
            }
            this.openedBefore = true;
            final HashMap<Integer, String> inventoryCopy = this.getInventoryCopy(maxValue);
            final HashMap<Integer, String> aimInventory = this.getInventoryCopy(maxValue, this.planInventory);
            this.sortItems = this.getInventorySort(inventoryCopy, aimInventory, maxValue);
            if (this.sortItems.size() == 0 && !this.doneBefore) {
                this.finishSort = false;
                if (this.infoMsgs.getValue()) {
                    PistonCrystal.printDebug("Inventory already sorted...", true);
                }
                if (this.closeAfter.getValue()) {
                    AutoGear.mc.field_71439_g.func_71053_j();
                }
            }
            else {
                this.finishSort = true;
                this.stepNow = 0;
            }
            this.openedBefore = true;
        }
        else if (this.finishSort) {
            int j = 0;
            while (j < this.switchForTick.getValue()) {
                if (this.sortItems.size() != 0) {
                    final int slotChange = this.sortItems.get(this.stepNow++);
                    AutoGear.mc.field_71442_b.func_187098_a(AutoGear.mc.field_71439_g.field_71070_bA.field_75152_c, slotChange, 0, ClickType.PICKUP, (EntityPlayer)AutoGear.mc.field_71439_g);
                }
                if (this.stepNow == this.sortItems.size()) {
                    if (this.confirmSort.getValue() && !this.doneBefore) {
                        this.openedBefore = false;
                        this.finishSort = false;
                        this.doneBefore = true;
                        this.checkLastItem();
                        return;
                    }
                    this.finishSort = false;
                    if (this.infoMsgs.getValue()) {
                        PistonCrystal.printDebug("Inventory sorted", false);
                    }
                    this.checkLastItem();
                    this.doneBefore = false;
                    if (this.closeAfter.getValue()) {
                        AutoGear.mc.field_71439_g.func_71053_j();
                    }
                }
                else {
                    ++j;
                }
            }
        }
    }
    
    private void checkLastItem() {
        if (this.sortItems.size() != 0) {
            final int slotChange = this.sortItems.get(this.sortItems.size() - 1);
            if (((ItemStack)AutoGear.mc.field_71439_g.field_71070_bA.func_75138_a().get(slotChange)).func_190926_b()) {
                AutoGear.mc.field_71442_b.func_187098_a(0, slotChange, 0, ClickType.PICKUP, (EntityPlayer)AutoGear.mc.field_71439_g);
            }
        }
    }
    
    private ArrayList<Integer> getInventorySort(final HashMap<Integer, String> copyInventory, final HashMap<Integer, String> planInventoryCopy, final int startValues) {
        final ArrayList<Integer> planMove = new ArrayList<Integer>();
        final HashMap<String, Integer> nItemsCopy = new HashMap<String, Integer>();
        for (final String value : planInventoryCopy.values()) {
            if (nItemsCopy.containsKey(value)) {
                nItemsCopy.put(value, nItemsCopy.get(value) + 1);
            }
            else {
                nItemsCopy.put(value, 1);
            }
        }
        final ArrayList<Integer> ignoreValues = new ArrayList<Integer>();
        final int[] listValue = new int[planInventoryCopy.size()];
        int id = 0;
        for (final int idx : planInventoryCopy.keySet()) {
            listValue[id++] = idx;
        }
        for (final int item : listValue) {
            if (copyInventory.get(item).equals(planInventoryCopy.get(item))) {
                ignoreValues.add(item);
                nItemsCopy.put(planInventoryCopy.get(item), nItemsCopy.get(planInventoryCopy.get(item)) - 1);
                if (nItemsCopy.get(planInventoryCopy.get(item)) == 0) {
                    nItemsCopy.remove(planInventoryCopy.get(item));
                }
                planInventoryCopy.remove(item);
            }
        }
        String pickedItem = null;
        for (int i = startValues; i < startValues + copyInventory.size(); ++i) {
            if (!ignoreValues.contains(i)) {
                final String itemCheck = copyInventory.get(i);
                final Optional<Map.Entry<Integer, String>> momentAim = planInventoryCopy.entrySet().stream().filter(x -> x.getValue().equals(itemCheck)).findFirst();
                if (momentAim.isPresent()) {
                    if (pickedItem == null) {
                        planMove.add(i);
                    }
                    final int aimKey = momentAim.get().getKey();
                    planMove.add(aimKey);
                    if (pickedItem == null || !pickedItem.equals(itemCheck)) {
                        ignoreValues.add(aimKey);
                    }
                    nItemsCopy.put(itemCheck, nItemsCopy.get(itemCheck) - 1);
                    if (nItemsCopy.get(itemCheck) == 0) {
                        nItemsCopy.remove(itemCheck);
                    }
                    copyInventory.put(i, copyInventory.get(aimKey));
                    copyInventory.put(aimKey, itemCheck);
                    if (!copyInventory.get(aimKey).equals("minecraft:air0")) {
                        if (i >= startValues + copyInventory.size()) {
                            continue;
                        }
                        pickedItem = copyInventory.get(i);
                        --i;
                    }
                    else {
                        pickedItem = null;
                    }
                    planInventoryCopy.remove(aimKey);
                }
                else if (pickedItem != null) {
                    planMove.add(i);
                    copyInventory.put(i, pickedItem);
                    pickedItem = null;
                }
            }
        }
        if (planMove.size() != 0 && planMove.get(planMove.size() - 1).equals(planMove.get(planMove.size() - 2))) {
            planMove.remove(planMove.size() - 1);
        }
        final Object[] keyList = this.containerInv.keySet().toArray();
        for (int values = 0; values < keyList.length; ++values) {
            final int itemC = (int)keyList[values];
            if (nItemsCopy.containsKey(this.containerInv.get(itemC))) {
                final int start = planInventoryCopy.entrySet().stream().filter(x -> x.getValue().equals(this.containerInv.get(itemC))).findFirst().get().getKey();
                if (this.invasive.getValue() || ((ItemStack)AutoGear.mc.field_71439_g.field_71070_bA.func_75138_a().get(start)).func_190926_b()) {
                    planMove.add(start);
                    planMove.add(itemC);
                    planMove.add(start);
                    nItemsCopy.put(planInventoryCopy.get(start), nItemsCopy.get(planInventoryCopy.get(start)) - 1);
                    if (nItemsCopy.get(planInventoryCopy.get(start)) == 0) {
                        nItemsCopy.remove(planInventoryCopy.get(start));
                    }
                    planInventoryCopy.remove(start);
                }
            }
        }
        if (this.debugMode.getValue()) {
            for (final int valuePath : planMove) {
                PistonCrystal.printDebug(Integer.toString(valuePath), false);
            }
        }
        return planMove;
    }
    
    private HashMap<Integer, String> getInventoryCopy(final int startPoint) {
        final HashMap<Integer, String> output = new HashMap<Integer, String>();
        for (int sizeInventory = AutoGear.mc.field_71439_g.field_71071_by.field_70462_a.size(), i = 0; i < sizeInventory; ++i) {
            final int value = i + startPoint + ((i < 9) ? (sizeInventory - 9) : -9);
            final ItemStack item = (ItemStack)AutoGear.mc.field_71439_g.field_71070_bA.func_75138_a().get(value);
            output.put(value, Objects.requireNonNull(item.func_77973_b().getRegistryName()).toString() + item.func_77960_j());
        }
        return output;
    }
    
    private HashMap<Integer, String> getInventoryCopy(final int startPoint, final HashMap<Integer, String> inventory) {
        final HashMap<Integer, String> output = new HashMap<Integer, String>();
        final int sizeInventory = AutoGear.mc.field_71439_g.field_71071_by.field_70462_a.size();
        for (final int val : inventory.keySet()) {
            output.put(val + startPoint + ((val < 9) ? (sizeInventory - 9) : -9), inventory.get(val));
        }
        return output;
    }
}
