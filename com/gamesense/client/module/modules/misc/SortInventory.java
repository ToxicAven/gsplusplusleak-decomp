// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import java.util.Objects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.item.ItemStack;
import java.util.Iterator;
import java.util.Optional;
import java.util.Map;
import net.minecraft.inventory.ClickType;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.gui.inventory.GuiInventory;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import com.gamesense.client.command.commands.AutoGearCommand;
import java.util.ArrayList;
import java.util.HashMap;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "SortInventory", category = Category.Misc)
public class SortInventory extends Module
{
    IntegerSetting tickDelay;
    IntegerSetting switchForTick;
    BooleanSetting confirmSort;
    BooleanSetting instaSort;
    BooleanSetting closeAfter;
    BooleanSetting infoMsgs;
    BooleanSetting finishCheck;
    BooleanSetting debugMode;
    private HashMap<Integer, String> planInventory;
    private HashMap<String, Integer> nItems;
    private ArrayList<Integer> sortItems;
    private int delayTimeTicks;
    private int stepNow;
    private boolean openedBefore;
    private boolean finishSort;
    private boolean doneBefore;
    private boolean lastCheck;
    private int lastItem;
    
    public SortInventory() {
        this.tickDelay = this.registerInteger("Tick Delay", 0, 0, 20);
        this.switchForTick = this.registerInteger("Switch Per Tick", 1, 1, 100);
        this.confirmSort = this.registerBoolean("Confirm Sort", true);
        this.instaSort = this.registerBoolean("Insta Sort", false);
        this.closeAfter = this.registerBoolean("Close After", false);
        this.infoMsgs = this.registerBoolean("Info Msgs", true);
        this.finishCheck = this.registerBoolean("Finish Check", true);
        this.debugMode = this.registerBoolean("Debug Mode", false);
        this.planInventory = new HashMap<Integer, String>();
        this.nItems = new HashMap<String, Integer>();
        this.sortItems = new ArrayList<Integer>();
        this.lastCheck = false;
        this.lastItem = -1;
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
        this.nItems = new HashMap<String, Integer>();
        for (int i = 0; i < inventoryDivided.length; ++i) {
            if (!inventoryDivided[i].contains("air")) {
                this.planInventory.put(i, inventoryDivided[i]);
                if (this.nItems.containsKey(inventoryDivided[i])) {
                    this.nItems.put(inventoryDivided[i], this.nItems.get(inventoryDivided[i]) + 1);
                }
                else {
                    this.nItems.put(inventoryDivided[i], 1);
                }
            }
        }
        this.delayTimeTicks = 0;
        final boolean b = false;
        this.doneBefore = b;
        this.openedBefore = b;
        if (this.instaSort.getValue()) {
            SortInventory.mc.func_147108_a((GuiScreen)new GuiInventory((EntityPlayer)SortInventory.mc.field_71439_g));
        }
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
        if (this.finishCheck.getValue() && this.lastCheck) {
            if (this.lastItem != -1 && SortInventory.mc.field_71439_g.field_71071_by.func_70301_a(this.lastItem).func_190926_b()) {
                SortInventory.mc.field_71442_b.func_187098_a(0, (this.lastItem < 9) ? (this.lastItem + 36) : this.lastItem, 0, ClickType.PICKUP, (EntityPlayer)SortInventory.mc.field_71439_g);
            }
            this.lastCheck = false;
        }
        if (this.planInventory.size() == 0) {
            this.disable();
        }
        if (SortInventory.mc.field_71462_r instanceof GuiInventory) {
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
            this.sortItems = this.getInventorySort();
            if (this.sortItems.size() == 0 && !this.doneBefore) {
                this.finishSort = false;
                if (this.infoMsgs.getValue()) {
                    PistonCrystal.printDebug("Inventory arleady sorted...", true);
                }
                if (this.instaSort.getValue() || this.closeAfter.getValue()) {
                    SortInventory.mc.field_71439_g.func_71053_j();
                    if (this.instaSort.getValue()) {
                        this.disable();
                    }
                }
            }
            else {
                this.finishSort = true;
                this.stepNow = 0;
            }
            this.openedBefore = true;
        }
        else if (this.finishSort) {
            int i = 0;
            while (i < this.switchForTick.getValue()) {
                if (this.sortItems.size() != 0) {
                    final int slotChange = this.sortItems.get(this.stepNow++);
                    SortInventory.mc.field_71442_b.func_187098_a(0, (slotChange < 9) ? (slotChange + 36) : slotChange, 0, ClickType.PICKUP, (EntityPlayer)SortInventory.mc.field_71439_g);
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
                    if (this.instaSort.getValue() || this.closeAfter.getValue()) {
                        SortInventory.mc.field_71439_g.func_71053_j();
                        if (this.instaSort.getValue()) {
                            this.disable();
                        }
                    }
                }
                else {
                    ++i;
                }
            }
        }
    }
    
    private void checkLastItem() {
        if (this.sortItems.size() != 0) {
            final int slotChange = this.sortItems.get(this.sortItems.size() - 1);
            if (SortInventory.mc.field_71439_g.field_71071_by.func_70301_a(slotChange).func_190926_b()) {
                SortInventory.mc.field_71442_b.func_187098_a(0, (slotChange < 9) ? (slotChange + 36) : slotChange, 0, ClickType.PICKUP, (EntityPlayer)SortInventory.mc.field_71439_g);
            }
            this.lastItem = slotChange;
            this.lastCheck = true;
        }
    }
    
    private ArrayList<Integer> getInventorySort() {
        final ArrayList<Integer> planMove = new ArrayList<Integer>();
        final ArrayList<String> copyInventory = this.getInventoryCopy();
        final HashMap<Integer, String> planInventoryCopy = (HashMap<Integer, String>)this.planInventory.clone();
        final HashMap<String, Integer> nItemsCopy = (HashMap<String, Integer>)this.nItems.clone();
        final ArrayList<Integer> ignoreValues = new ArrayList<Integer>();
        for (int i = 0; i < this.planInventory.size(); ++i) {
            final int value = (int)this.planInventory.keySet().toArray()[i];
            if (copyInventory.get(value).equals(planInventoryCopy.get(value))) {
                ignoreValues.add(value);
                nItemsCopy.put(planInventoryCopy.get(value), nItemsCopy.get(planInventoryCopy.get(value)) - 1);
                if (nItemsCopy.get(planInventoryCopy.get(value)) == 0) {
                    nItemsCopy.remove(planInventoryCopy.get(value));
                }
                planInventoryCopy.remove(value);
            }
        }
        String pickedItem = null;
        for (int j = 0; j < copyInventory.size(); ++j) {
            if (!ignoreValues.contains(j)) {
                final String itemCheck = copyInventory.get(j);
                final Optional<Map.Entry<Integer, String>> momentAim = planInventoryCopy.entrySet().stream().filter(x -> x.getValue().equals(itemCheck)).findFirst();
                if (momentAim.isPresent()) {
                    if (pickedItem == null) {
                        planMove.add(j);
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
                    copyInventory.set(j, copyInventory.get(aimKey));
                    copyInventory.set(aimKey, itemCheck);
                    if (!copyInventory.get(aimKey).equals("minecraft:air0")) {
                        if (j >= copyInventory.size()) {
                            continue;
                        }
                        pickedItem = copyInventory.get(j);
                        --j;
                    }
                    else {
                        pickedItem = null;
                    }
                    planInventoryCopy.remove(aimKey);
                }
                else if (pickedItem != null) {
                    if (planMove.get(planMove.size() - 1) != j) {
                        planMove.add(j);
                        copyInventory.set(j, pickedItem);
                    }
                    pickedItem = null;
                }
            }
        }
        if (planMove.size() != 0 && planMove.get(planMove.size() - 1).equals(planMove.get(planMove.size() - 2))) {
            planMove.remove(planMove.size() - 1);
        }
        if (this.debugMode.getValue()) {
            for (final int valuePath : planMove) {
                PistonCrystal.printDebug(Integer.toString(valuePath), false);
            }
        }
        return planMove;
    }
    
    private ArrayList<String> getInventoryCopy() {
        final ArrayList<String> output = new ArrayList<String>();
        for (final ItemStack i : SortInventory.mc.field_71439_g.field_71071_by.field_70462_a) {
            output.add(Objects.requireNonNull(i.func_77973_b().getRegistryName()).toString() + i.func_77960_j());
        }
        return output;
    }
}
