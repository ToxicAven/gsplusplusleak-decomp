// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.combat;

import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.api.util.world.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemEndCrystal;
import java.util.Objects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3i;
import com.gamesense.api.util.player.PlacementUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockAir;
import com.gamesense.api.util.world.combat.CrystalUtil;
import java.util.Iterator;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import com.gamesense.api.util.world.BlockUtil;
import net.minecraft.block.BlockObsidian;
import com.gamesense.client.module.modules.misc.AutoGG;
import net.minecraft.network.Packet;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import com.gamesense.client.GameSense;
import com.gamesense.client.module.ModuleManager;
import java.util.ArrayList;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.player.SpoofRotationUtil;
import net.minecraft.util.math.Vec2f;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.event.Phase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.network.play.server.SPacketSoundEffect;
import java.util.function.Predicate;
import java.util.Arrays;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import net.minecraft.util.math.Vec3d;
import com.gamesense.api.event.events.PacketEvent;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.DestroyBlockEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.player.EntityPlayer;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "CevBreaker", category = Category.Combat, priority = 999)
public class CevBreaker extends Module
{
    ModeSetting target;
    ModeSetting breakCrystal;
    ModeSetting breakBlock;
    DoubleSetting enemyRange;
    IntegerSetting preRotationDelay;
    IntegerSetting afterRotationDelay;
    IntegerSetting supDelay;
    IntegerSetting crystalDelay;
    IntegerSetting blocksPerTick;
    IntegerSetting hitDelay;
    IntegerSetting midHitDelay;
    IntegerSetting endDelay;
    IntegerSetting pickSwitchTick;
    BooleanSetting rotate;
    BooleanSetting confirmBreak;
    BooleanSetting confirmPlace;
    BooleanSetting antiWeakness;
    BooleanSetting switchSword;
    BooleanSetting fastPlace;
    BooleanSetting fastBreak;
    BooleanSetting trapPlayer;
    BooleanSetting antiStep;
    BooleanSetting placeCrystal;
    BooleanSetting forceRotation;
    BooleanSetting forceBreaker;
    public static int cur_item;
    public static boolean isActive;
    public static boolean forceBrk;
    private boolean noMaterials;
    private boolean hasMoved;
    private boolean isSneaking;
    private boolean isHole;
    private boolean enoughSpace;
    private boolean broken;
    private boolean stoppedCa;
    private boolean deadPl;
    private boolean rotationPlayerMoved;
    private boolean prevBreak;
    private boolean preRotationBol;
    private int oldSlot;
    private int stage;
    private int delayTimeTicks;
    private int hitTryTick;
    private int tickPick;
    private int afterRotationTick;
    private int preRotationTick;
    private final int[][] model;
    public static boolean isPossible;
    private int[] slot_mat;
    private int[] delayTable;
    private int[] enemyCoordsInt;
    private double[] enemyCoordsDouble;
    private structureTemp toPlace;
    Double[][] sur_block;
    private EntityPlayer aimTarget;
    @EventHandler
    private final Listener<DestroyBlockEvent> listener2;
    @EventHandler
    private final Listener<PacketEvent.Receive> packetReceiveListener;
    Vec3d lastHitVec;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
    
    public CevBreaker() {
        this.target = this.registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest");
        this.breakCrystal = this.registerMode("Break Crystal", Arrays.asList("Vanilla", "Packet", "None"), "Packet");
        this.breakBlock = this.registerMode("Break Block", Arrays.asList("Normal", "Packet"), "Packet");
        this.enemyRange = this.registerDouble("Range", 4.9, 0.0, 6.0);
        this.preRotationDelay = this.registerInteger("Pre Rotation Delay", 0, 0, 20);
        this.afterRotationDelay = this.registerInteger("After Rotation Delay", 0, 0, 20);
        this.supDelay = this.registerInteger("Support Delay", 1, 0, 4);
        this.crystalDelay = this.registerInteger("Crystal Delay", 2, 0, 20);
        this.blocksPerTick = this.registerInteger("Blocks Per Tick", 4, 2, 6);
        this.hitDelay = this.registerInteger("Hit Delay", 2, 0, 20);
        this.midHitDelay = this.registerInteger("Mid Hit Delay", 1, 0, 20);
        this.endDelay = this.registerInteger("End Delay", 1, 0, 20);
        this.pickSwitchTick = this.registerInteger("Pick Switch Tick", 100, 0, 500);
        this.rotate = this.registerBoolean("Rotate", false);
        this.confirmBreak = this.registerBoolean("No Glitch Break", true);
        this.confirmPlace = this.registerBoolean("No Glitch Place", true);
        this.antiWeakness = this.registerBoolean("Anti Weakness", false);
        this.switchSword = this.registerBoolean("Switch Sword", false);
        this.fastPlace = this.registerBoolean("Fast Place", false);
        this.fastBreak = this.registerBoolean("Fast Break", true);
        this.trapPlayer = this.registerBoolean("Trap Player", false);
        this.antiStep = this.registerBoolean("Anti Step", false);
        this.placeCrystal = this.registerBoolean("Place Crystal", true);
        this.forceRotation = this.registerBoolean("Force Rotation", false);
        this.forceBreaker = this.registerBoolean("Force Breaker", false);
        this.noMaterials = false;
        this.hasMoved = false;
        this.isSneaking = false;
        this.isHole = true;
        this.enoughSpace = true;
        this.oldSlot = -1;
        this.model = new int[][] { { 1, 1, 0 }, { -1, 1, 0 }, { 0, 1, 1 }, { 0, 1, -1 } };
        this.sur_block = new Double[4][3];
        this.listener2 = new Listener<DestroyBlockEvent>(event -> {
            if (this.enemyCoordsInt != null) {
                if (event.getBlockPos().field_177962_a + ((event.getBlockPos().field_177962_a < 0) ? 1 : 0) == this.enemyCoordsInt[0]) {
                    if (event.getBlockPos().field_177961_c + ((event.getBlockPos().field_177961_c < 0) ? 1 : 0) == this.enemyCoordsInt[2]) {
                        this.destroyCrystalAlgo();
                    }
                }
            }
            return;
        }, (Predicate<DestroyBlockEvent>[])new Predicate[0]);
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
            if (event.getPhase() == Phase.PRE && this.rotate.getValue() && this.lastHitVec != null && this.forceRotation.getValue()) {
                rotation = RotationUtil.getRotationTo(this.lastHitVec);
                packet2 = new PlayerPacket(this, rotation);
                PlayerPacketManager.INSTANCE.addPacket(packet2);
            }
        }, (Predicate<OnUpdateWalkingPlayerEvent>[])new Predicate[0]);
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
    
    private void initValues() {
        this.preRotationBol = false;
        final int n = 0;
        this.preRotationTick = n;
        this.afterRotationTick = n;
        CevBreaker.isPossible = false;
        this.aimTarget = null;
        this.lastHitVec = null;
        this.delayTable = new int[] { this.supDelay.getValue(), this.crystalDelay.getValue(), this.hitDelay.getValue(), this.endDelay.getValue() };
        this.toPlace = new structureTemp(0.0, 0, new ArrayList<Vec3d>());
        this.isHole = (CevBreaker.isActive = true);
        final boolean b = false;
        this.broken = b;
        this.deadPl = b;
        this.rotationPlayerMoved = b;
        this.hasMoved = b;
        this.slot_mat = new int[] { -1, -1, -1, -1 };
        final int n2 = 0;
        this.delayTimeTicks = n2;
        this.stage = n2;
        if (CevBreaker.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        this.oldSlot = CevBreaker.mc.field_71439_g.field_71071_by.field_70461_c;
        this.stoppedCa = false;
        CevBreaker.cur_item = -1;
        if (ModuleManager.isModuleEnabled(AutoCrystal.class)) {
            AutoCrystal.stopAC = true;
            this.stoppedCa = true;
        }
        CevBreaker.forceBrk = this.forceBreaker.getValue();
    }
    
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe(this);
        SpoofRotationUtil.ROTATION_UTIL.onDisable();
        if (CevBreaker.mc.field_71439_g == null) {
            return;
        }
        String output = "";
        String materialsNeeded = "";
        if (this.aimTarget == null) {
            output = "No target found...";
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
        this.setDisabledMessage(output + "CevBreaker turned OFF!");
        if (!materialsNeeded.equals("")) {
            this.setDisabledMessage("Materials missing:" + materialsNeeded);
        }
        if (this.stoppedCa) {
            AutoCrystal.stopAC = false;
            this.stoppedCa = false;
        }
        if (this.isSneaking) {
            CevBreaker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)CevBreaker.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        if (this.oldSlot != CevBreaker.mc.field_71439_g.field_71071_by.field_70461_c && this.oldSlot != -1) {
            CevBreaker.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
            this.oldSlot = -1;
        }
        this.noMaterials = (CevBreaker.isPossible = (AutoCrystal.stopAC = (CevBreaker.isActive = (CevBreaker.forceBrk = false))));
    }
    
    private String getMissingMaterials() {
        final StringBuilder output = new StringBuilder();
        if (this.slot_mat[0] == -1) {
            output.append(" Obsidian");
        }
        if (this.slot_mat[1] == -1) {
            output.append(" Crystal");
        }
        if ((this.antiWeakness.getValue() || this.switchSword.getValue()) && this.slot_mat[3] == -1) {
            output.append(" Sword");
        }
        if (this.slot_mat[2] == -1) {
            output.append(" Pick");
        }
        return output.toString();
    }
    
    @Override
    public void onUpdate() {
        if (CevBreaker.mc.field_71439_g == null || CevBreaker.mc.field_71439_g.field_70128_L) {
            this.disable();
            return;
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
                    this.playerChecks();
                    if (ModuleManager.isModuleEnabled(AutoGG.class)) {
                        AutoGG.INSTANCE.addTargetedPlayer(this.aimTarget.func_70005_c_());
                    }
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
        if ((int)this.aimTarget.field_70165_t != (int)this.enemyCoordsDouble[0] || (int)this.aimTarget.field_70161_v != (int)this.enemyCoordsDouble[2]) {
            this.hasMoved = true;
        }
        if (this.checkVariable()) {
            return;
        }
        if (this.placeSupport()) {
            switch (this.stage) {
                case 1: {
                    if (this.getCrystal() != null) {
                        this.stage = 3;
                        return;
                    }
                    if (this.afterRotationDelay.getValue() != 0 && this.afterRotationTick != this.afterRotationDelay.getValue()) {
                        ++this.afterRotationTick;
                        return;
                    }
                    if (this.preRotationDelay.getValue() != 0 && !this.preRotationBol) {
                        this.placeBlockThings(this.stage, true, false);
                        if (this.preRotationTick != this.preRotationDelay.getValue()) {
                            ++this.preRotationTick;
                            break;
                        }
                        this.preRotationBol = true;
                        this.preRotationTick = 0;
                    }
                    this.placeBlockThings(this.stage, false, false);
                    if (this.fastPlace.getValue()) {
                        this.placeCrystal(false);
                    }
                    this.prevBreak = false;
                    this.tickPick = 0;
                    break;
                }
                case 2: {
                    if (this.afterRotationDelay.getValue() != 0 && this.afterRotationTick != this.afterRotationDelay.getValue()) {
                        ++this.afterRotationTick;
                        return;
                    }
                    if (this.preRotationDelay.getValue() != 0 && !this.preRotationBol) {
                        this.placeCrystal(true);
                        if (this.preRotationTick != this.preRotationDelay.getValue()) {
                            ++this.preRotationTick;
                            break;
                        }
                        this.preRotationBol = true;
                        this.preRotationTick = 0;
                    }
                    if (this.confirmPlace.getValue() && !(BlockUtil.getBlock(this.compactBlockPos(0)) instanceof BlockObsidian)) {
                        --this.stage;
                        return;
                    }
                    this.placeCrystal(false);
                    break;
                }
                case 3: {
                    if (this.confirmPlace.getValue() && this.getCrystal() == null) {
                        this.stage = 1;
                        return;
                    }
                    int switchValue = 3;
                    if (!this.switchSword.getValue() || this.tickPick == this.pickSwitchTick.getValue() || this.tickPick++ == 0) {
                        switchValue = 2;
                    }
                    this.switchPick(switchValue);
                    final BlockPos obbyBreak = new BlockPos(this.enemyCoordsDouble[0], (double)(this.enemyCoordsInt[1] + 2), this.enemyCoordsDouble[2]);
                    if (BlockUtil.getBlock(obbyBreak) instanceof BlockObsidian) {
                        final EnumFacing sideBreak = BlockUtil.getPlaceableSide(obbyBreak);
                        if (sideBreak != null) {
                            final String s = this.breakBlock.getValue();
                            switch (s) {
                                case "Packet": {
                                    if (!this.prevBreak) {
                                        CevBreaker.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                                        CevBreaker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, obbyBreak, sideBreak));
                                        CevBreaker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, obbyBreak, sideBreak));
                                        this.prevBreak = true;
                                        break;
                                    }
                                    break;
                                }
                                case "Normal": {
                                    CevBreaker.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                                    CevBreaker.mc.field_71442_b.func_180512_c(obbyBreak, sideBreak);
                                    break;
                                }
                            }
                        }
                        break;
                    }
                    this.destroyCrystalAlgo();
                    break;
                }
            }
        }
    }
    
    private void switchPick(final int switchValue) {
        if (CevBreaker.cur_item != this.slot_mat[switchValue]) {
            if (this.slot_mat[switchValue] == -1) {
                this.noMaterials = true;
                return;
            }
            CevBreaker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(CevBreaker.cur_item = this.slot_mat[switchValue]));
            CevBreaker.mc.field_71439_g.field_71071_by.field_70461_c = CevBreaker.cur_item;
        }
    }
    
    private void placeCrystal(final boolean onlyRotate) {
        this.placeBlockThings(this.stage, onlyRotate, true);
        if (this.fastBreak.getValue() && !onlyRotate) {
            this.fastBreakFun();
        }
    }
    
    private void fastBreakFun() {
        this.switchPick(2);
        CevBreaker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, new BlockPos(this.enemyCoordsInt[0], this.enemyCoordsInt[1] + 2, this.enemyCoordsInt[2]), EnumFacing.UP));
        CevBreaker.isPossible = true;
    }
    
    private Entity getCrystal() {
        for (final Entity t : CevBreaker.mc.field_71441_e.field_72996_f) {
            if (t instanceof EntityEnderCrystal && (int)t.field_70165_t == this.enemyCoordsInt[0] && (int)t.field_70161_v == this.enemyCoordsInt[2] && t.field_70163_u - this.enemyCoordsInt[1] == 3.0) {
                return t;
            }
        }
        return null;
    }
    
    public void destroyCrystalAlgo() {
        CevBreaker.isPossible = false;
        final Entity crystal = this.getCrystal();
        if (this.confirmBreak.getValue() && this.broken && crystal == null) {
            this.stage = 1;
            this.broken = false;
        }
        if (crystal != null) {
            this.breakCrystalPiston(crystal);
            if (this.confirmBreak.getValue()) {
                this.broken = true;
            }
            else {
                this.stage = 1;
            }
        }
        else {
            this.stage = 1;
        }
    }
    
    private void breakCrystalPiston(final Entity crystal) {
        if (this.hitTryTick++ < this.midHitDelay.getValue()) {
            return;
        }
        this.hitTryTick = 0;
        if (this.antiWeakness.getValue()) {
            CevBreaker.mc.field_71439_g.field_71071_by.field_70461_c = this.slot_mat[3];
        }
        final Vec3d vecCrystal = crystal.func_174791_d().func_72441_c(0.5, 0.5, 0.5);
        if (!this.breakCrystal.getValue().equalsIgnoreCase("None") && this.rotate.getValue()) {
            SpoofRotationUtil.ROTATION_UTIL.lookAtPacket(vecCrystal.field_72450_a, vecCrystal.field_72448_b, vecCrystal.field_72449_c, (EntityPlayer)CevBreaker.mc.field_71439_g);
            if (this.forceRotation.getValue()) {
                this.lastHitVec = vecCrystal;
            }
        }
        try {
            final String s = this.breakCrystal.getValue();
            switch (s) {
                case "Vanilla": {
                    CrystalUtil.breakCrystal(crystal);
                    break;
                }
                case "Packet": {
                    CrystalUtil.breakCrystalPacket(crystal);
                    break;
                }
            }
        }
        catch (NullPointerException ex) {}
        if (this.rotate.getValue()) {
            SpoofRotationUtil.ROTATION_UTIL.resetRotation();
        }
    }
    
    private boolean placeSupport() {
        int checksDone = 0;
        int blockDone = 0;
        if (this.toPlace.supportBlock > 0) {
            do {
                final BlockPos targetPos = this.getTargetPos(checksDone);
                if (BlockUtil.getBlock(targetPos) instanceof BlockAir) {
                    if (this.preRotationDelay.getValue() != 0 && !this.preRotationBol) {
                        if (this.preRotationTick == 0) {
                            this.placeBlock(targetPos, 0, true);
                        }
                        if (this.preRotationTick != this.preRotationDelay.getValue()) {
                            ++this.preRotationTick;
                            return false;
                        }
                        this.preRotationBol = true;
                        this.preRotationTick = 0;
                    }
                    if (!this.placeBlock(targetPos, 0, false)) {
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
    
    private boolean changeItem(final int step) {
        if (this.slot_mat[step] == 11 || CevBreaker.mc.field_71439_g.field_71071_by.func_70301_a(this.slot_mat[step]) != ItemStack.field_190927_a) {
            if (CevBreaker.cur_item != this.slot_mat[step]) {
                if (this.slot_mat[step] == -1) {
                    return this.noMaterials = true;
                }
                if (this.slot_mat[step] != 11) {
                    CevBreaker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(CevBreaker.cur_item = this.slot_mat[step]));
                    CevBreaker.mc.field_71439_g.field_71071_by.field_70461_c = CevBreaker.cur_item;
                }
            }
            return false;
        }
        return this.noMaterials = true;
    }
    
    private boolean placeBlock(final BlockPos pos, final int step, final boolean onlyRotate) {
        if (this.changeItem(step)) {
            return false;
        }
        if (!onlyRotate) {
            EnumHand handSwing = EnumHand.MAIN_HAND;
            if (this.slot_mat[step] == 11) {
                handSwing = EnumHand.OFF_HAND;
            }
            PlacementUtil.place(pos, handSwing, this.rotate.getValue() && !this.forceRotation.getValue(), false);
            return true;
        }
        final EnumFacing side = BlockUtil.getPlaceableSide(pos);
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = pos.func_177972_a(side);
        final EnumFacing opposite = side.func_176734_d();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        final double add = (step == 1 && (int)CevBreaker.mc.field_71439_g.field_70163_u == this.enemyCoordsInt[1]) ? -0.5 : 0.0;
        this.lastHitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.5 + add, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
        return false;
    }
    
    public void placeBlockThings(int step, final boolean onlyRotate, final boolean isCrystal) {
        if (step != 1 || this.placeCrystal.getValue()) {
            --step;
            final BlockPos targetPos = this.compactBlockPos(step);
            if (!isCrystal) {
                this.placeBlock(targetPos, step, onlyRotate);
            }
            else {
                if (this.changeItem(step)) {
                    return;
                }
                EnumHand handSwing = EnumHand.MAIN_HAND;
                if (this.slot_mat[step] == 11) {
                    handSwing = EnumHand.OFF_HAND;
                }
                CevBreaker.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerTryUseItemOnBlock(targetPos.func_177963_a(0.5, 0.5, 0.5), EnumFacing.func_190914_a(targetPos, (EntityLivingBase)CevBreaker.mc.field_71439_g), handSwing, 0.0f, 0.0f, 0.0f));
                CevBreaker.mc.field_71439_g.func_184609_a(handSwing);
            }
        }
        if (!onlyRotate) {
            ++this.stage;
            this.afterRotationTick = 0;
            this.preRotationBol = false;
        }
    }
    
    public BlockPos compactBlockPos(final int step) {
        final BlockPos offsetPos = new BlockPos((Vec3d)this.toPlace.to_place.get(this.toPlace.supportBlock + step));
        return new BlockPos(this.enemyCoordsDouble[0] + offsetPos.func_177958_n(), this.enemyCoordsDouble[1] + offsetPos.func_177956_o(), this.enemyCoordsDouble[2] + offsetPos.func_177952_p());
    }
    
    private BlockPos getTargetPos(final int idx) {
        final BlockPos offsetPos = new BlockPos((Vec3d)this.toPlace.to_place.get(idx));
        return new BlockPos(this.enemyCoordsDouble[0] + offsetPos.func_177958_n(), this.enemyCoordsDouble[1] + offsetPos.func_177956_o(), this.enemyCoordsDouble[2] + offsetPos.func_177952_p());
    }
    
    private boolean checkVariable() {
        if (this.noMaterials || !this.isHole || !this.enoughSpace || this.hasMoved || this.deadPl || this.rotationPlayerMoved) {
            this.disable();
            return true;
        }
        return false;
    }
    
    private boolean createStructure() {
        if (Objects.requireNonNull(BlockUtil.getBlock(this.enemyCoordsDouble[0], this.enemyCoordsDouble[1] + 2.0, this.enemyCoordsDouble[2]).getRegistryName()).toString().toLowerCase().contains("bedrock") || !(BlockUtil.getBlock(this.enemyCoordsDouble[0], this.enemyCoordsDouble[1] + 3.0, this.enemyCoordsDouble[2]) instanceof BlockAir) || !(BlockUtil.getBlock(this.enemyCoordsDouble[0], this.enemyCoordsDouble[1] + 4.0, this.enemyCoordsDouble[2]) instanceof BlockAir)) {
            return false;
        }
        double max_found = Double.MIN_VALUE;
        int cor = 0;
        int i = 0;
        for (final Double[] cord_b : this.sur_block) {
            final double distance_now;
            if ((distance_now = CevBreaker.mc.field_71439_g.func_174818_b(new BlockPos((double)cord_b[0], (double)cord_b[1], (double)cord_b[2]))) > max_found) {
                max_found = distance_now;
                cor = i;
            }
            ++i;
        }
        this.toPlace.to_place.add(new Vec3d((double)this.model[cor][0], 1.0, (double)this.model[cor][2]));
        this.toPlace.to_place.add(new Vec3d((double)this.model[cor][0], 2.0, (double)this.model[cor][2]));
        this.toPlace.supportBlock = 2;
        if (this.trapPlayer.getValue() || this.antiStep.getValue()) {
            for (int high = 1; high < 3; ++high) {
                if (high != 2 || this.antiStep.getValue()) {
                    for (final int[] modelBas : this.model) {
                        final Vec3d toAdd = new Vec3d((double)modelBas[0], (double)high, (double)modelBas[2]);
                        if (!this.toPlace.to_place.contains(toAdd)) {
                            this.toPlace.to_place.add(toAdd);
                            final structureTemp toPlace = this.toPlace;
                            ++toPlace.supportBlock;
                        }
                    }
                }
            }
        }
        this.toPlace.to_place.add(new Vec3d(0.0, 2.0, 0.0));
        this.toPlace.to_place.add(new Vec3d(0.0, 2.0, 0.0));
        return true;
    }
    
    private boolean getMaterialsSlot() {
        if (CevBreaker.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemEndCrystal) {
            this.slot_mat[1] = 11;
        }
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = CevBreaker.mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack != ItemStack.field_190927_a) {
                if (this.slot_mat[1] == -1 && stack.func_77973_b() instanceof ItemEndCrystal) {
                    this.slot_mat[1] = i;
                }
                else if ((this.antiWeakness.getValue() || this.switchSword.getValue()) && stack.func_77973_b() instanceof ItemSword) {
                    this.slot_mat[3] = i;
                }
                else if (stack.func_77973_b() instanceof ItemPickaxe) {
                    this.slot_mat[2] = i;
                }
                if (stack.func_77973_b() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.func_77973_b()).func_179223_d();
                    if (block instanceof BlockObsidian) {
                        this.slot_mat[0] = i;
                    }
                }
            }
        }
        int count = 0;
        for (final int val : this.slot_mat) {
            if (val != -1) {
                ++count;
            }
        }
        return count >= 3 + ((this.antiWeakness.getValue() || this.switchSword.getValue()) ? 1 : 0);
    }
    
    private boolean is_in_hole() {
        this.sur_block = new Double[][] { { this.aimTarget.field_70165_t + 1.0, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v }, { this.aimTarget.field_70165_t - 1.0, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v }, { this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v + 1.0 }, { this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v - 1.0 } };
        return HoleUtil.isHole(EntityUtil.getPosition((Entity)this.aimTarget), true, true).getType() != HoleUtil.HoleType.NONE;
    }
    
    static {
        CevBreaker.cur_item = -1;
        CevBreaker.isActive = false;
        CevBreaker.forceBrk = false;
        CevBreaker.isPossible = false;
    }
    
    private static class structureTemp
    {
        public double distance;
        public int supportBlock;
        public ArrayList<Vec3d> to_place;
        public int direction;
        
        public structureTemp(final double distance, final int supportBlock, final ArrayList<Vec3d> to_place) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = -1;
        }
    }
}
