// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.combat;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.passive.EntityAnimal;
import com.gamesense.api.util.player.social.SocialManager;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemAxe;
import java.util.Iterator;
import java.util.List;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item;
import com.gamesense.api.util.player.InventoryUtil;
import net.minecraft.item.ItemSword;
import net.minecraft.util.math.Vec2f;
import java.util.Optional;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.util.misc.Pair;
import java.util.Comparator;
import net.minecraft.entity.Entity;
import com.gamesense.api.util.world.EntityUtil;
import net.minecraft.entity.EntityLivingBase;
import java.util.function.Predicate;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "KillAura", category = Category.Combat)
public class KillAura extends Module
{
    BooleanSetting players;
    BooleanSetting hostileMobs;
    BooleanSetting passiveMobs;
    ModeSetting itemUsed;
    ModeSetting enemyPriority;
    BooleanSetting swordPriority;
    BooleanSetting caCheck;
    BooleanSetting criticals;
    BooleanSetting rotation;
    BooleanSetting autoSwitch;
    DoubleSetting switchHealth;
    DoubleSetting range;
    private boolean isAttacking;
    @EventHandler
    private final Listener<PacketEvent.Send> listener;
    
    public KillAura() {
        this.players = this.registerBoolean("Players", true);
        this.hostileMobs = this.registerBoolean("Monsters", false);
        this.passiveMobs = this.registerBoolean("Animals", false);
        this.itemUsed = this.registerMode("Item used", Arrays.asList("Sword", "Axe", "Both", "All"), "Sword");
        this.enemyPriority = this.registerMode("Enemy Priority", Arrays.asList("Closest", "Health"), "Closest");
        this.swordPriority = this.registerBoolean("Prioritise Sword", true);
        this.caCheck = this.registerBoolean("AC Check", false);
        this.criticals = this.registerBoolean("Criticals", true);
        this.rotation = this.registerBoolean("Rotation", true);
        this.autoSwitch = this.registerBoolean("Switch", false);
        this.switchHealth = this.registerDouble("Min Switch Health", 0.0, 0.0, 20.0);
        this.range = this.registerDouble("Range", 5.0, 0.0, 10.0);
        this.isAttacking = false;
        this.listener = new Listener<PacketEvent.Send>(event -> {
            if (event.getPacket() instanceof CPacketUseEntity && this.criticals.getValue() && ((CPacketUseEntity)event.getPacket()).func_149565_c() == CPacketUseEntity.Action.ATTACK && KillAura.mc.field_71439_g.field_70122_E && this.isAttacking) {
                KillAura.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(KillAura.mc.field_71439_g.field_70165_t, KillAura.mc.field_71439_g.field_70163_u + 0.10000000149011612, KillAura.mc.field_71439_g.field_70161_v, false));
                KillAura.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position(KillAura.mc.field_71439_g.field_70165_t, KillAura.mc.field_71439_g.field_70163_u, KillAura.mc.field_71439_g.field_70161_v, false));
            }
        }, (Predicate<PacketEvent.Send>[])new Predicate[0]);
    }
    
    @Override
    public void onUpdate() {
        if (KillAura.mc.field_71439_g == null || !KillAura.mc.field_71439_g.func_70089_S()) {
            return;
        }
        final double rangeSq = this.range.getValue() * this.range.getValue();
        final Optional<Entity> optionalTarget = (Optional<Entity>)KillAura.mc.field_71441_e.field_72996_f.stream().filter(entity -> entity instanceof EntityLivingBase).filter(entity -> !EntityUtil.basicChecksEntity(entity)).filter(entity -> KillAura.mc.field_71439_g.func_70068_e(entity) <= rangeSq).filter(this::attackCheck).min(Comparator.comparing(e -> this.enemyPriority.getValue().equals("Closest") ? KillAura.mc.field_71439_g.func_70068_e((Entity)e) : e.func_110143_aJ()));
        final boolean sword = this.itemUsed.getValue().equalsIgnoreCase("Sword");
        final boolean axe = this.itemUsed.getValue().equalsIgnoreCase("Axe");
        final boolean both = this.itemUsed.getValue().equalsIgnoreCase("Both");
        final boolean all = this.itemUsed.getValue().equalsIgnoreCase("All");
        if (optionalTarget.isPresent()) {
            Pair<Float, Integer> newSlot = new Pair<Float, Integer>(0.0f, -1);
            if (this.autoSwitch.getValue() && KillAura.mc.field_71439_g.func_110143_aJ() + KillAura.mc.field_71439_g.func_110139_bj() >= this.switchHealth.getValue()) {
                if (sword || both || all) {
                    newSlot = this.findSwordSlot();
                }
                if ((axe || both || all) && (!this.swordPriority.getValue() || newSlot.getValue() == -1)) {
                    final Pair<Float, Integer> possibleSlot = this.findAxeSlot();
                    if (possibleSlot.getKey() > newSlot.getKey()) {
                        newSlot = possibleSlot;
                    }
                }
            }
            final int temp = KillAura.mc.field_71439_g.field_71071_by.field_70461_c;
            if (newSlot.getValue() != -1) {
                KillAura.mc.field_71439_g.field_71071_by.field_70461_c = newSlot.getValue();
            }
            if (this.shouldAttack(sword, axe, both, all)) {
                final Entity target = optionalTarget.get();
                if (this.rotation.getValue()) {
                    final Vec2f rotation = RotationUtil.getRotationTo(target.func_174813_aQ());
                    final PlayerPacket packet = new PlayerPacket(this, rotation);
                    PlayerPacketManager.INSTANCE.addPacket(packet);
                }
                if (ModuleManager.isModuleEnabled(AutoGG.class)) {
                    AutoGG.INSTANCE.addTargetedPlayer(target.func_70005_c_());
                }
                this.attack(target);
            }
            else {
                KillAura.mc.field_71439_g.field_71071_by.field_70461_c = temp;
            }
        }
    }
    
    private Pair<Float, Integer> findSwordSlot() {
        final List<Integer> items = InventoryUtil.findAllItemSlots((Class<? extends Item>)ItemSword.class);
        final List<ItemStack> inventory = (List<ItemStack>)KillAura.mc.field_71439_g.field_71071_by.field_70462_a;
        float bestModifier = 0.0f;
        int correspondingSlot = -1;
        for (final Integer integer : items) {
            if (integer > 8) {
                continue;
            }
            final ItemStack stack = inventory.get(integer);
            final float modifier = (EnchantmentHelper.func_152377_a(stack, EnumCreatureAttribute.UNDEFINED) + 1.0f) * ((ItemSword)stack.func_77973_b()).func_150931_i();
            if (modifier <= bestModifier) {
                continue;
            }
            bestModifier = modifier;
            correspondingSlot = integer;
        }
        return new Pair<Float, Integer>(bestModifier, correspondingSlot);
    }
    
    private Pair<Float, Integer> findAxeSlot() {
        final List<Integer> items = InventoryUtil.findAllItemSlots((Class<? extends Item>)ItemAxe.class);
        final List<ItemStack> inventory = (List<ItemStack>)KillAura.mc.field_71439_g.field_71071_by.field_70462_a;
        float bestModifier = 0.0f;
        int correspondingSlot = -1;
        for (final Integer integer : items) {
            if (integer > 8) {
                continue;
            }
            final ItemStack stack = inventory.get(integer);
            final float modifier = (EnchantmentHelper.func_152377_a(stack, EnumCreatureAttribute.UNDEFINED) + 1.0f) * ((ItemAxe)stack.func_77973_b()).field_77865_bY;
            if (modifier <= bestModifier) {
                continue;
            }
            bestModifier = modifier;
            correspondingSlot = integer;
        }
        return new Pair<Float, Integer>(bestModifier, correspondingSlot);
    }
    
    private boolean shouldAttack(final boolean sword, final boolean axe, final boolean both, final boolean all) {
        final Item item = KillAura.mc.field_71439_g.func_184614_ca().func_77973_b();
        return (all || ((sword || both) && item instanceof ItemSword) || ((axe || both) && item instanceof ItemAxe)) && (!this.caCheck.getValue() || !ModuleManager.getModule(AutoCrystal.class).isAttacking);
    }
    
    private void attack(final Entity e) {
        if (KillAura.mc.field_71439_g.func_184825_o(0.0f) >= 1.0f) {
            this.isAttacking = true;
            KillAura.mc.field_71442_b.func_78764_a((EntityPlayer)KillAura.mc.field_71439_g, e);
            KillAura.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
            this.isAttacking = false;
        }
    }
    
    private boolean attackCheck(final Entity entity) {
        if (this.players.getValue() && entity instanceof EntityPlayer && !SocialManager.isFriend(entity.func_70005_c_()) && ((EntityPlayer)entity).func_110143_aJ() > 0.0f) {
            return true;
        }
        if (this.passiveMobs.getValue() && entity instanceof EntityAnimal) {
            return !(entity instanceof EntityTameable);
        }
        return this.hostileMobs.getValue() && entity instanceof EntityMob;
    }
}
