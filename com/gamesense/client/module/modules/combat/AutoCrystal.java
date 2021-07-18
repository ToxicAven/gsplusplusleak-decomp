// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.gamesense.api.util.world.combat.DamageUtil;
import com.gamesense.api.util.render.RenderUtil;
import com.gamesense.api.event.events.RenderEvent;
import net.minecraft.util.math.RayTraceResult;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.AutoGG;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3i;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.item.ItemTool;
import net.minecraft.item.Item;
import com.gamesense.api.util.player.InventoryUtil;
import net.minecraft.item.ItemSword;
import net.minecraft.init.MobEffects;
import com.gamesense.api.util.world.combat.ac.ACUtil;
import java.util.TreeSet;
import java.util.Comparator;
import java.util.Iterator;
import net.minecraft.network.Packet;
import net.minecraft.util.math.Vec2f;
import net.minecraft.entity.item.EntityEnderCrystal;
import java.util.Collection;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.network.play.server.SPacketSoundEffect;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.event.Phase;
import java.util.function.Predicate;
import com.gamesense.api.util.world.combat.ac.ACHelper;
import com.gamesense.api.util.world.combat.ac.ACSettings;
import net.minecraft.entity.player.EntityPlayer;
import com.gamesense.api.util.world.combat.ac.PlayerInfo;
import java.util.ArrayList;
import com.gamesense.api.util.render.GSColor;
import java.util.Arrays;
import com.gamesense.api.event.events.PacketEvent;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.api.util.world.combat.ac.CrystalInfo;
import java.util.List;
import net.minecraft.util.math.Vec3d;
import com.gamesense.api.util.misc.Timer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.entity.Entity;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "AutoCrystal", category = Category.Combat, priority = 100)
public class AutoCrystal extends Module
{
    ModeSetting breakMode;
    ModeSetting handBreak;
    ModeSetting breakType;
    ModeSetting crystalPriority;
    BooleanSetting breakCrystal;
    BooleanSetting placeCrystal;
    IntegerSetting attackSpeed;
    public DoubleSetting breakRange;
    public DoubleSetting placeRange;
    DoubleSetting wallsRange;
    DoubleSetting enemyRange;
    BooleanSetting antiWeakness;
    BooleanSetting antiSuicide;
    IntegerSetting antiSuicideValue;
    BooleanSetting autoSwitch;
    BooleanSetting noGapSwitch;
    public BooleanSetting endCrystalMode;
    BooleanSetting cancelCrystal;
    DoubleSetting minDmg;
    DoubleSetting minBreakDmg;
    DoubleSetting maxSelfDmg;
    IntegerSetting facePlaceValue;
    IntegerSetting armourFacePlace;
    DoubleSetting minFacePlaceDmg;
    BooleanSetting rotate;
    BooleanSetting raytrace;
    BooleanSetting showDamage;
    ModeSetting hudDisplay;
    ColorSetting color;
    BooleanSetting wait;
    IntegerSetting timeout;
    IntegerSetting maxTargets;
    private boolean switchCooldown;
    public boolean isAttacking;
    public static boolean stopAC;
    private Entity renderEntity;
    private BlockPos render;
    Timer timer;
    private Vec3d lastHitVec;
    private boolean rotating;
    public List<CrystalInfo.PlaceInfo> targets;
    private boolean finished;
    @EventHandler
    private final Listener<TickEvent.ClientTickEvent> onUpdate;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener;
    
    public AutoCrystal() {
        this.breakMode = this.registerMode("Target", Arrays.asList("All", "Smart", "Own"), "All");
        this.handBreak = this.registerMode("Hand", Arrays.asList("Main", "Offhand", "Both"), "Main");
        this.breakType = this.registerMode("Type", Arrays.asList("Swing", "Packet"), "Swing");
        this.crystalPriority = this.registerMode("Prioritise", Arrays.asList("Damage", "Closest", "Health"), "Damage");
        this.breakCrystal = this.registerBoolean("Break", true);
        this.placeCrystal = this.registerBoolean("Place", true);
        this.attackSpeed = this.registerInteger("Attack Speed", 16, 0, 20);
        this.breakRange = this.registerDouble("Hit Range", 4.4, 0.0, 10.0);
        this.placeRange = this.registerDouble("Place Range", 4.4, 0.0, 6.0);
        this.wallsRange = this.registerDouble("Walls Range", 3.5, 0.0, 10.0);
        this.enemyRange = this.registerDouble("Enemy Range", 6.0, 0.0, 16.0);
        this.antiWeakness = this.registerBoolean("Anti Weakness", true);
        this.antiSuicide = this.registerBoolean("Anti Suicide", true);
        this.antiSuicideValue = this.registerInteger("Min Health", 14, 1, 36);
        this.autoSwitch = this.registerBoolean("Switch", true);
        this.noGapSwitch = this.registerBoolean("No Gap Switch", false);
        this.endCrystalMode = this.registerBoolean("1.13 Place", false);
        this.cancelCrystal = this.registerBoolean("Cancel Crystal", false);
        this.minDmg = this.registerDouble("Min Damage", 5.0, 0.0, 36.0);
        this.minBreakDmg = this.registerDouble("Min Break Dmg", 5.0, 0.0, 36.0);
        this.maxSelfDmg = this.registerDouble("Max Self Dmg", 10.0, 1.0, 36.0);
        this.facePlaceValue = this.registerInteger("FacePlace HP", 8, 0, 36);
        this.armourFacePlace = this.registerInteger("Armour Health%", 20, 0, 100);
        this.minFacePlaceDmg = this.registerDouble("FacePlace Dmg", 2.0, 0.0, 10.0);
        this.rotate = this.registerBoolean("Rotate", true);
        this.raytrace = this.registerBoolean("Raytrace", false);
        this.showDamage = this.registerBoolean("Render Dmg", true);
        this.hudDisplay = this.registerMode("HUD", Arrays.asList("Mode", "Target", "None"), "Mode");
        this.color = this.registerColor("Color", new GSColor(0, 255, 0, 50));
        this.wait = this.registerBoolean("Force Wait", true);
        this.timeout = this.registerInteger("Timeout (ms)", 10, 1, 50);
        this.maxTargets = this.registerInteger("Max Targets", 2, 1, 5);
        this.switchCooldown = false;
        this.isAttacking = false;
        this.timer = new Timer();
        this.lastHitVec = Vec3d.field_186680_a;
        this.rotating = false;
        this.targets = new ArrayList<CrystalInfo.PlaceInfo>();
        this.finished = false;
        PlayerInfo player;
        ACSettings settings;
        float armourPercent;
        double enemyDistance;
        this.onUpdate = new Listener<TickEvent.ClientTickEvent>(event -> {
            if (AutoCrystal.mc.field_71439_g == null || AutoCrystal.mc.field_71441_e == null || AutoCrystal.mc.field_71439_g.field_70128_L) {
                return;
            }
            else if (AutoCrystal.stopAC) {
                return;
            }
            else {
                player = new PlayerInfo((EntityPlayer)AutoCrystal.mc.field_71439_g, false);
                if (this.antiSuicide.getValue() && player.health <= this.antiSuicideValue.getValue()) {
                    return;
                }
                else {
                    settings = new ACSettings(this.breakCrystal.getValue(), this.placeCrystal.getValue(), this.enemyRange.getValue(), this.breakRange.getValue(), this.wallsRange.getValue(), this.placeRange.getValue(), this.minDmg.getValue(), this.minBreakDmg.getValue(), this.minFacePlaceDmg.getValue(), this.maxSelfDmg.getValue(), this.facePlaceValue.getValue(), this.antiSuicide.getValue(), this.endCrystalMode.getValue(), this.breakMode.getValue(), this.crystalPriority.getValue(), player, AutoCrystal.mc.field_71439_g.func_174791_d());
                    armourPercent = this.armourFacePlace.getValue() / 100.0f;
                    enemyDistance = this.enemyRange.getValue() + this.placeRange.getValue();
                    ACHelper.INSTANCE.recalculateValues(settings, player, armourPercent, enemyDistance);
                    if (event.phase == TickEvent.Phase.START) {
                        this.collectTargetFinder();
                    }
                    else if (this.finished) {
                        this.startTargetFinder();
                        this.finished = false;
                    }
                    this.targets.removeIf(placeInfo -> placeInfo.target.entity.field_70128_L || placeInfo.target.entity.func_110143_aJ() == 0.0f);
                    if (!this.breakCrystal(settings) && !this.placeCrystal(settings)) {
                        this.rotating = false;
                        this.isAttacking = false;
                        this.render = null;
                        this.renderEntity = null;
                    }
                    return;
                }
            }
        }, (Predicate<TickEvent.ClientTickEvent>[])new Predicate[0]);
        Vec2f rotation;
        PlayerPacket packet;
        this.onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
            if (event.getPhase() != Phase.PRE || !this.rotating) {
                return;
            }
            else {
                rotation = RotationUtil.getRotationTo(this.lastHitVec);
                packet = new PlayerPacket(this, rotation);
                PlayerPacketManager.INSTANCE.addPacket(packet);
                return;
            }
        }, (Predicate<OnUpdateWalkingPlayerEvent>[])new Predicate[0]);
        final Packet packet2;
        SPacketSoundEffect packetSoundEffect;
        final Iterator<Entity> iterator;
        Entity entity;
        this.packetReceiveListener = new Listener<PacketEvent.Receive>(event -> {
            packet2 = event.getPacket();
            if (packet2 instanceof SPacketSoundEffect) {
                packetSoundEffect = (SPacketSoundEffect)packet2;
                if (packetSoundEffect.func_186977_b() == SoundCategory.BLOCKS && packetSoundEffect.func_186978_a() == SoundEvents.field_187539_bB) {
                    new ArrayList<Entity>(AutoCrystal.mc.field_71441_e.field_72996_f).iterator();
                    while (iterator.hasNext()) {
                        entity = iterator.next();
                        if (entity instanceof EntityEnderCrystal && entity.func_70092_e(packetSoundEffect.func_149207_d(), packetSoundEffect.func_149211_e(), packetSoundEffect.func_149210_f()) <= 36.0) {
                            entity.func_70106_y();
                        }
                    }
                }
            }
        }, (Predicate<PacketEvent.Receive>[])new Predicate[0]);
    }
    
    public boolean breakCrystal(final ACSettings settings) {
        if (this.breakCrystal.getValue() && this.targets.size() > 0) {
            List<CrystalInfo.PlaceInfo> currentTargets;
            if (this.targets.size() < this.maxTargets.getValue()) {
                currentTargets = new ArrayList<CrystalInfo.PlaceInfo>(this.targets);
            }
            else {
                currentTargets = new ArrayList<CrystalInfo.PlaceInfo>(this.targets.subList(0, this.maxTargets.getValue()));
            }
            final List<EntityEnderCrystal> crystals = ACHelper.INSTANCE.getTargetableCrystals();
            final String crystalPriorityValue = this.crystalPriority.getValue();
            TreeSet<CrystalInfo.BreakInfo> possibleCrystals;
            if (crystalPriorityValue.equalsIgnoreCase("Health")) {
                possibleCrystals = new TreeSet<CrystalInfo.BreakInfo>(Comparator.comparingDouble(i -> -i.target.health));
            }
            else if (crystalPriorityValue.equalsIgnoreCase("Closest")) {
                possibleCrystals = new TreeSet<CrystalInfo.BreakInfo>(Comparator.comparingDouble(i -> -AutoCrystal.mc.field_71439_g.func_70068_e((Entity)i.target.entity)));
            }
            else {
                possibleCrystals = new TreeSet<CrystalInfo.BreakInfo>(Comparator.comparingDouble(i -> i.damage));
            }
            for (final CrystalInfo.PlaceInfo currentTarget : currentTargets) {
                final CrystalInfo.BreakInfo breakInfo = ACUtil.calculateBestBreakable(settings, new PlayerInfo(currentTarget.target.entity, currentTarget.target.lowArmour), crystals);
                if (breakInfo != null) {
                    possibleCrystals.add(breakInfo);
                }
            }
            if (possibleCrystals.size() != 0) {
                final EntityEnderCrystal crystal = possibleCrystals.last().crystal;
                if (AutoCrystal.mc.field_71439_g.func_70685_l((Entity)crystal) || AutoCrystal.mc.field_71439_g.func_70032_d((Entity)crystal) < this.wallsRange.getValue()) {
                    if (this.antiWeakness.getValue() && AutoCrystal.mc.field_71439_g.func_70644_a(MobEffects.field_76437_t)) {
                        if (!this.isAttacking) {
                            this.isAttacking = true;
                        }
                        final int newSlot = InventoryUtil.findFirstItemSlot((Class<? extends Item>)ItemSword.class, 0, 8);
                        if (newSlot == -1) {
                            InventoryUtil.findFirstItemSlot((Class<? extends Item>)ItemTool.class, 0, 8);
                        }
                        if (newSlot != -1) {
                            AutoCrystal.mc.field_71439_g.field_71071_by.field_70461_c = newSlot;
                            this.switchCooldown = true;
                        }
                    }
                    if (this.timer.getTimePassed() / 50L >= 20 - this.attackSpeed.getValue()) {
                        this.timer.reset();
                        this.rotating = this.rotate.getValue();
                        this.lastHitVec = crystal.func_174791_d();
                        this.swingArm();
                        if (this.breakType.getValue().equalsIgnoreCase("Swing")) {
                            AutoCrystal.mc.field_71442_b.func_78764_a((EntityPlayer)AutoCrystal.mc.field_71439_g, (Entity)crystal);
                        }
                        else {
                            AutoCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketUseEntity((Entity)crystal));
                        }
                        if (this.cancelCrystal.getValue()) {
                            crystal.func_70106_y();
                            AutoCrystal.mc.field_71441_e.func_73022_a();
                            AutoCrystal.mc.field_71441_e.func_72910_y();
                        }
                    }
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean placeCrystal(final ACSettings settings) {
        int crystalSlot = (AutoCrystal.mc.field_71439_g.func_184614_ca().func_77973_b() == Items.field_185158_cP) ? AutoCrystal.mc.field_71439_g.field_71071_by.field_70461_c : -1;
        if (crystalSlot == -1) {
            crystalSlot = InventoryUtil.findFirstItemSlot((Class<? extends Item>)ItemEndCrystal.class, 0, 8);
        }
        boolean offhand = false;
        if (AutoCrystal.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_185158_cP) {
            offhand = true;
        }
        else if (crystalSlot == -1) {
            return false;
        }
        if (!this.placeCrystal.getValue()) {
            return false;
        }
        List<CrystalInfo.PlaceInfo> currentTargets;
        if (this.targets.size() < this.maxTargets.getValue()) {
            currentTargets = new ArrayList<CrystalInfo.PlaceInfo>(this.targets);
        }
        else {
            currentTargets = this.targets.subList(0, this.maxTargets.getValue());
        }
        final List<BlockPos> placements = ACHelper.INSTANCE.getPossiblePlacements();
        final String crystalPriorityValue = this.crystalPriority.getValue();
        TreeSet<CrystalInfo.PlaceInfo> possiblePlacements;
        if (crystalPriorityValue.equalsIgnoreCase("Health")) {
            possiblePlacements = new TreeSet<CrystalInfo.PlaceInfo>(Comparator.comparingDouble(i -> -i.target.health));
        }
        else if (crystalPriorityValue.equalsIgnoreCase("Closest")) {
            possiblePlacements = new TreeSet<CrystalInfo.PlaceInfo>(Comparator.comparingDouble(i -> -AutoCrystal.mc.field_71439_g.func_70068_e((Entity)i.target.entity)));
        }
        else {
            possiblePlacements = new TreeSet<CrystalInfo.PlaceInfo>(Comparator.comparingDouble(i -> i.damage));
        }
        for (final CrystalInfo.PlaceInfo currentTarget : currentTargets) {
            final CrystalInfo.PlaceInfo placeInfo = ACUtil.calculateBestPlacement(settings, new PlayerInfo(currentTarget.target.entity, currentTarget.target.lowArmour), placements);
            if (placeInfo != null) {
                possiblePlacements.add(placeInfo);
            }
        }
        if (possiblePlacements.size() == 0) {
            return false;
        }
        final CrystalInfo.PlaceInfo crystal = possiblePlacements.last();
        this.render = crystal.crystal;
        this.renderEntity = (Entity)crystal.target.entity;
        if (!offhand && AutoCrystal.mc.field_71439_g.field_71071_by.field_70461_c != crystalSlot) {
            if (this.autoSwitch.getValue() && (!this.noGapSwitch.getValue() || AutoCrystal.mc.field_71439_g.func_184614_ca().func_77973_b() != Items.field_151153_ao)) {
                AutoCrystal.mc.field_71439_g.field_71071_by.field_70461_c = crystalSlot;
                this.rotating = false;
                this.switchCooldown = true;
            }
            return false;
        }
        EnumFacing enumFacing = null;
        if (this.raytrace.getValue()) {
            final RayTraceResult result = AutoCrystal.mc.field_71441_e.func_72933_a(new Vec3d(AutoCrystal.mc.field_71439_g.field_70165_t, AutoCrystal.mc.field_71439_g.field_70163_u + AutoCrystal.mc.field_71439_g.func_70047_e(), AutoCrystal.mc.field_71439_g.field_70161_v), new Vec3d(crystal.crystal.func_177958_n() + 0.5, crystal.crystal.func_177956_o() - 0.5, crystal.crystal.func_177952_p() + 0.5));
            if (result == null || result.field_178784_b == null) {
                this.render = null;
                return false;
            }
            enumFacing = result.field_178784_b;
        }
        if (this.switchCooldown) {
            return this.switchCooldown = false;
        }
        ACHelper.INSTANCE.onPlaceCrystal(crystal.crystal);
        this.rotating = this.rotate.getValue();
        this.lastHitVec = new Vec3d((Vec3i)crystal.crystal).func_72441_c(0.5, 0.5, 0.5);
        AutoCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketAnimation(offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND));
        if (this.raytrace.getValue() && enumFacing != null) {
            AutoCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(crystal.crystal, enumFacing, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        }
        else if (crystal.crystal.func_177956_o() == 255) {
            AutoCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(crystal.crystal, EnumFacing.DOWN, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        }
        else {
            AutoCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(crystal.crystal, EnumFacing.UP, offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.0f, 0.0f, 0.0f));
        }
        if (ModuleManager.isModuleEnabled(AutoGG.class)) {
            AutoGG.INSTANCE.addTargetedPlayer(this.renderEntity.func_70005_c_());
        }
        return true;
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (this.render != null) {
            RenderUtil.drawBox(this.render, 1.0, new GSColor(this.color.getValue(), 50), 63);
            RenderUtil.drawBoundingBox(this.render, 1.0, 1.0f, new GSColor(this.color.getValue(), 255));
        }
        if (this.showDamage.getValue() && this.render != null && this.renderEntity != null) {
            final String[] damageText = { String.format("%.1f", DamageUtil.calculateDamage(this.render.func_177958_n() + 0.5, this.render.func_177956_o() + 1.0, this.render.func_177952_p() + 0.5, this.renderEntity)) };
            RenderUtil.drawNametag(this.render.func_177958_n() + 0.5, this.render.func_177956_o() + 0.5, this.render.func_177952_p() + 0.5, damageText, new GSColor(255, 255, 255), 1);
        }
    }
    
    private void startTargetFinder() {
        final long timeoutTime = System.currentTimeMillis() + this.timeout.getValue();
        ACHelper.INSTANCE.startCalculations(timeoutTime);
    }
    
    private void collectTargetFinder() {
        final List<CrystalInfo.PlaceInfo> output = ACHelper.INSTANCE.getOutput(this.wait.getValue());
        if (output != null) {
            this.finished = true;
            if (output.size() > 0) {
                this.targets = output;
            }
        }
        else {
            this.finished = false;
        }
    }
    
    private void swingArm() {
        final String s = this.handBreak.getValue();
        switch (s) {
            case "Both": {
                AutoCrystal.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                AutoCrystal.mc.field_71439_g.func_184609_a(EnumHand.OFF_HAND);
                break;
            }
            case "Offhand": {
                AutoCrystal.mc.field_71439_g.func_184609_a(EnumHand.OFF_HAND);
                break;
            }
            default: {
                AutoCrystal.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                break;
            }
        }
    }
    
    public void onEnable() {
        ACHelper.INSTANCE.onEnable();
    }
    
    public void onDisable() {
        ACHelper.INSTANCE.onDisable();
        this.render = null;
        this.renderEntity = null;
        this.rotating = false;
        this.targets.clear();
    }
    
    @Override
    public String getHudInfo() {
        String t = "";
        if (this.hudDisplay.getValue().equalsIgnoreCase("Mode")) {
            t = "[" + ChatFormatting.WHITE + this.breakMode.getValue() + ChatFormatting.GRAY + "]";
        }
        else if (this.hudDisplay.getValue().equalsIgnoreCase("Target")) {
            if (this.renderEntity == null) {
                t = "[" + ChatFormatting.WHITE + "None" + ChatFormatting.GRAY + "]";
            }
            else {
                t = "[" + ChatFormatting.WHITE + this.renderEntity.func_70005_c_() + ChatFormatting.GRAY + "]";
            }
        }
        return t;
    }
    
    static {
        AutoCrystal.stopAC = false;
    }
}
