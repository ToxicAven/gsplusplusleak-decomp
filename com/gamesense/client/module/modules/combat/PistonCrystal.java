// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.combat;

import com.gamesense.api.setting.Setting;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.api.util.world.EntityUtil;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.block.BlockRedstoneTorch;
import net.minecraft.block.BlockObsidian;
import com.gamesense.api.util.player.PlacementUtil;
import org.apache.logging.log4j.Logger;
import net.minecraft.block.Block;
import org.apache.logging.log4j.LogManager;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.EnumHand;
import net.minecraft.network.play.client.CPacketUseEntity;
import com.gamesense.api.util.world.combat.CrystalUtil;
import java.util.Iterator;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.BlockPos;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.client.module.modules.misc.AutoGG;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import com.gamesense.client.module.ModuleManager;
import java.util.List;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.player.SpoofRotationUtil;
import net.minecraft.util.math.Vec2f;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.event.Phase;
import java.util.function.Predicate;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.network.play.server.SPacketSoundEffect;
import java.util.Arrays;
import net.minecraft.util.EnumFacing;
import java.util.ArrayList;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import net.minecraft.util.math.Vec3d;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.PacketEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.player.EntityPlayer;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "PistonCrystal", category = Category.Combat, priority = 999)
public class PistonCrystal extends Module
{
    ModeSetting breakType;
    ModeSetting placeMode;
    ModeSetting target;
    DoubleSetting enemyRange;
    DoubleSetting crystalDeltaBreak;
    IntegerSetting crystalPlaceTry;
    IntegerSetting blocksPerTick;
    IntegerSetting minHealth;
    IntegerSetting supBlocksDelay;
    IntegerSetting preRotationDelay;
    IntegerSetting afterRotationDelay;
    IntegerSetting startDelay;
    IntegerSetting pistonDelay;
    IntegerSetting crystalDelay;
    IntegerSetting redstoneDelay;
    IntegerSetting midHitDelay;
    IntegerSetting hitDelay;
    IntegerSetting stuckDetector;
    IntegerSetting maxYincr;
    BooleanSetting blockPlayer;
    BooleanSetting rotate;
    BooleanSetting confirmBreak;
    BooleanSetting confirmPlace;
    BooleanSetting allowCheapMode;
    BooleanSetting betterPlacement;
    BooleanSetting bypassObsidian;
    BooleanSetting antiWeakness;
    BooleanSetting debugMode;
    BooleanSetting speedMeter;
    BooleanSetting packetReducer;
    BooleanSetting preRotation;
    BooleanSetting forceRotation;
    BooleanSetting chatMsg;
    private boolean noMaterials;
    private boolean hasMoved;
    private boolean isSneaking;
    private boolean yUnder;
    private boolean isHole;
    private boolean enoughSpace;
    private boolean redstoneBlockMode;
    private boolean fastModeActive;
    private boolean broken;
    private boolean brokenCrystalBug;
    private boolean brokenRedstoneTorch;
    private boolean stoppedCa;
    private boolean deadPl;
    private boolean rotationPlayerMoved;
    private boolean preRotationBol;
    private boolean minHp;
    private boolean itemCrystal;
    private int oldSlot;
    private int stage;
    private int delayTimeTicks;
    private int stuck;
    private int hitTryTick;
    private int round;
    private int nCrystal;
    private int redstoneTickDelay;
    private int preRotationTick;
    private int afterRotationTick;
    private int placeTry;
    private long startTime;
    private long endTime;
    private int[] slot_mat;
    private int[] delayTable;
    private int[] meCoordsInt;
    private int[] enemyCoordsInt;
    private double[] enemyCoordsDouble;
    private structureTemp toPlace;
    int[][] disp_surblock;
    Double[][] sur_block;
    private EntityPlayer aimTarget;
    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener;
    int lenTable;
    Vec3d lastHitVec;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
    private final ArrayList<EnumFacing> exd;
    boolean redstoneAbovePiston;
    
    public PistonCrystal() {
        this.breakType = this.registerMode("Type", Arrays.asList("Swing", "Packet"), "Swing");
        this.placeMode = this.registerMode("Place", Arrays.asList("Torch", "Block", "Both"), "Torch");
        this.target = this.registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest");
        this.enemyRange = this.registerDouble("Range", 4.9, 0.0, 6.0);
        this.crystalDeltaBreak = this.registerDouble("Center Break", 0.1, 0.0, 0.5);
        this.crystalPlaceTry = this.registerInteger("Crystal Place Try", 15, 2, 30);
        this.blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 20);
        this.minHealth = this.registerInteger("Min Health", 8, 0, 20);
        this.supBlocksDelay = this.registerInteger("Surround Delay", 4, 0, 20);
        this.preRotationDelay = this.registerInteger("Pre Rotation Delay", 0, 0, 20);
        this.afterRotationDelay = this.registerInteger("After Rotation Delay", 0, 0, 20);
        this.startDelay = this.registerInteger("Start Delay", 4, 0, 20);
        this.pistonDelay = this.registerInteger("Piston Delay", 2, 0, 20);
        this.crystalDelay = this.registerInteger("Crystal Delay", 2, 0, 20);
        this.redstoneDelay = this.registerInteger("Redstone Delay", 0, 0, 20);
        this.midHitDelay = this.registerInteger("Mid Hit Delay", 5, 0, 20);
        this.hitDelay = this.registerInteger("Hit Delay", 2, 0, 20);
        this.stuckDetector = this.registerInteger("Stuck Check", 35, 0, 200);
        this.maxYincr = this.registerInteger("Max Y", 3, 0, 5);
        this.blockPlayer = this.registerBoolean("Trap Player", true);
        this.rotate = this.registerBoolean("Rotate", false);
        this.confirmBreak = this.registerBoolean("No Glitch Break", true);
        this.confirmPlace = this.registerBoolean("No Glitch Place", true);
        this.allowCheapMode = this.registerBoolean("Cheap Mode", false);
        this.betterPlacement = this.registerBoolean("Better Place", true);
        this.bypassObsidian = this.registerBoolean("Bypass Obsidian", false);
        this.antiWeakness = this.registerBoolean("Anti Weakness", false);
        this.debugMode = this.registerBoolean("Debug Mode", false);
        this.speedMeter = this.registerBoolean("Speed Meter", false);
        this.packetReducer = this.registerBoolean("Packet Reducer", false);
        this.preRotation = this.registerBoolean("Pre Rotation", false);
        this.forceRotation = this.registerBoolean("Force Rotation", false);
        this.chatMsg = this.registerBoolean("Chat Msgs", true);
        this.noMaterials = false;
        this.hasMoved = false;
        this.isSneaking = false;
        this.yUnder = false;
        this.isHole = true;
        this.enoughSpace = true;
        this.redstoneBlockMode = false;
        this.fastModeActive = false;
        this.preRotationBol = false;
        this.oldSlot = -1;
        this.stuck = 0;
        this.round = 0;
        this.disp_surblock = new int[][] { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 0, 1 }, { 0, 0, -1 } };
        this.sur_block = new Double[4][3];
        SPacketSoundEffect packet;
        this.packetReceiveListener = new Listener<PacketEvent.Receive>(event -> {
            if (event.getPacket() instanceof SPacketSoundEffect) {
                packet = (SPacketSoundEffect)event.getPacket();
                if (packet.func_186977_b() == SoundCategory.BLOCKS && packet.func_186978_a() == SoundEvents.field_187539_bB && (int)packet.func_149207_d() == this.enemyCoordsInt[0] && (int)packet.func_149210_f() == this.enemyCoordsInt[2]) {
                    this.stage = 1;
                }
            }
            return;
        }, (Predicate<PacketEvent.Receive>[])new Predicate[0]);
        Vec2f rotation;
        PlayerPacket packet2;
        this.onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
            if (event.getPhase() != Phase.PRE || !this.rotate.getValue() || this.lastHitVec == null || !this.forceRotation.getValue()) {
                return;
            }
            else {
                rotation = RotationUtil.getRotationTo(this.lastHitVec);
                packet2 = new PlayerPacket(this, rotation);
                PlayerPacketManager.INSTANCE.addPacket(packet2);
                return;
            }
        }, (Predicate<OnUpdateWalkingPlayerEvent>[])new Predicate[0]);
        this.exd = new ArrayList<EnumFacing>() {
            {
                this.add(EnumFacing.DOWN);
            }
        };
    }
    
    public void onEnable() {
        SpoofRotationUtil.ROTATION_UTIL.onEnable();
        this.initValues();
        if (this.getAimTarget()) {
            return;
        }
        this.playerChecks();
    }
    
    private boolean getAimTarget() {
        if (this.target.getValue().equals("Nearest")) {
            this.aimTarget = PlayerUtil.findClosestTarget(this.enemyRange.getValue(), this.aimTarget);
        }
        else {
            this.aimTarget = PlayerUtil.findLookingPlayer(this.enemyRange.getValue());
        }
        if (this.aimTarget == null || !this.target.getValue().equals("Looking")) {
            if (!this.target.getValue().equals("Looking") && this.aimTarget == null) {
                this.disable();
            }
            return this.aimTarget == null;
        }
        return false;
    }
    
    private void playerChecks() {
        if (this.getMaterialsSlot()) {
            if (this.is_in_hole()) {
                this.enemyCoordsDouble = new double[] { this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v };
                this.enemyCoordsInt = new int[] { (int)this.enemyCoordsDouble[0], (int)this.enemyCoordsDouble[1], (int)this.enemyCoordsDouble[2] };
                this.meCoordsInt = new int[] { (int)PistonCrystal.mc.field_71439_g.field_70165_t, (int)PistonCrystal.mc.field_71439_g.field_70163_u, (int)PistonCrystal.mc.field_71439_g.field_70161_v };
                this.antiAutoDestruction();
                this.enoughSpace = this.createStructure();
            }
            else {
                this.isHole = false;
            }
        }
        else {
            this.noMaterials = true;
        }
    }
    
    private void antiAutoDestruction() {
        if (this.redstoneBlockMode || this.rotate.getValue()) {
            this.betterPlacement.setValue(false);
        }
    }
    
    private void initValues() {
        this.preRotationBol = false;
        final int n = 0;
        this.afterRotationTick = n;
        this.preRotationTick = n;
        this.lastHitVec = null;
        this.aimTarget = null;
        this.delayTable = new int[] { this.startDelay.getValue(), this.supBlocksDelay.getValue(), this.pistonDelay.getValue(), this.crystalDelay.getValue(), this.hitDelay.getValue() };
        this.lenTable = this.delayTable.length;
        this.toPlace = new structureTemp(0.0, 0, null);
        final boolean b = true;
        this.minHp = b;
        this.isHole = b;
        final boolean b2 = false;
        this.fastModeActive = b2;
        this.redstoneBlockMode = b2;
        this.yUnder = b2;
        this.brokenRedstoneTorch = b2;
        this.brokenCrystalBug = b2;
        this.broken = b2;
        this.deadPl = b2;
        this.rotationPlayerMoved = b2;
        this.itemCrystal = b2;
        this.hasMoved = b2;
        this.slot_mat = new int[] { -1, -1, -1, -1, -1, -1 };
        final int stage = 0;
        this.stuck = stage;
        this.delayTimeTicks = stage;
        this.stage = stage;
        if (PistonCrystal.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        if (this.chatMsg.getValue()) {
            printDebug("PistonCrystal turned ON!", false);
        }
        this.oldSlot = PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c;
        this.stoppedCa = false;
        if (ModuleManager.isModuleEnabled(AutoCrystal.class)) {
            AutoCrystal.stopAC = true;
            this.stoppedCa = true;
        }
        if (this.debugMode.getValue() || this.speedMeter.getValue()) {
            printDebug("Started pistonCrystal n^" + ++this.round, false);
            this.startTime = System.currentTimeMillis();
            this.nCrystal = 0;
        }
    }
    
    public void onDisable() {
        SpoofRotationUtil.ROTATION_UTIL.onDisable();
        if (PistonCrystal.mc.field_71439_g == null) {
            return;
        }
        String output = "";
        String materialsNeeded = "";
        if (this.aimTarget == null) {
            output = "No target found...";
        }
        else if (this.yUnder) {
            output = String.format("Sorry but you cannot be 2+ blocks under the enemy or %d above...", ((Setting<Object>)this.maxYincr).getValue());
        }
        else if (this.noMaterials) {
            output = "No Materials Detected...";
            materialsNeeded = this.getMissingMaterials();
        }
        else if (!this.isHole) {
            output = "The enemy is not in a hole...";
        }
        else if (!this.enoughSpace) {
            output = "Not enough space...";
        }
        else if (this.hasMoved) {
            output = "Out of range...";
        }
        else if (this.deadPl) {
            output = "Enemy is dead, gg! ";
        }
        else if (this.rotationPlayerMoved) {
            output = "You cannot move from your hole if you have rotation on. ";
        }
        else if (!this.minHp) {
            output = "Your hp is low";
        }
        else if (this.itemCrystal) {
            output = "An item is where the crystal should be placed";
        }
        this.setDisabledMessage(output + "PistonCrystal turned OFF!");
        if (!materialsNeeded.equals("")) {
            this.setDisabledMessage("Materials missing:" + materialsNeeded);
        }
        if (this.stoppedCa) {
            AutoCrystal.stopAC = false;
            this.stoppedCa = false;
        }
        if (this.isSneaking) {
            PistonCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)PistonCrystal.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        if (this.oldSlot != PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c && this.oldSlot != -1) {
            PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            this.oldSlot = -1;
        }
        this.noMaterials = false;
        AutoCrystal.stopAC = false;
        if (this.debugMode.getValue() || this.speedMeter.getValue()) {
            printDebug("Ended pistonCrystal n^" + this.round, false);
        }
    }
    
    private String getMissingMaterials() {
        final StringBuilder output = new StringBuilder();
        if (this.slot_mat[0] == -1) {
            output.append(" Obsidian");
        }
        if (this.slot_mat[1] == -1) {
            output.append(" Piston");
        }
        if (this.slot_mat[2] == -1) {
            output.append(" Crystals");
        }
        if (this.slot_mat[3] == -1) {
            output.append(" Redstone");
        }
        if (this.antiWeakness.getValue() && this.slot_mat[4] == -1) {
            output.append(" Sword");
        }
        if (this.redstoneBlockMode && this.slot_mat[5] == -1) {
            output.append(" Pick");
        }
        return output.toString();
    }
    
    @Override
    public void onUpdate() {
        if (PistonCrystal.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        if (this.stage >= this.lenTable) {
            this.stage = 0;
        }
        if (this.delayTimeTicks < this.delayTable[this.stage]) {
            ++this.delayTimeTicks;
            return;
        }
        this.delayTimeTicks = 0;
        SpoofRotationUtil.ROTATION_UTIL.shouldSpoofAngles(true);
        if (this.enemyCoordsDouble == null || this.aimTarget == null) {
            if (this.aimTarget == null) {
                this.aimTarget = PlayerUtil.findLookingPlayer(this.enemyRange.getValue());
                if (this.aimTarget != null) {
                    if (ModuleManager.isModuleEnabled(AutoGG.class)) {
                        AutoGG.INSTANCE.addTargetedPlayer(this.aimTarget.func_70005_c_());
                    }
                    this.playerChecks();
                }
            }
            else {
                this.checkVariable();
            }
            return;
        }
        if (this.aimTarget.field_70128_L) {
            this.deadPl = true;
        }
        if (PlayerUtil.getHealth() <= this.minHealth.getValue()) {
            this.minHp = false;
        }
        if (this.rotate.getValue() && (int)PistonCrystal.mc.field_71439_g.field_70165_t != this.meCoordsInt[0] && (int)PistonCrystal.mc.field_71439_g.field_70161_v != this.meCoordsInt[2]) {
            this.rotationPlayerMoved = true;
        }
        if ((int)this.aimTarget.field_70165_t != (int)this.enemyCoordsDouble[0] || (int)this.aimTarget.field_70161_v != (int)this.enemyCoordsDouble[2]) {
            this.hasMoved = true;
        }
        if (this.checkVariable()) {
            return;
        }
        if (this.placeSupport()) {
            switch (this.stage) {
                case 1: {
                    this.placeTry = 0;
                    if (this.confirmBreak.getValue() && (this.checkCrystalPlaceExt(false) || this.checkCrystalPlaceIns() != null)) {
                        this.stage = 4;
                        break;
                    }
                    if (this.checkPistonPlace(false)) {
                        ++this.stage;
                        return;
                    }
                    if (this.preRotation.getValue() && !this.preRotationBol) {
                        this.placeBlockThings(this.stage, false, true, false);
                        if (this.preRotationTick != this.preRotationDelay.getValue()) {
                            ++this.preRotationTick;
                            break;
                        }
                        this.preRotationBol = true;
                        this.preRotationTick = 0;
                    }
                    if (this.afterRotationTick != this.afterRotationDelay.getValue()) {
                        ++this.afterRotationTick;
                        break;
                    }
                    if (this.debugMode.getValue()) {
                        printDebug("step 1", false);
                    }
                    if (this.fastModeActive || this.breakRedstone()) {
                        if (!this.fastModeActive || this.checkCrystalPlaceExt(true)) {
                            this.placeBlockThings(this.stage, false, false, false);
                        }
                        else {
                            this.stage = 2;
                            this.afterRotationTick = 0;
                        }
                    }
                    this.preRotationBol = false;
                    break;
                }
                case 2: {
                    if (this.placeTry++ >= this.crystalPlaceTry.getValue()) {
                        this.itemCrystal = true;
                        return;
                    }
                    if (this.afterRotationTick != this.afterRotationDelay.getValue()) {
                        ++this.afterRotationTick;
                        break;
                    }
                    if (!this.preRotation.getValue() || this.preRotationBol) {
                        if (this.debugMode.getValue()) {
                            printDebug("step 2", false);
                        }
                        if (this.fastModeActive || !this.confirmPlace.getValue() || this.checkPistonPlace(true)) {
                            this.placeBlockThings(this.stage, false, false, false);
                        }
                        this.redstoneTickDelay = 0;
                        this.preRotationBol = false;
                        break;
                    }
                    this.placeBlockThings(this.stage, false, true, false);
                    if (this.preRotationTick == this.preRotationDelay.getValue()) {
                        this.preRotationBol = true;
                        this.preRotationTick = 0;
                        break;
                    }
                    ++this.preRotationTick;
                    break;
                }
                case 3: {
                    if (this.afterRotationTick != this.afterRotationDelay.getValue()) {
                        ++this.afterRotationTick;
                        break;
                    }
                    if (this.preRotation.getValue() && !this.preRotationBol) {
                        this.placeBlockThings(this.stage, false, true, false);
                        if (this.preRotationTick == this.preRotationDelay.getValue()) {
                            this.preRotationBol = true;
                            this.preRotationTick = 0;
                            break;
                        }
                        ++this.preRotationTick;
                        break;
                    }
                    else {
                        if (this.redstoneTickDelay++ != this.redstoneDelay.getValue()) {
                            this.delayTimeTicks = 99;
                            break;
                        }
                        this.redstoneTickDelay = 0;
                        if (this.debugMode.getValue()) {
                            printDebug("step 3", false);
                        }
                        if (this.fastModeActive || !this.confirmPlace.getValue() || this.checkCrystalPlaceExt(true)) {
                            this.placeBlockThings(this.stage, true, false, false);
                            this.hitTryTick = 0;
                            if (this.fastModeActive && !this.checkPistonPlace(true)) {
                                this.stage = 1;
                            }
                        }
                        this.preRotationBol = false;
                        break;
                    }
                    break;
                }
                case 4: {
                    if (this.afterRotationTick != this.afterRotationDelay.getValue()) {
                        ++this.afterRotationTick;
                        break;
                    }
                    if (this.debugMode.getValue()) {
                        printDebug("step 4", false);
                    }
                    this.destroyCrystalAlgo();
                    this.preRotationBol = false;
                    if (this.confirmPlace.getValue() && this.checkRedstonePlace()) {
                        this.stage = 3;
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    private boolean checkRedstonePlace() {
        final BlockPos targetPosPist = this.compactBlockPos(3);
        return !BlockUtil.getBlock(targetPosPist.func_177958_n(), targetPosPist.func_177956_o(), targetPosPist.func_177952_p()).getRegistryName().toString().contains("redstone");
    }
    
    public void destroyCrystalAlgo() {
        final Entity crystal = this.checkCrystalPlaceIns();
        if (this.confirmBreak.getValue() && this.broken && crystal == null) {
            final int n = 0;
            this.stuck = n;
            this.stage = n;
            this.broken = false;
            if ((this.debugMode.getValue() || this.speedMeter.getValue()) && ++this.nCrystal == 3) {
                this.printTimeCrystals();
            }
        }
        if (crystal != null) {
            this.breakCrystalPiston(crystal);
            if (this.confirmBreak.getValue()) {
                this.broken = true;
            }
            else {
                final int n2 = 0;
                this.stuck = n2;
                this.stage = n2;
                if ((this.debugMode.getValue() || this.speedMeter.getValue()) && ++this.nCrystal == 3) {
                    this.printTimeCrystals();
                }
            }
        }
        else if (++this.stuck >= this.stuckDetector.getValue()) {
            if (!this.checkPistonPlace(true)) {
                final BlockPos crystPos = this.getTargetPos(this.toPlace.supportBlock + 1);
                printDebug(String.format("aim: %d %d", crystPos.func_177958_n(), crystPos.func_177952_p()), false);
                Entity crystalF = null;
                for (final Entity t : PistonCrystal.mc.field_71441_e.field_72996_f) {
                    if (t instanceof EntityEnderCrystal && (int)(t.field_70165_t - 0.5) == crystPos.func_177958_n() && (int)(t.field_70161_v - 0.5) == crystPos.func_177952_p()) {
                        crystalF = t;
                    }
                }
                if (this.confirmBreak.getValue() && this.brokenCrystalBug && crystalF == null) {
                    final int n3 = 0;
                    this.stuck = n3;
                    this.stage = n3;
                }
                if (crystalF != null) {
                    this.breakCrystalPiston(crystalF);
                    if (this.confirmBreak.getValue()) {
                        this.brokenCrystalBug = true;
                    }
                    else {
                        final int n4 = 0;
                        this.stuck = n4;
                        this.stage = n4;
                    }
                }
                printDebug("Stuck detected: piston not placed", true);
                return;
            }
            boolean found = false;
            for (final Entity t2 : PistonCrystal.mc.field_71441_e.field_72996_f) {
                if (t2 instanceof EntityEnderCrystal && (int)t2.field_70165_t == (int)this.toPlace.to_place.get(this.toPlace.supportBlock + 1).field_72450_a && (int)t2.field_70161_v == (int)this.toPlace.to_place.get(this.toPlace.supportBlock + 1).field_72449_c) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                final BlockPos offsetPosPist = new BlockPos((Vec3d)this.toPlace.to_place.get(this.toPlace.supportBlock + 2));
                final BlockPos pos = new BlockPos(this.aimTarget.func_174791_d()).func_177982_a(offsetPosPist.func_177958_n(), offsetPosPist.func_177956_o(), offsetPosPist.func_177952_p());
                if (this.confirmBreak.getValue() && this.brokenRedstoneTorch && BlockUtil.getBlock(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p()) instanceof BlockAir) {
                    this.stage = 1;
                    this.brokenRedstoneTorch = false;
                }
                else {
                    final EnumFacing side = BlockUtil.getPlaceableSide(pos);
                    if (side != null) {
                        this.breakRedstone();
                        if (this.confirmBreak.getValue()) {
                            this.brokenRedstoneTorch = true;
                        }
                        else {
                            this.stage = 1;
                            if ((this.debugMode.getValue() || this.speedMeter.getValue()) && ++this.nCrystal == 3) {
                                this.printTimeCrystals();
                            }
                        }
                        printDebug("Stuck detected: crystal not placed", true);
                    }
                }
            }
            else {
                boolean ext = false;
                for (final Entity t : PistonCrystal.mc.field_71441_e.field_72996_f) {
                    if (t instanceof EntityEnderCrystal && (int)t.field_70165_t == (int)this.toPlace.to_place.get(this.toPlace.supportBlock + 1).field_72450_a && (int)t.field_70161_v == (int)this.toPlace.to_place.get(this.toPlace.supportBlock + 1).field_72449_c) {
                        ext = true;
                        break;
                    }
                }
                if (this.confirmBreak.getValue() && this.brokenCrystalBug && !ext) {
                    final int n5 = 0;
                    this.stuck = n5;
                    this.stage = n5;
                    this.brokenCrystalBug = false;
                }
                if (ext) {
                    this.breakCrystalPiston(crystal);
                    if (this.confirmBreak.getValue()) {
                        this.brokenCrystalBug = true;
                    }
                    else {
                        final int n6 = 0;
                        this.stuck = n6;
                        this.stage = n6;
                    }
                    printDebug("Stuck detected: crystal is stuck in the moving piston", true);
                }
            }
        }
    }
    
    private void printTimeCrystals() {
        this.endTime = System.currentTimeMillis();
        printDebug("3 crystal, time took: " + (this.endTime - this.startTime), false);
        this.nCrystal = 0;
        this.startTime = System.currentTimeMillis();
    }
    
    private void breakCrystalPiston(final Entity crystal) {
        if (this.hitTryTick++ < this.midHitDelay.getValue()) {
            return;
        }
        this.hitTryTick = 0;
        if (this.antiWeakness.getValue()) {
            PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c = this.slot_mat[4];
        }
        if (this.rotate.getValue()) {
            SpoofRotationUtil.ROTATION_UTIL.lookAtPacket(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v, (EntityPlayer)PistonCrystal.mc.field_71439_g);
        }
        if (this.forceRotation.getValue()) {
            this.lastHitVec = new Vec3d(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v);
        }
        if (this.breakType.getValue().equals("Swing")) {
            CrystalUtil.breakCrystal(crystal);
        }
        else if (this.breakType.getValue().equals("Packet")) {
            try {
                PistonCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketUseEntity(crystal));
                PistonCrystal.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
            }
            catch (NullPointerException ex) {}
        }
        if (this.rotate.getValue()) {
            SpoofRotationUtil.ROTATION_UTIL.resetRotation();
        }
    }
    
    private boolean breakRedstone() {
        final BlockPos offsetPosPist = new BlockPos((Vec3d)this.toPlace.to_place.get(this.toPlace.supportBlock + 2));
        final BlockPos pos = new BlockPos(this.aimTarget.func_174791_d()).func_177982_a(offsetPosPist.func_177958_n(), offsetPosPist.func_177956_o(), offsetPosPist.func_177952_p());
        if (!(BlockUtil.getBlock(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p()) instanceof BlockAir)) {
            this.breakBlock(pos);
            return false;
        }
        return true;
    }
    
    private void breakBlock(final BlockPos pos) {
        if (this.redstoneBlockMode) {
            PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c = this.slot_mat[5];
        }
        final EnumFacing side = BlockUtil.getPlaceableSide(pos);
        if (side != null) {
            if (this.rotate.getValue()) {
                final BlockPos neighbour = pos.func_177972_a(side);
                final EnumFacing opposite = side.func_176734_d();
                final Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.0, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
                BlockUtil.faceVectorPacketInstant(hitVec, true);
                if (this.forceRotation.getValue()) {
                    this.lastHitVec = hitVec;
                }
            }
            PistonCrystal.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
            PistonCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side));
            PistonCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
        }
    }
    
    private boolean checkPistonPlace(final boolean decr) {
        final BlockPos targetPosPist = this.compactBlockPos(1);
        if (!(BlockUtil.getBlock(targetPosPist.func_177958_n(), targetPosPist.func_177956_o(), targetPosPist.func_177952_p()) instanceof BlockPistonBase)) {
            if (this.stage != 4 && decr) {
                --this.stage;
            }
            return false;
        }
        return true;
    }
    
    private boolean checkCrystalPlaceExt(final boolean decr) {
        for (final Entity t : PistonCrystal.mc.field_71441_e.field_72996_f) {
            if (t instanceof EntityEnderCrystal && (int)t.field_70165_t == (int)(this.aimTarget.field_70165_t + this.toPlace.to_place.get(this.toPlace.supportBlock + 1).field_72450_a) && (int)t.field_70161_v == (int)(this.aimTarget.field_70161_v + this.toPlace.to_place.get(this.toPlace.supportBlock + 1).field_72449_c)) {
                return true;
            }
        }
        if (decr) {
            --this.stage;
        }
        return false;
    }
    
    private Entity checkCrystalPlaceIns() {
        for (final Entity t : PistonCrystal.mc.field_71441_e.field_72996_f) {
            if (t instanceof EntityEnderCrystal && (((int)t.field_70165_t == this.enemyCoordsInt[0] && ((int)(t.field_70161_v - this.crystalDeltaBreak.getValue()) == this.enemyCoordsInt[2] || (int)(t.field_70161_v + this.crystalDeltaBreak.getValue()) == this.enemyCoordsInt[2])) || ((int)t.field_70161_v == this.enemyCoordsInt[2] && ((int)(t.field_70165_t - this.crystalDeltaBreak.getValue()) == this.enemyCoordsInt[0] || (int)(t.field_70165_t + this.crystalDeltaBreak.getValue()) == this.enemyCoordsInt[0])))) {
                return t;
            }
        }
        return null;
    }
    
    private boolean placeSupport() {
        int checksDone = 0;
        int blockDone = 0;
        if (this.toPlace.supportBlock > 0) {
            do {
                final BlockPos targetPos = this.getTargetPos(checksDone);
                if (BlockUtil.getBlock(targetPos) instanceof BlockAir) {
                    if (this.preRotation.getValue() && !this.preRotationBol) {
                        if (this.preRotationTick == 0) {
                            this.placeBlockConfirm(targetPos, 0, 0.0, 0.0, 1.0, false, true, false);
                        }
                        if (this.preRotationTick != this.preRotationDelay.getValue()) {
                            ++this.preRotationTick;
                            return false;
                        }
                        this.preRotationBol = true;
                        this.preRotationTick = 0;
                    }
                    if (this.packetReducer.getValue()) {
                        if (!this.placeBlockConfirm(targetPos, 0, 0.0, 0.0, 1.0, false, false, false)) {
                            continue;
                        }
                    }
                    else if (!this.placeBlock(targetPos, 0, 0.0, 0.0, 1.0, false)) {
                        continue;
                    }
                    this.preRotationBol = false;
                    if (++blockDone == this.blocksPerTick.getValue()) {
                        return false;
                    }
                    continue;
                }
            } while (++checksDone != this.toPlace.supportBlock);
        }
        this.stage = ((this.stage == 0) ? 1 : this.stage);
        return true;
    }
    
    private boolean placeBlock(final BlockPos pos, final int step, final double offsetX, final double offsetZ, final double offsetY, final boolean redstone) {
        final Block block = PistonCrystal.mc.field_71441_e.func_180495_p(pos).func_177230_c();
        EnumFacing side;
        if (redstone && this.redstoneAbovePiston) {
            side = BlockUtil.getPlaceableSideExlude(pos, this.exd);
        }
        else {
            side = BlockUtil.getPlaceableSide(pos);
        }
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = pos.func_177972_a(side);
        final EnumFacing opposite = side.func_176734_d();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5 + offsetX, offsetY, 0.5 + offsetZ).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        final Block neighbourBlock = PistonCrystal.mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
        try {
            if (this.slot_mat[step] != 11 && PistonCrystal.mc.field_71439_g.field_71071_by.func_70301_a(this.slot_mat[step]) == ItemStack.field_190927_a) {
                this.noMaterials = true;
                return false;
            }
            if (PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c != this.slot_mat[step]) {
                if (this.slot_mat[step] == -1) {
                    this.noMaterials = true;
                    return false;
                }
                PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c = ((this.slot_mat[step] == 11) ? PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c : this.slot_mat[step]);
            }
        }
        catch (Exception e) {
            printDebug("Fatal Error during the creation of the structure. Please, report this bug in the discor's server", true);
            final Logger LOGGER = LogManager.getLogger("GameSense");
            LOGGER.error("[PistonCrystal] error during the creation of the structure.");
            if (e.getMessage() != null) {
                LOGGER.error("[PistonCrystal] error message: " + e.getClass().getName() + " " + e.getMessage());
            }
            else {
                LOGGER.error("[PistonCrystal] cannot find the cause");
            }
            final int i5 = 0;
            if (e.getStackTrace().length != 0) {
                LOGGER.error("[PistonCrystal] StackTrace Start");
                for (final StackTraceElement errorMess : e.getStackTrace()) {
                    LOGGER.error("[PistonCrystal] " + errorMess.toString());
                }
                LOGGER.error("[PistonCrystal] StackTrace End");
            }
            printDebug(Integer.toString(step), true);
            this.disable();
        }
        if ((!this.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            PistonCrystal.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)PistonCrystal.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        if (this.rotate.getValue() || step == 1) {
            Vec3d positionHit = hitVec;
            if (!this.rotate.getValue() && step == 1) {
                positionHit = new Vec3d(PistonCrystal.mc.field_71439_g.field_70165_t + offsetX, PistonCrystal.mc.field_71439_g.field_70163_u + ((offsetY == -1.0) ? offsetY : 0.0), PistonCrystal.mc.field_71439_g.field_70161_v + offsetZ);
            }
            BlockUtil.faceVectorPacketInstant(positionHit, true);
        }
        EnumHand handSwing = EnumHand.MAIN_HAND;
        if (this.slot_mat[step] == 11) {
            handSwing = EnumHand.OFF_HAND;
        }
        PistonCrystal.mc.field_71442_b.func_187099_a(PistonCrystal.mc.field_71439_g, PistonCrystal.mc.field_71441_e, neighbour, opposite, hitVec, handSwing);
        PistonCrystal.mc.field_71439_g.func_184609_a(handSwing);
        return true;
    }
    
    private boolean placeBlockConfirm(final BlockPos pos, final int step, final double offsetX, final double offsetZ, final double offsetY, final boolean redstone, final boolean onlyRotation, final boolean support) {
        final Block block = PistonCrystal.mc.field_71441_e.func_180495_p(pos).func_177230_c();
        EnumFacing side;
        if (redstone && this.redstoneAbovePiston) {
            side = BlockUtil.getPlaceableSideExlude(pos, this.exd);
        }
        else {
            side = BlockUtil.getPlaceableSide(pos);
        }
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = pos.func_177972_a(side);
        final EnumFacing opposite = side.func_176734_d();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5 + offsetX, 0.5, 0.5 + offsetZ).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        if (this.forceRotation.getValue()) {
            this.lastHitVec = hitVec;
        }
        try {
            if (this.slot_mat[step] != 11 && PistonCrystal.mc.field_71439_g.field_71071_by.func_70301_a(this.slot_mat[step]) == ItemStack.field_190927_a) {
                this.noMaterials = true;
                return false;
            }
            if (PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c != this.slot_mat[step]) {
                if (this.slot_mat[step] == -1) {
                    this.noMaterials = true;
                    return false;
                }
                PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c = ((this.slot_mat[step] == 11) ? PistonCrystal.mc.field_71439_g.field_71071_by.field_70461_c : this.slot_mat[step]);
            }
        }
        catch (Exception e) {
            printDebug("Fatal Error during the creation of the structure. Please, report this bug in the discor's server", true);
            final Logger LOGGER = LogManager.getLogger("GameSense");
            LOGGER.error("[PistonCrystal] error during the creation of the structure.");
            if (e.getMessage() != null) {
                LOGGER.error("[PistonCrystal] error message: " + e.getClass().getName() + " " + e.getMessage());
            }
            else {
                LOGGER.error("[PistonCrystal] cannot find the cause");
            }
            final int i5 = 0;
            if (e.getStackTrace().length != 0) {
                LOGGER.error("[PistonCrystal] StackTrace Start");
                for (final StackTraceElement errorMess : e.getStackTrace()) {
                    LOGGER.error("[PistonCrystal] " + errorMess.toString());
                }
                LOGGER.error("[PistonCrystal] StackTrace End");
            }
            printDebug(Integer.toString(step), true);
            this.disable();
        }
        Vec3d positionHit = null;
        if (this.rotate.getValue() || step == 1) {
            positionHit = hitVec;
            if (!this.rotate.getValue() && step == 1) {
                positionHit = new Vec3d(PistonCrystal.mc.field_71439_g.field_70165_t + offsetX, PistonCrystal.mc.field_71439_g.field_70163_u + ((offsetY == -1.0) ? offsetY : 0.0), PistonCrystal.mc.field_71439_g.field_70161_v + offsetZ);
            }
        }
        EnumHand handSwing = EnumHand.MAIN_HAND;
        if (this.slot_mat[step] == 11) {
            handSwing = EnumHand.OFF_HAND;
        }
        PlacementUtil.placePrecise(pos, handSwing, step == 1 || (this.rotate.getValue() && !this.forceRotation.getValue()), positionHit, side, onlyRotation, !support || !this.forceRotation.getValue());
        return true;
    }
    
    public void placeBlockThings(final int step, final boolean redstone, final boolean preRotation, final boolean support) {
        final BlockPos targetPos = this.compactBlockPos(step);
        if (this.packetReducer.getValue()) {
            if (!this.placeBlockConfirm(targetPos, step, this.toPlace.offsetX, this.toPlace.offsetZ, this.toPlace.offsetY, redstone, preRotation, support)) {
                return;
            }
        }
        else if (!this.placeBlock(targetPos, step, this.toPlace.offsetX, this.toPlace.offsetZ, this.toPlace.offsetY, redstone)) {
            return;
        }
        if (!preRotation) {
            ++this.stage;
            this.afterRotationTick = 0;
        }
    }
    
    public BlockPos compactBlockPos(final int step) {
        final BlockPos offsetPos = new BlockPos((Vec3d)this.toPlace.to_place.get(this.toPlace.supportBlock + step - 1));
        return new BlockPos(this.enemyCoordsDouble[0] + offsetPos.func_177958_n(), this.enemyCoordsDouble[1] + offsetPos.func_177956_o(), this.enemyCoordsDouble[2] + offsetPos.func_177952_p());
    }
    
    private BlockPos getTargetPos(final int idx) {
        final BlockPos offsetPos = new BlockPos((Vec3d)this.toPlace.to_place.get(idx));
        return new BlockPos(this.enemyCoordsDouble[0] + offsetPos.func_177958_n(), this.enemyCoordsDouble[1] + offsetPos.func_177956_o(), this.enemyCoordsDouble[2] + offsetPos.func_177952_p());
    }
    
    private boolean checkVariable() {
        if (this.noMaterials || !this.isHole || !this.enoughSpace || this.hasMoved || this.deadPl || this.rotationPlayerMoved || !this.minHp || this.itemCrystal) {
            this.disable();
            return true;
        }
        return false;
    }
    
    private boolean createStructure() {
        final structureTemp addedStructure = new structureTemp(Double.MAX_VALUE, 0, null);
        try {
            if (this.meCoordsInt[1] - this.enemyCoordsInt[1] > -1 && this.meCoordsInt[1] - this.enemyCoordsInt[1] <= this.maxYincr.getValue()) {
                for (int startH = 1; startH >= 0; --startH) {
                    if (addedStructure.to_place == null) {
                        int incr = 0;
                        final List<Vec3d> highSup = new ArrayList<Vec3d>();
                        while (this.meCoordsInt[1] > this.enemyCoordsInt[1] + incr) {
                            ++incr;
                            for (final int[] cordSupport : this.disp_surblock) {
                                highSup.add(new Vec3d((double)cordSupport[0], (double)incr, (double)cordSupport[2]));
                            }
                        }
                        incr += startH;
                        int i = -1;
                        for (final Double[] cord_b : this.sur_block) {
                            ++i;
                            final double[] crystalCordsAbs = { cord_b[0], cord_b[1] + incr, cord_b[2] };
                            final int[] crystalCordsRel = { this.disp_surblock[i][0], this.disp_surblock[i][1] + incr, this.disp_surblock[i][2] };
                            Label_3196: {
                                final double distanceNowCrystal;
                                if ((distanceNowCrystal = PistonCrystal.mc.field_71439_g.func_70011_f(crystalCordsAbs[0], crystalCordsAbs[1], crystalCordsAbs[2])) < addedStructure.distance) {
                                    if (BlockUtil.getBlock(crystalCordsAbs[0], crystalCordsAbs[1], crystalCordsAbs[2]) instanceof BlockAir) {
                                        if (BlockUtil.getBlock(crystalCordsAbs[0], crystalCordsAbs[1] + 1.0, crystalCordsAbs[2]) instanceof BlockAir) {
                                            if (!someoneInCoords(crystalCordsAbs[0], crystalCordsAbs[2])) {
                                                if (BlockUtil.getBlock(crystalCordsAbs[0], crystalCordsAbs[1] - 1.0, crystalCordsAbs[2]) instanceof BlockObsidian || BlockUtil.getBlock(crystalCordsAbs[0], crystalCordsAbs[1] - 1.0, crystalCordsAbs[2]).getRegistryName().func_110623_a().equals("bedrock")) {
                                                    double[] pistonCordAbs = new double[3];
                                                    int[] pistonCordRel = new int[3];
                                                    if (this.rotate.getValue() || !this.betterPlacement.getValue()) {
                                                        pistonCordAbs = new double[] { crystalCordsAbs[0] + this.disp_surblock[i][0], crystalCordsAbs[1], crystalCordsAbs[2] + this.disp_surblock[i][2] };
                                                        final Block tempBlock;
                                                        if ((tempBlock = BlockUtil.getBlock(pistonCordAbs[0], pistonCordAbs[1], pistonCordAbs[2])) instanceof BlockPistonBase == tempBlock instanceof BlockAir) {
                                                            break Label_3196;
                                                        }
                                                        if (someoneInCoords(pistonCordAbs[0], pistonCordAbs[2])) {
                                                            break Label_3196;
                                                        }
                                                        pistonCordRel = new int[] { crystalCordsRel[0] * 2, crystalCordsRel[1], crystalCordsRel[2] * 2 };
                                                    }
                                                    else {
                                                        double distancePist = Double.MAX_VALUE;
                                                        for (final int[] disp : this.disp_surblock) {
                                                            final BlockPos blockPiston = new BlockPos(crystalCordsAbs[0] + disp[0], crystalCordsAbs[1], crystalCordsAbs[2] + disp[2]);
                                                            final double distanceNowPiston;
                                                            if ((distanceNowPiston = PistonCrystal.mc.field_71439_g.func_174831_c(blockPiston)) <= distancePist) {
                                                                if (BlockUtil.getBlock(blockPiston.func_177958_n(), blockPiston.func_177956_o(), blockPiston.func_177952_p()) instanceof BlockPistonBase || BlockUtil.getBlock(blockPiston.func_177958_n(), blockPiston.func_177956_o(), blockPiston.func_177952_p()) instanceof BlockAir) {
                                                                    if (!someoneInCoords(crystalCordsAbs[0] + disp[0], crystalCordsAbs[2] + disp[2])) {
                                                                        if (BlockUtil.getBlock(blockPiston.func_177958_n() - crystalCordsRel[0], blockPiston.func_177956_o(), blockPiston.func_177952_p() - crystalCordsRel[2]) instanceof BlockAir) {
                                                                            distancePist = distanceNowPiston;
                                                                            pistonCordAbs = new double[] { crystalCordsAbs[0] + disp[0], crystalCordsAbs[1], crystalCordsAbs[2] + disp[2] };
                                                                            pistonCordRel = new int[] { crystalCordsRel[0] + disp[0], crystalCordsRel[1], crystalCordsRel[2] + disp[2] };
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        if (distancePist == Double.MAX_VALUE) {
                                                            break Label_3196;
                                                        }
                                                    }
                                                    if (this.rotate.getValue()) {
                                                        final int[] pistonCordInt = { (int)pistonCordAbs[0], (int)pistonCordAbs[1], (int)pistonCordAbs[2] };
                                                        boolean behindBol = false;
                                                        for (final int checkBehind : new int[] { 0, 2 }) {
                                                            if (this.meCoordsInt[checkBehind] == pistonCordInt[checkBehind]) {
                                                                final int idx = (checkBehind == 2) ? 0 : 2;
                                                                if (pistonCordInt[idx] >= this.enemyCoordsInt[idx] == this.meCoordsInt[idx] >= this.enemyCoordsInt[idx]) {
                                                                    behindBol = true;
                                                                }
                                                            }
                                                        }
                                                        if (!behindBol && Math.abs(this.meCoordsInt[0] - this.enemyCoordsInt[0]) == 2 && Math.abs(this.meCoordsInt[2] - this.enemyCoordsInt[2]) == 2 && ((this.meCoordsInt[0] == pistonCordInt[0] && Math.abs(this.meCoordsInt[2] - pistonCordInt[2]) >= 2) || (this.meCoordsInt[2] == pistonCordInt[2] && Math.abs(this.meCoordsInt[0] - pistonCordInt[0]) >= 2))) {
                                                            behindBol = true;
                                                        }
                                                        if ((!behindBol && Math.abs(this.meCoordsInt[0] - this.enemyCoordsInt[0]) > 2 && this.meCoordsInt[2] != this.enemyCoordsInt[2]) || (Math.abs(this.meCoordsInt[2] - this.enemyCoordsInt[2]) > 2 && this.meCoordsInt[0] != this.enemyCoordsInt[0])) {
                                                            behindBol = true;
                                                        }
                                                        if (behindBol) {
                                                            break Label_3196;
                                                        }
                                                    }
                                                    double[] redstoneCoordsAbs = new double[3];
                                                    int[] redstoneCoordsRel = new int[3];
                                                    double minFound = Double.MAX_VALUE;
                                                    double minNow = -1.0;
                                                    boolean foundOne = true;
                                                    for (final int[] pos : this.disp_surblock) {
                                                        final double[] torchCoords = { pistonCordAbs[0] + pos[0], pistonCordAbs[1], pistonCordAbs[2] + pos[2] };
                                                        if ((minNow = PistonCrystal.mc.field_71439_g.func_70011_f(torchCoords[0], torchCoords[1], torchCoords[2])) < minFound) {
                                                            if (!this.redstoneBlockMode || pos[0] == crystalCordsRel[0]) {
                                                                if (!someoneInCoords(torchCoords[0], torchCoords[2]) && (BlockUtil.getBlock(torchCoords[0], torchCoords[1], torchCoords[2]) instanceof BlockRedstoneTorch || BlockUtil.getBlock(torchCoords[0], torchCoords[1], torchCoords[2]) instanceof BlockAir)) {
                                                                    if ((int)torchCoords[0] != (int)crystalCordsAbs[0] || (int)torchCoords[2] != (int)crystalCordsAbs[2]) {
                                                                        boolean torchFront = false;
                                                                        for (final int part : new int[] { 0, 2 }) {
                                                                            final int contPart = (part == 0) ? 2 : 0;
                                                                            if ((int)torchCoords[contPart] == (int)pistonCordAbs[contPart] && (int)torchCoords[part] == this.enemyCoordsInt[part]) {
                                                                                torchFront = true;
                                                                            }
                                                                        }
                                                                        if (!torchFront) {
                                                                            redstoneCoordsAbs = new double[] { torchCoords[0], torchCoords[1], torchCoords[2] };
                                                                            redstoneCoordsRel = new int[] { pistonCordRel[0] + pos[0], pistonCordRel[1], pistonCordRel[2] + pos[2] };
                                                                            foundOne = false;
                                                                            minFound = minNow;
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    this.redstoneAbovePiston = false;
                                                    if (foundOne) {
                                                        if (this.redstoneBlockMode || !(BlockUtil.getBlock(pistonCordAbs[0], pistonCordAbs[1] + 1.0, pistonCordAbs[2]) instanceof BlockAir)) {
                                                            break Label_3196;
                                                        }
                                                        redstoneCoordsAbs = new double[] { pistonCordAbs[0], pistonCordAbs[1] + 1.0, pistonCordAbs[2] };
                                                        redstoneCoordsRel = new int[] { pistonCordRel[0], pistonCordRel[1] + 1, pistonCordRel[2] };
                                                        this.redstoneAbovePiston = true;
                                                    }
                                                    if (this.redstoneBlockMode && this.allowCheapMode.getValue() && (BlockUtil.getBlock(redstoneCoordsAbs[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsAbs[2]) instanceof BlockAir || BlockUtil.getBlock(redstoneCoordsAbs[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsAbs[2]).field_149770_b.equals("blockRedstone"))) {
                                                        pistonCordAbs = new double[] { redstoneCoordsAbs[0], redstoneCoordsAbs[1], redstoneCoordsAbs[2] };
                                                        pistonCordRel = new int[] { redstoneCoordsRel[0], redstoneCoordsRel[1], redstoneCoordsRel[2] };
                                                        redstoneCoordsAbs = new double[] { redstoneCoordsAbs[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsRel[2] };
                                                        redstoneCoordsRel = new int[] { redstoneCoordsRel[0], redstoneCoordsRel[1] - 1, redstoneCoordsRel[2] };
                                                        this.fastModeActive = true;
                                                    }
                                                    final List<Vec3d> toPlaceTemp = new ArrayList<Vec3d>();
                                                    int supportBlock = 0;
                                                    if (BlockUtil.getBlock(crystalCordsAbs[0], crystalCordsAbs[1] - 1.0, crystalCordsAbs[2]) instanceof BlockAir) {
                                                        toPlaceTemp.add(new Vec3d((double)crystalCordsRel[0], (double)(crystalCordsRel[1] - 1), (double)crystalCordsRel[2]));
                                                        ++supportBlock;
                                                    }
                                                    if (!this.fastModeActive && BlockUtil.getBlock(pistonCordAbs[0], pistonCordAbs[1] - 1.0, pistonCordAbs[2]) instanceof BlockAir) {
                                                        toPlaceTemp.add(new Vec3d((double)pistonCordRel[0], (double)(pistonCordRel[1] - 1), (double)pistonCordRel[2]));
                                                        ++supportBlock;
                                                    }
                                                    if (!this.fastModeActive) {
                                                        if (this.redstoneAbovePiston) {
                                                            int[] toAdd;
                                                            if (this.enemyCoordsInt[0] == (int)pistonCordAbs[0] && this.enemyCoordsInt[2] == (int)pistonCordAbs[2]) {
                                                                toAdd = new int[] { crystalCordsRel[0], 0, 0 };
                                                            }
                                                            else {
                                                                toAdd = new int[] { crystalCordsRel[0], 0, crystalCordsRel[2] };
                                                            }
                                                            for (int hight = 0; hight < 2; ++hight) {
                                                                if (BlockUtil.getBlock(pistonCordAbs[0] + toAdd[0], pistonCordAbs[1] + hight, pistonCordAbs[2] + toAdd[2]) instanceof BlockAir) {
                                                                    toPlaceTemp.add(new Vec3d((double)(pistonCordRel[0] + toAdd[0]), (double)(pistonCordRel[1] + hight), (double)(pistonCordRel[2] + toAdd[2])));
                                                                    ++supportBlock;
                                                                }
                                                            }
                                                        }
                                                        else if (!this.redstoneBlockMode && BlockUtil.getBlock(redstoneCoordsAbs[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsAbs[2]) instanceof BlockAir) {
                                                            toPlaceTemp.add(new Vec3d((double)redstoneCoordsRel[0], (double)(redstoneCoordsRel[1] - 1), (double)redstoneCoordsRel[2]));
                                                            ++supportBlock;
                                                        }
                                                    }
                                                    else if (BlockUtil.getBlock(redstoneCoordsAbs[0] - crystalCordsRel[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsAbs[2] - crystalCordsRel[2]) instanceof BlockAir) {
                                                        toPlaceTemp.add(new Vec3d((double)(redstoneCoordsRel[0] - crystalCordsRel[0]), (double)redstoneCoordsRel[1], (double)(redstoneCoordsRel[2] - crystalCordsRel[2])));
                                                        ++supportBlock;
                                                    }
                                                    toPlaceTemp.add(new Vec3d((double)pistonCordRel[0], (double)pistonCordRel[1], (double)pistonCordRel[2]));
                                                    toPlaceTemp.add(new Vec3d((double)crystalCordsRel[0], (double)crystalCordsRel[1], (double)crystalCordsRel[2]));
                                                    toPlaceTemp.add(new Vec3d((double)redstoneCoordsRel[0], (double)redstoneCoordsRel[1], (double)redstoneCoordsRel[2]));
                                                    if (incr > 1) {
                                                        for (int i2 = 0; i2 < highSup.size(); ++i2) {
                                                            toPlaceTemp.add(0, highSup.get(i2));
                                                            ++supportBlock;
                                                        }
                                                    }
                                                    float offsetX;
                                                    float offsetZ;
                                                    if (this.disp_surblock[i][0] != 0) {
                                                        offsetX = (this.rotate.getValue() ? (this.disp_surblock[i][0] / 2.0f) : ((float)this.disp_surblock[i][0]));
                                                        if (this.rotate.getValue()) {
                                                            if (PistonCrystal.mc.field_71439_g.func_70092_e(pistonCordAbs[0], pistonCordAbs[1], pistonCordAbs[2] + 0.5) > PistonCrystal.mc.field_71439_g.func_70092_e(pistonCordAbs[0], pistonCordAbs[1], pistonCordAbs[2] - 0.5)) {
                                                                offsetZ = -0.5f;
                                                            }
                                                            else {
                                                                offsetZ = 0.5f;
                                                            }
                                                        }
                                                        else {
                                                            offsetZ = (float)this.disp_surblock[i][2];
                                                        }
                                                    }
                                                    else {
                                                        offsetZ = (this.rotate.getValue() ? (this.disp_surblock[i][2] / 2.0f) : ((float)this.disp_surblock[i][2]));
                                                        if (this.rotate.getValue()) {
                                                            if (PistonCrystal.mc.field_71439_g.func_70092_e(pistonCordAbs[0] + 0.5, pistonCordAbs[1], pistonCordAbs[2]) > PistonCrystal.mc.field_71439_g.func_70092_e(pistonCordAbs[0] - 0.5, pistonCordAbs[1], pistonCordAbs[2])) {
                                                                offsetX = -0.5f;
                                                            }
                                                            else {
                                                                offsetX = 0.5f;
                                                            }
                                                        }
                                                        else {
                                                            offsetX = (float)this.disp_surblock[i][0];
                                                        }
                                                    }
                                                    final float offsetY = (this.meCoordsInt[1] - this.enemyCoordsInt[1] == -1) ? 0.0f : 1.0f;
                                                    addedStructure.replaceValues(distanceNowCrystal, supportBlock, toPlaceTemp, -1, offsetX, offsetZ, offsetY);
                                                    if (this.blockPlayer.getValue()) {
                                                        final Vec3d valuesStart = addedStructure.to_place.get(addedStructure.supportBlock + 1);
                                                        final int[] valueBegin = { (int)(-valuesStart.field_72450_a), (int)valuesStart.field_72448_b, (int)(-valuesStart.field_72449_c) };
                                                        if (!this.bypassObsidian.getValue() || (int)PistonCrystal.mc.field_71439_g.field_70163_u == this.enemyCoordsInt[1]) {
                                                            addedStructure.to_place.add(0, new Vec3d(0.0, (double)(incr + 1), 0.0));
                                                            addedStructure.to_place.add(0, new Vec3d((double)valueBegin[0], (double)(incr + 1), (double)valueBegin[2]));
                                                            addedStructure.to_place.add(0, new Vec3d((double)valueBegin[0], (double)incr, (double)valueBegin[2]));
                                                            final structureTemp structureTemp = addedStructure;
                                                            structureTemp.supportBlock += 3;
                                                        }
                                                        else {
                                                            addedStructure.to_place.add(0, new Vec3d(0.0, (double)incr, 0.0));
                                                            addedStructure.to_place.add(0, new Vec3d((double)valueBegin[0], (double)incr, (double)valueBegin[2]));
                                                            final structureTemp structureTemp2 = addedStructure;
                                                            structureTemp2.supportBlock += 2;
                                                        }
                                                    }
                                                    this.toPlace = addedStructure;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            else {
                this.yUnder = true;
            }
        }
        catch (Exception e) {
            printDebug("Fatal Error during the creation of the structure. Please, report this bug in the discor's server", true);
            final Logger LOGGER = LogManager.getLogger("GameSense");
            LOGGER.error("[PistonCrystal] error during the creation of the structure.");
            if (e.getMessage() != null) {
                LOGGER.error("[PistonCrystal] error message: " + e.getClass().getName() + " " + e.getMessage());
            }
            else {
                LOGGER.error("[PistonCrystal] cannot find the cause");
            }
            int i3 = 0;
            if (e.getStackTrace().length != 0) {
                LOGGER.error("[PistonCrystal] StackTrace Start");
                for (final StackTraceElement errorMess : e.getStackTrace()) {
                    LOGGER.error("[PistonCrystal] " + errorMess.toString());
                }
                LOGGER.error("[PistonCrystal] StackTrace End");
            }
            if (this.aimTarget != null) {
                LOGGER.error("[PistonCrystal] closest target is not null");
            }
            else {
                LOGGER.error("[PistonCrystal] closest target is null somehow");
            }
            for (final Double[] cord_b2 : this.sur_block) {
                if (cord_b2 != null) {
                    LOGGER.error("[PistonCrystal] " + i3 + " is not null");
                }
                else {
                    LOGGER.error("[PistonCrystal] " + i3 + " is null");
                }
                ++i3;
            }
        }
        if (this.debugMode.getValue() && addedStructure.to_place != null) {
            printDebug("Skeleton structure:", false);
            for (final Vec3d parte : addedStructure.to_place) {
                printDebug(String.format("%f %f %f", parte.field_72450_a, parte.field_72448_b, parte.field_72449_c), false);
            }
        }
        return addedStructure.to_place != null;
    }
    
    public static boolean someoneInCoords(final double x, final double z) {
        final int xCheck = (int)x;
        final int zCheck = (int)z;
        final List<EntityPlayer> playerList = (List<EntityPlayer>)PistonCrystal.mc.field_71441_e.field_73010_i;
        for (final EntityPlayer player : playerList) {
            if ((int)player.field_70165_t == xCheck && (int)player.field_70161_v == zCheck) {
                return true;
            }
        }
        return false;
    }
    
    private boolean getMaterialsSlot() {
        if (PistonCrystal.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemEndCrystal) {
            this.slot_mat[2] = 11;
        }
        if (this.placeMode.getValue().equals("Block")) {
            this.redstoneBlockMode = true;
        }
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = PistonCrystal.mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack != ItemStack.field_190927_a) {
                if (this.slot_mat[2] == -1 && stack.func_77973_b() instanceof ItemEndCrystal) {
                    this.slot_mat[2] = i;
                }
                else if (this.antiWeakness.getValue() && stack.func_77973_b() instanceof ItemSword) {
                    this.slot_mat[4] = i;
                }
                else if (stack.func_77973_b() instanceof ItemPickaxe) {
                    this.slot_mat[5] = i;
                }
                if (stack.func_77973_b() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
                    if (block instanceof BlockObsidian) {
                        this.slot_mat[0] = i;
                    }
                    else if (block instanceof BlockPistonBase) {
                        this.slot_mat[1] = i;
                    }
                    else if (!this.placeMode.getValue().equals("Block") && block instanceof BlockRedstoneTorch) {
                        this.slot_mat[3] = i;
                        this.redstoneBlockMode = false;
                    }
                    else if (!this.placeMode.getValue().equals("Torch") && block.field_149770_b.equals("blockRedstone")) {
                        this.slot_mat[3] = i;
                        this.redstoneBlockMode = true;
                    }
                }
            }
        }
        if (!this.redstoneBlockMode) {
            this.slot_mat[5] = -1;
        }
        int count = 0;
        for (final int val : this.slot_mat) {
            if (val != -1) {
                ++count;
            }
        }
        if (this.debugMode.getValue()) {
            printDebug(String.format("%d %d %d %d %d %d", this.slot_mat[0], this.slot_mat[1], this.slot_mat[2], this.slot_mat[3], this.slot_mat[4], this.slot_mat[5]), false);
        }
        return count >= 4 + (((boolean)this.antiWeakness.getValue()) ? 1 : 0) + (this.redstoneBlockMode ? 1 : 0);
    }
    
    private boolean is_in_hole() {
        this.sur_block = new Double[][] { { this.aimTarget.field_70165_t + 1.0, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v }, { this.aimTarget.field_70165_t - 1.0, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v }, { this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v + 1.0 }, { this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v - 1.0 } };
        return HoleUtil.isHole(EntityUtil.getPosition((Entity)this.aimTarget), true, true).getType() != HoleUtil.HoleType.NONE;
    }
    
    public static void printDebug(final String text, final Boolean error) {
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        MessageBus.sendClientPrefixMessage((error ? colorMain.getDisabledColor() : colorMain.getEnabledColor()) + text);
    }
    
    private static class structureTemp
    {
        public double distance;
        public int supportBlock;
        public List<Vec3d> to_place;
        public int direction;
        public float offsetX;
        public float offsetY;
        public float offsetZ;
        
        public structureTemp(final double distance, final int supportBlock, final List<Vec3d> to_place) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = -1;
        }
        
        public void replaceValues(final double distance, final int supportBlock, final List<Vec3d> to_place, final int direction, final float offsetX, final float offsetZ, final float offsetY) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = direction;
            this.offsetX = offsetX;
            this.offsetZ = offsetZ;
            this.offsetY = offsetY;
        }
    }
}
