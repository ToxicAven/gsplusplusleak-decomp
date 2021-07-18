// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.gamesense.api.util.player.PlayerUtil;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemBlock;
import com.gamesense.api.util.world.combat.DamageUtil;
import net.minecraft.entity.item.EntityEnderCrystal;
import java.util.Iterator;
import net.minecraft.entity.Entity;
import net.minecraft.init.MobEffects;
import com.gamesense.api.util.player.InventoryUtil;
import java.util.function.ToIntFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Blocks;
import java.util.HashMap;
import net.minecraft.init.Items;
import java.util.Arrays;
import net.minecraft.block.Block;
import java.util.Map;
import net.minecraft.item.Item;
import java.util.ArrayList;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Offhand", category = Category.Combat)
public class OffHand extends Module
{
    ModeSetting defaultItem;
    ModeSetting nonDefaultItem;
    ModeSetting noPlayerItem;
    ModeSetting potionChoose;
    IntegerSetting healthSwitch;
    IntegerSetting tickDelay;
    IntegerSetting fallDistance;
    IntegerSetting maxSwitchPerSecond;
    DoubleSetting biasDamage;
    DoubleSetting playerDistance;
    BooleanSetting pickObby;
    BooleanSetting pickObbyShift;
    BooleanSetting crystObby;
    BooleanSetting rightGap;
    BooleanSetting shiftPot;
    BooleanSetting swordCheck;
    BooleanSetting swordCrystal;
    BooleanSetting pickCrystal;
    BooleanSetting fallDistanceBol;
    BooleanSetting crystalCheck;
    BooleanSetting noHotBar;
    BooleanSetting onlyHotBar;
    BooleanSetting antiWeakness;
    BooleanSetting hotBarTotem;
    int prevSlot;
    int tickWaited;
    int totems;
    boolean returnBack;
    boolean stepChanging;
    boolean firstChange;
    private static String forceItem;
    private final ArrayList<Long> switchDone;
    private final ArrayList<Item> ignoreNoSword;
    Map<String, Item> allowedItemsItem;
    Map<String, Block> allowedItemsBlock;
    
    public OffHand() {
        this.defaultItem = this.registerMode("Default", Arrays.asList("Totem", "Crystal", "Gapple", "Plates", "Obby", "Pot", "Exp"), "Totem");
        this.nonDefaultItem = this.registerMode("Non Default", Arrays.asList("Totem", "Crystal", "Gapple", "Obby", "Pot", "Exp", "Plates", "String", "Skull"), "Crystal");
        this.noPlayerItem = this.registerMode("No Player", Arrays.asList("Totem", "Crystal", "Gapple", "Plates", "Obby", "Pot", "Exp"), "Gapple");
        this.potionChoose = this.registerMode("Potion", Arrays.asList("first", "strength", "swiftness"), "first");
        this.healthSwitch = this.registerInteger("Health Switch", 14, 0, 36);
        this.tickDelay = this.registerInteger("Tick Delay", 0, 0, 20);
        this.fallDistance = this.registerInteger("Fall Distance", 12, 0, 30);
        this.maxSwitchPerSecond = this.registerInteger("Max Switch", 6, 2, 10);
        this.biasDamage = this.registerDouble("Bias Damage", 1.0, 0.0, 3.0);
        this.playerDistance = this.registerDouble("Player Distance", 0.0, 0.0, 30.0);
        this.pickObby = this.registerBoolean("Pick Obby", false);
        this.pickObbyShift = this.registerBoolean("Pick Obby On Shift", false);
        this.crystObby = this.registerBoolean("Cryst Shift Obby", false);
        this.rightGap = this.registerBoolean("Right Click Gap", false);
        this.shiftPot = this.registerBoolean("Shift Pot", false);
        this.swordCheck = this.registerBoolean("Only Sword", true);
        this.swordCrystal = this.registerBoolean("Sword Crystal", false);
        this.pickCrystal = this.registerBoolean("Pick Crystal", false);
        this.fallDistanceBol = this.registerBoolean("Fall Distance", true);
        this.crystalCheck = this.registerBoolean("Crystal Check", false);
        this.noHotBar = this.registerBoolean("No HotBar", false);
        this.onlyHotBar = this.registerBoolean("Only HotBar", false);
        this.antiWeakness = this.registerBoolean("AntiWeakness", false);
        this.hotBarTotem = this.registerBoolean("HotBar Totem", false);
        this.switchDone = new ArrayList<Long>();
        this.ignoreNoSword = new ArrayList<Item>() {
            {
                this.add(Items.field_151153_ao);
                this.add(Items.field_151062_by);
                this.add((Item)Items.field_151031_f);
                this.add((Item)Items.field_151068_bn);
            }
        };
        this.allowedItemsItem = new HashMap<String, Item>() {
            {
                this.put("Totem", Items.field_190929_cY);
                this.put("Crystal", Items.field_185158_cP);
                this.put("Gapple", Items.field_151153_ao);
                this.put("Pot", (Item)Items.field_151068_bn);
                this.put("Exp", Items.field_151062_by);
                this.put("String", Items.field_151007_F);
            }
        };
        this.allowedItemsBlock = new HashMap<String, Block>() {
            {
                this.put("Plates", Blocks.field_150452_aw);
                this.put("Skull", (Block)Blocks.field_150465_bP);
                this.put("Obby", Blocks.field_150343_Z);
                this.put("Anvil", Blocks.field_150467_bQ);
                this.put("EChest", Blocks.field_150477_bB);
            }
        };
    }
    
    public static void requestItems(final int want) {
        switch (want) {
            case 0: {
                OffHand.forceItem = "Obby";
                break;
            }
            case 1: {
                OffHand.forceItem = "Skull";
                break;
            }
            case 2: {
                OffHand.forceItem = "EChest";
                break;
            }
        }
    }
    
    public static void removeItem(final int want) {
        String check = "";
        switch (want) {
            case 0: {
                check = "Obby";
                break;
            }
            case 1: {
                check = "Skull";
                break;
            }
            case 2: {
                check = "EChest";
                break;
            }
        }
        if (OffHand.forceItem.equals(check)) {
            OffHand.forceItem = "";
        }
    }
    
    public void onEnable() {
        this.firstChange = true;
        OffHand.forceItem = "";
        this.returnBack = false;
    }
    
    public void onDisable() {
        OffHand.forceItem = "";
    }
    
    @Override
    public void onUpdate() {
        if (OffHand.mc.field_71462_r instanceof GuiContainer && !(OffHand.mc.field_71462_r instanceof GuiInventory)) {
            return;
        }
        if (this.stepChanging) {
            if (this.tickWaited++ < this.tickDelay.getValue()) {
                return;
            }
            this.tickWaited = 0;
            this.stepChanging = false;
            OffHand.mc.field_71442_b.func_187098_a(0, 45, 0, ClickType.PICKUP, (EntityPlayer)OffHand.mc.field_71439_g);
            this.switchDone.add(System.currentTimeMillis());
        }
        this.totems = OffHand.mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> itemStack.func_77973_b() == Items.field_190929_cY).mapToInt(ItemStack::func_190916_E).sum();
        if (this.returnBack) {
            if (this.tickWaited++ < this.tickDelay.getValue()) {
                return;
            }
            this.changeBack();
        }
        final String itemCheck = this.getItem();
        if (this.offHandSame(itemCheck)) {
            boolean done = false;
            if (this.hotBarTotem.getValue() && itemCheck.equals("Totem")) {
                done = this.switchItemTotemHot();
            }
            if (!done) {
                this.switchItemNormal(itemCheck);
            }
        }
    }
    
    private void changeBack() {
        if (this.prevSlot == -1 || !OffHand.mc.field_71439_g.field_71071_by.func_70301_a(this.prevSlot).func_190926_b()) {
            this.prevSlot = this.findEmptySlot();
        }
        if (this.prevSlot != -1) {
            OffHand.mc.field_71442_b.func_187098_a(0, (this.prevSlot < 9) ? (this.prevSlot + 36) : this.prevSlot, 0, ClickType.PICKUP, (EntityPlayer)OffHand.mc.field_71439_g);
        }
        else {
            PistonCrystal.printDebug("Your inventory is full. the item that was on your offhand is going to be dropped. Open your inventory and choose where to put it", true);
        }
        this.returnBack = false;
        this.tickWaited = 0;
    }
    
    private boolean switchItemTotemHot() {
        final int slot = InventoryUtil.findTotemSlot(0, 8);
        if (slot != -1) {
            if (OffHand.mc.field_71439_g.field_71071_by.field_70461_c != slot) {
                OffHand.mc.field_71439_g.field_71071_by.field_70461_c = slot;
            }
            return true;
        }
        return false;
    }
    
    private void switchItemNormal(final String itemCheck) {
        final int t = this.getInventorySlot(itemCheck);
        if (t == -1) {
            return;
        }
        if (!itemCheck.equals("Totem") && this.canSwitch()) {
            return;
        }
        this.toOffHand(t);
    }
    
    private String getItem() {
        String itemCheck = "";
        boolean normalOffHand = true;
        if ((this.fallDistanceBol.getValue() && OffHand.mc.field_71439_g.field_70143_R >= this.fallDistance.getValue() && OffHand.mc.field_71439_g.field_70167_r != OffHand.mc.field_71439_g.field_70163_u && !OffHand.mc.field_71439_g.func_184613_cA()) || (this.crystalCheck.getValue() && this.crystalDamage())) {
            normalOffHand = false;
            itemCheck = "Totem";
        }
        if (!OffHand.forceItem.equals("")) {
            itemCheck = OffHand.forceItem;
            normalOffHand = false;
        }
        final Item mainHandItem = OffHand.mc.field_71439_g.func_184614_ca().func_77973_b();
        if (normalOffHand && ((this.crystObby.getValue() && OffHand.mc.field_71474_y.field_74311_E.func_151470_d() && mainHandItem == Items.field_185158_cP) || (this.pickObby.getValue() && mainHandItem == Items.field_151046_w && (!this.pickObbyShift.getValue() || OffHand.mc.field_71474_y.field_74311_E.func_151470_d())))) {
            itemCheck = "Obby";
            normalOffHand = false;
        }
        if (this.swordCrystal.getValue() && mainHandItem == Items.field_151048_u) {
            itemCheck = "Crystal";
            normalOffHand = false;
        }
        if (this.pickCrystal.getValue() && mainHandItem == Items.field_151046_w) {
            itemCheck = "Crystal";
            normalOffHand = false;
        }
        if (normalOffHand && OffHand.mc.field_71474_y.field_74313_G.func_151470_d() && (!this.swordCheck.getValue() || mainHandItem == Items.field_151048_u)) {
            if (OffHand.mc.field_71474_y.field_74311_E.func_151470_d()) {
                if (this.shiftPot.getValue()) {
                    itemCheck = "Pot";
                    normalOffHand = false;
                }
            }
            else if (this.rightGap.getValue() && !this.ignoreNoSword.contains(mainHandItem)) {
                itemCheck = "Gapple";
                normalOffHand = false;
            }
        }
        if (normalOffHand && this.antiWeakness.getValue() && OffHand.mc.field_71439_g.func_70644_a(MobEffects.field_76437_t)) {
            normalOffHand = false;
            itemCheck = "Crystal";
        }
        if (normalOffHand && !this.nearPlayer()) {
            normalOffHand = false;
            itemCheck = this.noPlayerItem.getValue();
        }
        itemCheck = this.getItemToCheck(itemCheck);
        return itemCheck;
    }
    
    private boolean canSwitch() {
        final long now = System.currentTimeMillis();
        for (int i = 0; i < this.switchDone.size() && now - this.switchDone.get(i) > 1000L; ++i) {
            this.switchDone.remove(i);
        }
        if (this.switchDone.size() / 2 >= this.maxSwitchPerSecond.getValue()) {
            return true;
        }
        this.switchDone.add(now);
        return false;
    }
    
    private boolean nearPlayer() {
        if (this.playerDistance.getValue().intValue() == 0) {
            return true;
        }
        for (final EntityPlayer pl : OffHand.mc.field_71441_e.field_73010_i) {
            if (pl != OffHand.mc.field_71439_g && OffHand.mc.field_71439_g.func_70032_d((Entity)pl) < this.playerDistance.getValue()) {
                return true;
            }
        }
        return false;
    }
    
    private boolean crystalDamage() {
        for (final Entity t : OffHand.mc.field_71441_e.field_72996_f) {
            if (t instanceof EntityEnderCrystal && OffHand.mc.field_71439_g.func_70032_d(t) <= 12.0f && DamageUtil.calculateDamage(t.field_70165_t, t.field_70163_u, t.field_70161_v, (Entity)OffHand.mc.field_71439_g) * this.biasDamage.getValue() >= OffHand.mc.field_71439_g.func_110143_aJ()) {
                return true;
            }
        }
        return false;
    }
    
    private int findEmptySlot() {
        for (int i = 35; i > -1; --i) {
            if (OffHand.mc.field_71439_g.field_71071_by.func_70301_a(i).func_190926_b()) {
                return i;
            }
        }
        return -1;
    }
    
    private boolean offHandSame(final String itemCheck) {
        final Item offHandItem = OffHand.mc.field_71439_g.func_184592_cb().func_77973_b();
        if (!this.allowedItemsBlock.containsKey(itemCheck)) {
            final Item item = this.allowedItemsItem.get(itemCheck);
            return item != offHandItem;
        }
        final Block item2 = this.allowedItemsBlock.get(itemCheck);
        if (offHandItem instanceof ItemBlock) {
            return ((ItemBlock)offHandItem).func_179223_d() != item2;
        }
        return !(offHandItem instanceof ItemSkull) || item2 != Blocks.field_150465_bP || true;
    }
    
    private String getItemToCheck(final String str) {
        return (PlayerUtil.getHealth() > this.healthSwitch.getValue()) ? (str.equals("") ? this.nonDefaultItem.getValue() : str) : this.defaultItem.getValue();
    }
    
    private int getInventorySlot(final String itemName) {
        boolean blockBool = false;
        Object item;
        if (this.allowedItemsItem.containsKey(itemName)) {
            item = this.allowedItemsItem.get(itemName);
        }
        else {
            item = this.allowedItemsBlock.get(itemName);
            blockBool = true;
        }
        if (!this.firstChange && this.prevSlot != -1) {
            final int res = this.isCorrect(this.prevSlot, blockBool, item, itemName);
            if (res != -1) {
                return res;
            }
        }
        for (int i = this.onlyHotBar.getValue() ? 8 : 35; i > (this.noHotBar.getValue() ? 9 : -1); --i) {
            final int res = this.isCorrect(i, blockBool, item, itemName);
            if (res != -1) {
                return res;
            }
        }
        return -1;
    }
    
    private int isCorrect(final int i, final boolean blockBool, final Object item, final String itemName) {
        final Item temp = OffHand.mc.field_71439_g.field_71071_by.func_70301_a(i).func_77973_b();
        if (blockBool) {
            if (temp instanceof ItemBlock) {
                if (((ItemBlock)temp).func_179223_d() == item) {
                    return i;
                }
            }
            else if (temp instanceof ItemSkull && item == Blocks.field_150465_bP) {
                return i;
            }
        }
        else if (item == temp) {
            if (itemName.equals("Pot") && !this.potionChoose.getValue().equalsIgnoreCase("first") && !OffHand.mc.field_71439_g.field_71071_by.func_70301_a(i).field_77990_d.toString().split(":")[2].contains(((Setting<CharSequence>)this.potionChoose).getValue())) {
                return -1;
            }
            return i;
        }
        return -1;
    }
    
    private void toOffHand(final int t) {
        if (!OffHand.mc.field_71439_g.func_184592_cb().func_190926_b()) {
            if (this.firstChange) {
                this.prevSlot = t;
            }
            this.returnBack = true;
            this.firstChange = !this.firstChange;
        }
        else {
            this.prevSlot = -1;
        }
        OffHand.mc.field_71442_b.func_187098_a(0, (t < 9) ? (t + 36) : t, 0, ClickType.PICKUP, (EntityPlayer)OffHand.mc.field_71439_g);
        this.stepChanging = true;
        this.tickWaited = 0;
    }
    
    @Override
    public String getHudInfo() {
        return "[" + ChatFormatting.WHITE + this.totems + ChatFormatting.GRAY + "]";
    }
}
