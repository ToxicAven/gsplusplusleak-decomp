// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.combat;

import java.util.Iterator;
import java.util.Collection;
import com.gamesense.api.util.world.HoleUtil;
import com.gamesense.api.util.world.EntityUtil;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockObsidian;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemPickaxe;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.item.ItemStack;
import net.minecraft.block.BlockLiquid;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.gamesense.api.util.player.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketEntityAction;
import com.gamesense.api.util.player.SpoofRotationUtil;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vec3d;
import com.gamesense.api.util.world.BlockUtil;
import net.minecraft.util.math.BlockPos;
import java.util.function.Predicate;
import net.minecraft.init.Blocks;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockRedstoneTorch;
import java.util.Arrays;
import net.minecraft.util.EnumFacing;
import java.util.ArrayList;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.BlockChangeEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.player.EntityPlayer;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Elevatot", category = Category.Combat)
public class Elevatot extends Module
{
    ModeSetting target;
    ModeSetting placeMode;
    IntegerSetting supportDelay;
    IntegerSetting pistonDelay;
    IntegerSetting redstoneDelay;
    IntegerSetting blocksPerTick;
    IntegerSetting tickBreakRedstone;
    DoubleSetting enemyRange;
    BooleanSetting debugMode;
    BooleanSetting trapMode;
    BooleanSetting trapAfter;
    BooleanSetting rotate;
    BooleanSetting forceBurrow;
    EntityPlayer aimTarget;
    double[][] sur_block;
    double[] enemyCoordsDouble;
    int[][] disp_surblock;
    int[] slot_mat;
    int[] enemyCoordsInt;
    int[] meCoordsInt;
    int lastStage;
    int blockPlaced;
    int tickPassedRedstone;
    int delayTimeTicks;
    boolean redstoneBlockMode;
    boolean enoughSpace;
    boolean isHole;
    boolean noMaterials;
    boolean redstoneAbovePiston;
    boolean isSneaking;
    boolean redstonePlaced;
    structureTemp toPlace;
    @EventHandler
    private final Listener<BlockChangeEvent> blockChangeEventListener;
    final ArrayList<EnumFacing> exd;
    
    public Elevatot() {
        this.target = this.registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest");
        this.placeMode = this.registerMode("Place", Arrays.asList("Torch", "Block", "Both"), "Torch");
        this.supportDelay = this.registerInteger("Support Delay", 0, 0, 8);
        this.pistonDelay = this.registerInteger("Piston Delay", 0, 0, 8);
        this.redstoneDelay = this.registerInteger("Redstone Delay", 0, 0, 8);
        this.blocksPerTick = this.registerInteger("Blocks per Tick", 4, 1, 8);
        this.tickBreakRedstone = this.registerInteger("Tick Break Redstone", 2, 0, 10);
        this.enemyRange = this.registerDouble("Range", 4.9, 0.0, 6.0);
        this.debugMode = this.registerBoolean("Debug Mode", false);
        this.trapMode = this.registerBoolean("Trap Mode", false);
        this.trapAfter = this.registerBoolean("Trap After", false);
        this.rotate = this.registerBoolean("Rotate", false);
        this.forceBurrow = this.registerBoolean("Force Burrow", false);
        this.disp_surblock = new int[][] { { 1, 0, 0 }, { -1, 0, 0 }, { 0, 0, 1 }, { 0, 0, -1 } };
        BlockPos temp;
        final int n;
        final BlockPos blockPos;
        this.blockChangeEventListener = new Listener<BlockChangeEvent>(event -> {
            if (Elevatot.mc.field_71439_g == null || Elevatot.mc.field_71441_e == null) {
                return;
            }
            else if (event.getBlock() == null || event.getPosition() == null) {
                return;
            }
            else {
                event.getPosition().func_177958_n();
                temp = this.compactBlockPos(2);
                if (n == blockPos.func_177958_n() && event.getPosition().func_177956_o() == temp.func_177956_o() && event.getPosition().func_177952_p() == temp.func_177952_p()) {
                    if (event.getBlock() instanceof BlockRedstoneTorch) {
                        if (this.tickBreakRedstone.getValue() == 0) {
                            this.breakBlock(temp);
                            this.lastStage = 2;
                        }
                        else {
                            this.lastStage = 3;
                        }
                    }
                    else if (event.getBlock() instanceof BlockAir && this.redstoneDelay.getValue() == 0) {
                        this.placeBlock(temp, 0.0, 0.0, 0.0, false, false, this.slot_mat[2], -1);
                        Elevatot.mc.field_71441_e.func_175656_a(temp, Blocks.field_150429_aA.func_176223_P());
                    }
                }
                return;
            }
        }, (Predicate<BlockChangeEvent>[])new Predicate[0]);
        this.exd = new ArrayList<EnumFacing>() {
            {
                this.add(EnumFacing.DOWN);
            }
        };
    }
    
    private void breakBlock(final BlockPos pos) {
        if (this.redstoneBlockMode) {
            Elevatot.mc.field_71439_g.field_71071_by.field_70461_c = this.slot_mat[3];
        }
        final EnumFacing side = BlockUtil.getPlaceableSide(pos);
        if (side != null) {
            if (this.rotate.getValue()) {
                final BlockPos neighbour = pos.func_177972_a(side);
                final EnumFacing opposite = side.func_176734_d();
                final Vec3d hitVec = new Vec3d((Vec3i)neighbour).func_72441_c(0.5, 0.0, 0.5).func_178787_e(new Vec3d(opposite.func_176730_m()).func_186678_a(0.5));
                BlockUtil.faceVectorPacketInstant(hitVec, true);
            }
            Elevatot.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
            Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, side));
            Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, side));
        }
    }
    
    public void onEnable() {
        SpoofRotationUtil.ROTATION_UTIL.onEnable();
        this.initValues();
        if (this.getAimTarget()) {
            return;
        }
        this.playerChecks();
    }
    
    public void onDisable() {
        if (this.isSneaking) {
            Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)Elevatot.mc.field_71439_g, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        String output = "";
        String materialsNeeded = "";
        if (this.aimTarget == null) {
            output = "No target found...";
        }
        else if (!this.isHole) {
            output = "The enemy is not in a hole...";
        }
        else if (!this.enoughSpace) {
            output = "Not enough space...";
        }
        else if (this.noMaterials) {
            output = "No materials detected...";
            materialsNeeded = this.getMissingMaterials();
        }
        this.setDisabledMessage(output + "Elevatot turned OFF!");
        if (!materialsNeeded.equals("")) {
            this.setDisabledMessage("Materials missing:" + materialsNeeded);
        }
    }
    
    String getMissingMaterials() {
        final StringBuilder output = new StringBuilder();
        if (this.slot_mat[0] == -1) {
            output.append(" Obsidian");
        }
        if (this.slot_mat[1] == -1) {
            output.append(" Piston");
        }
        if (this.slot_mat[2] == -1) {
            output.append(" Redstone");
        }
        if (this.slot_mat[3] == -1 && this.redstoneBlockMode) {
            output.append(" Pick");
        }
        if (this.slot_mat[4] == -1 && this.forceBurrow.getValue()) {
            output.append(" Skull");
        }
        return output.toString();
    }
    
    @Override
    public void onUpdate() {
        if (Elevatot.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        int toWait = 0;
        switch (this.lastStage) {
            case 0: {
                toWait = this.supportDelay.getValue();
                break;
            }
            case 1: {
                toWait = this.pistonDelay.getValue();
                break;
            }
            case 2: {
                toWait = this.redstoneDelay.getValue();
                break;
            }
            case 3: {
                toWait = this.tickBreakRedstone.getValue();
                break;
            }
            default: {
                toWait = 0;
                break;
            }
        }
        if (this.delayTimeTicks < toWait) {
            ++this.delayTimeTicks;
            return;
        }
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
            return;
        }
        if (this.checkVariable()) {
            return;
        }
        if (this.placeSupport()) {
            BlockPos temp;
            if (BlockUtil.getBlock(temp = this.compactBlockPos(1)) instanceof BlockAir) {
                this.placeBlock(temp, this.toPlace.offsetX, this.toPlace.offsetY, this.toPlace.offsetZ, false, true, this.slot_mat[1], this.toPlace.position);
                if (this.continueBlock()) {
                    this.lastStage = 1;
                    return;
                }
            }
            if (BlockUtil.getBlock(temp = this.compactBlockPos(2)) instanceof BlockAir) {
                this.placeBlock(temp, 0.0, 0.0, 0.0, false, false, this.slot_mat[2], -1);
                this.lastStage = 2;
                return;
            }
            if (this.lastStage == 3) {
                this.breakBlock(this.compactBlockPos(2));
                this.lastStage = 2;
            }
        }
    }
    
    boolean continueBlock() {
        return ++this.blockPlaced == this.blocksPerTick.getValue();
    }
    
    boolean placeSupport() {
        if (this.toPlace.supportBlock > 0) {
            if (this.forceBurrow.getValue() && BlockUtil.getBlock(this.aimTarget.func_180425_c()) instanceof BlockAir) {
                final boolean temp = this.redstoneAbovePiston;
                this.redstoneAbovePiston = true;
                this.placeBlock(this.aimTarget.func_180425_c(), 0.0, 0.0, 0.0, true, false, this.slot_mat[4], -1);
                this.redstoneAbovePiston = temp;
                if (this.continueBlock()) {
                    this.lastStage = 0;
                    return false;
                }
            }
            for (int i = 0; i < this.toPlace.supportBlock; ++i) {
                final BlockPos targetPos = this.getTargetPos(i);
                if (BlockUtil.getBlock(targetPos) instanceof BlockAir) {
                    this.placeBlock(targetPos, 0.0, 0.0, 0.0, false, false, this.slot_mat[0], -1);
                    if (this.continueBlock()) {
                        this.lastStage = 0;
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    boolean placeBlock(final BlockPos pos, final double offsetX, final double offsetY, final double offsetZ, final boolean redstone, final boolean piston, final int slot, final int position) {
        final Block block = Elevatot.mc.field_71441_e.func_180495_p(pos).func_177230_c();
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
        final Block neighbourBlock = Elevatot.mc.field_71441_e.func_180495_p(neighbour).func_177230_c();
        if (Elevatot.mc.field_71439_g.field_71071_by.func_70301_a(slot) != ItemStack.field_190927_a && Elevatot.mc.field_71439_g.field_71071_by.field_70461_c != slot) {
            if (slot == -1) {
                this.noMaterials = true;
                return false;
            }
            Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketHeldItemChange(slot));
            Elevatot.mc.field_71439_g.field_71071_by.field_70461_c = slot;
        }
        if ((!this.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketEntityAction((Entity)Elevatot.mc.field_71439_g, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        if (this.rotate.getValue()) {
            BlockUtil.faceVectorPacketInstant(hitVec, true);
        }
        else if (piston) {
            switch (position) {
                case 0: {
                    Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(0.0f, 0.0f, Elevatot.mc.field_71439_g.field_70122_E));
                    break;
                }
                case 1: {
                    Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(180.0f, 0.0f, Elevatot.mc.field_71439_g.field_70122_E));
                    break;
                }
                case 2: {
                    Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(-90.0f, 0.0f, Elevatot.mc.field_71439_g.field_70122_E));
                    break;
                }
                default: {
                    Elevatot.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Rotation(90.0f, 0.0f, Elevatot.mc.field_71439_g.field_70122_E));
                    break;
                }
            }
        }
        Elevatot.mc.field_71442_b.func_187099_a(Elevatot.mc.field_71439_g, Elevatot.mc.field_71441_e, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        Elevatot.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
        return true;
    }
    
    BlockPos getTargetPos(final int idx) {
        final BlockPos offsetPos = new BlockPos((Vec3d)this.toPlace.to_place.get(idx));
        return new BlockPos(this.enemyCoordsDouble[0] + offsetPos.func_177958_n(), this.enemyCoordsDouble[1] + offsetPos.func_177956_o(), this.enemyCoordsDouble[2] + offsetPos.func_177952_p());
    }
    
    public BlockPos compactBlockPos(final int step) {
        final BlockPos offsetPos = new BlockPos((Vec3d)this.toPlace.to_place.get(this.toPlace.supportBlock + step - 1));
        return new BlockPos(this.enemyCoordsDouble[0] + offsetPos.func_177958_n(), this.enemyCoordsDouble[1] + offsetPos.func_177956_o(), this.enemyCoordsDouble[2] + offsetPos.func_177952_p());
    }
    
    boolean checkVariable() {
        if (this.noMaterials || !this.isHole || !this.enoughSpace) {
            this.disable();
            return true;
        }
        return false;
    }
    
    void initValues() {
        this.sur_block = new double[4][3];
        this.slot_mat = new int[] { -1, -1, -1, -1, -1 };
        this.enemyCoordsDouble = new double[3];
        this.toPlace = new structureTemp(0.0, 0, null, -1);
        final boolean redstoneBlockMode = false;
        this.redstonePlaced = redstoneBlockMode;
        this.noMaterials = redstoneBlockMode;
        this.redstoneBlockMode = redstoneBlockMode;
        this.isHole = true;
        this.aimTarget = null;
        this.lastStage = -1;
        this.delayTimeTicks = 0;
    }
    
    boolean getMaterialsSlot() {
        if (this.placeMode.getValue().equals("Block")) {
            this.redstoneBlockMode = true;
        }
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Elevatot.mc.field_71439_g.field_71071_by.func_70301_a(i);
            if (stack != ItemStack.field_190927_a) {
                if (stack.func_77973_b() instanceof ItemPickaxe) {
                    this.slot_mat[3] = i;
                }
                else if (this.forceBurrow.getValue() && stack.func_77973_b() instanceof ItemSkull) {
                    this.slot_mat[4] = i;
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
                        this.slot_mat[2] = i;
                        this.redstoneBlockMode = false;
                    }
                    else if (!this.placeMode.getValue().equals("Torch") && block.field_149770_b.equals("blockRedstone")) {
                        this.slot_mat[2] = i;
                        this.redstoneBlockMode = true;
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
        if (this.debugMode.getValue()) {
            PistonCrystal.printDebug(String.format("%d %d %d %d", this.slot_mat[0], this.slot_mat[1], this.slot_mat[2], this.slot_mat[3]), false);
        }
        return count >= 3 + (this.redstoneBlockMode ? 1 : 0) + (((boolean)this.forceBurrow.getValue()) ? 1 : 0);
    }
    
    boolean getAimTarget() {
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
    
    void playerChecks() {
        if (this.getMaterialsSlot()) {
            if (this.is_in_hole()) {
                this.enemyCoordsDouble = new double[] { this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v };
                this.enemyCoordsInt = new int[] { (int)this.enemyCoordsDouble[0], (int)this.enemyCoordsDouble[1], (int)this.enemyCoordsDouble[2] };
                this.meCoordsInt = new int[] { (int)Elevatot.mc.field_71439_g.field_70165_t, (int)Elevatot.mc.field_71439_g.field_70163_u, (int)Elevatot.mc.field_71439_g.field_70161_v };
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
    
    boolean is_in_hole() {
        this.sur_block = new double[][] { { this.aimTarget.field_70165_t + 1.0, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v }, { this.aimTarget.field_70165_t - 1.0, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v }, { this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v + 1.0 }, { this.aimTarget.field_70165_t, this.aimTarget.field_70163_u, this.aimTarget.field_70161_v - 1.0 } };
        return HoleUtil.isHole(EntityUtil.getPosition((Entity)this.aimTarget), true, true).getType() != HoleUtil.HoleType.NONE;
    }
    
    boolean createStructure() {
        final structureTemp addedStructure = new structureTemp(Double.MAX_VALUE, 0, null, -1);
        for (int i = 0; i < 4; ++i) {
            final double[] pistonCoordsAbs = { this.sur_block[i][0], this.sur_block[i][1] + 1.0, this.sur_block[i][2] };
            final int[] pistonCoordsRel = { this.disp_surblock[i][0], this.disp_surblock[i][1] + 1, this.disp_surblock[i][2] };
            final double distanceNowCrystal;
            if ((distanceNowCrystal = Elevatot.mc.field_71439_g.func_70011_f(pistonCoordsAbs[0], pistonCoordsAbs[1], pistonCoordsAbs[2])) < addedStructure.distance) {
                if (BlockUtil.getBlock(pistonCoordsAbs[0], pistonCoordsAbs[1], pistonCoordsAbs[2]) instanceof BlockAir || BlockUtil.getBlock(pistonCoordsAbs[0], pistonCoordsAbs[1], pistonCoordsAbs[2]) instanceof BlockPistonBase) {
                    double[] redstoneCoordsAbs = new double[3];
                    int[] redstoneCoordsRel = new int[3];
                    double minFound = 1000.0;
                    double minNow = -1.0;
                    boolean foundOne = false;
                    for (final int[] pos : this.disp_surblock) {
                        final double[] torchCoords = { pistonCoordsAbs[0] + pos[0], pistonCoordsAbs[1], pistonCoordsAbs[2] + pos[2] };
                        if ((minNow = Elevatot.mc.field_71439_g.func_70011_f(torchCoords[0], torchCoords[1], torchCoords[2])) <= minFound) {
                            if (!PistonCrystal.someoneInCoords(torchCoords[0], torchCoords[2])) {
                                if (BlockUtil.getBlock(torchCoords[0], torchCoords[1], torchCoords[2]) instanceof BlockRedstoneTorch || BlockUtil.getBlock(torchCoords[0], torchCoords[1], torchCoords[2]) instanceof BlockAir) {
                                    redstoneCoordsAbs = new double[] { torchCoords[0], torchCoords[1], torchCoords[2] };
                                    redstoneCoordsRel = new int[] { pistonCoordsRel[0] + pos[0], pistonCoordsRel[1], pistonCoordsRel[2] + pos[2] };
                                    foundOne = true;
                                    minFound = minNow;
                                }
                            }
                        }
                    }
                    this.redstoneAbovePiston = false;
                    if (!foundOne) {
                        if (!this.redstoneBlockMode && BlockUtil.getBlock(pistonCoordsAbs[0], pistonCoordsAbs[1] + 1.0, pistonCoordsAbs[2]) instanceof BlockAir) {
                            redstoneCoordsAbs = new double[] { pistonCoordsAbs[0], pistonCoordsAbs[1] + 1.0, pistonCoordsAbs[2] };
                            redstoneCoordsRel = new int[] { pistonCoordsRel[0], pistonCoordsRel[1] + 1, pistonCoordsRel[2] };
                            this.redstoneAbovePiston = true;
                        }
                        if (!this.redstoneAbovePiston) {
                            continue;
                        }
                    }
                    final List<Vec3d> toPlaceTemp = new ArrayList<Vec3d>();
                    int supportBlock = 0;
                    if (!this.redstoneBlockMode) {
                        if (this.redstoneAbovePiston) {
                            int[] toAdd;
                            if (this.enemyCoordsInt[0] == (int)pistonCoordsAbs[0] && this.enemyCoordsInt[2] == (int)pistonCoordsAbs[2]) {
                                toAdd = new int[] { pistonCoordsRel[0], pistonCoordsRel[1], 0 };
                            }
                            else {
                                toAdd = new int[] { pistonCoordsRel[0], pistonCoordsRel[1], pistonCoordsRel[2] };
                            }
                            for (int hight = -1; hight < 2; ++hight) {
                                if (!PistonCrystal.someoneInCoords(pistonCoordsAbs[0] + toAdd[0], pistonCoordsAbs[2] + toAdd[2]) && BlockUtil.getBlock(pistonCoordsAbs[0] + toAdd[0], pistonCoordsAbs[1] + hight, pistonCoordsAbs[2] + toAdd[2]) instanceof BlockAir) {
                                    toPlaceTemp.add(new Vec3d((double)(pistonCoordsRel[0] + toAdd[0]), (double)(pistonCoordsRel[1] + hight), (double)(pistonCoordsRel[2] + toAdd[2])));
                                    ++supportBlock;
                                }
                            }
                        }
                        else if (BlockUtil.getBlock(redstoneCoordsAbs[0], redstoneCoordsAbs[1] - 1.0, redstoneCoordsAbs[2]) instanceof BlockAir) {
                            toPlaceTemp.add(new Vec3d((double)redstoneCoordsRel[0], (double)(redstoneCoordsRel[1] - 1), (double)redstoneCoordsRel[2]));
                            ++supportBlock;
                        }
                    }
                    if (this.trapMode.getValue()) {
                        toPlaceTemp.addAll(Arrays.asList(new Vec3d(-1.0, -1.0, -1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(-1.0, 1.0, -1.0), new Vec3d(-1.0, 2.0, -1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(1.0, 2.0, 1.0), new Vec3d(0.0, 2.0, 1.0)));
                        supportBlock += 10;
                    }
                    toPlaceTemp.add(new Vec3d((double)pistonCoordsRel[0], (double)pistonCoordsRel[1], (double)pistonCoordsRel[2]));
                    toPlaceTemp.add(new Vec3d((double)redstoneCoordsRel[0], (double)redstoneCoordsRel[1], (double)redstoneCoordsRel[2]));
                    int position;
                    if (this.disp_surblock[i][0] == 0) {
                        if (this.disp_surblock[i][2] == 1) {
                            position = 0;
                        }
                        else {
                            position = 1;
                        }
                    }
                    else if (this.disp_surblock[i][0] == 1) {
                        position = 2;
                    }
                    else {
                        position = 3;
                    }
                    float offsetX;
                    float offsetZ;
                    if (this.disp_surblock[i][0] != 0) {
                        offsetX = (float)this.disp_surblock[i][0];
                        if (Elevatot.mc.field_71439_g.func_70092_e(pistonCoordsAbs[0], pistonCoordsAbs[1], pistonCoordsAbs[2] + 0.5) > Elevatot.mc.field_71439_g.func_70092_e(pistonCoordsAbs[0], pistonCoordsAbs[1], pistonCoordsAbs[2] - 0.5)) {
                            offsetZ = 0.5f;
                        }
                        else {
                            offsetZ = -0.5f;
                        }
                    }
                    else {
                        offsetZ = (float)this.disp_surblock[i][2];
                        if (Elevatot.mc.field_71439_g.func_70092_e(pistonCoordsAbs[0] + 0.5, pistonCoordsAbs[1], pistonCoordsAbs[2]) > Elevatot.mc.field_71439_g.func_70092_e(pistonCoordsAbs[0] - 0.5, pistonCoordsAbs[1], pistonCoordsAbs[2])) {
                            offsetX = 0.5f;
                        }
                        else {
                            offsetX = -0.5f;
                        }
                    }
                    final float offsetY = (this.meCoordsInt[1] - this.enemyCoordsInt[1] == -1) ? 0.0f : 1.0f;
                    addedStructure.replaceValues(distanceNowCrystal, supportBlock, toPlaceTemp, -1, offsetX, offsetZ, offsetY, position);
                    this.toPlace = addedStructure;
                }
            }
        }
        if (this.debugMode.getValue() && addedStructure.to_place != null) {
            PistonCrystal.printDebug("Skeleton structure:", false);
            for (final Vec3d parte : addedStructure.to_place) {
                PistonCrystal.printDebug(String.format("%f %f %f", parte.field_72450_a, parte.field_72448_b, parte.field_72449_c), false);
            }
            PistonCrystal.printDebug(String.format("X: %f Y: %f Z: %f", this.toPlace.offsetX, this.toPlace.offsetY, this.toPlace.offsetZ), false);
        }
        return addedStructure.to_place != null;
    }
    
    class structureTemp
    {
        public double distance;
        public int supportBlock;
        public List<Vec3d> to_place;
        public int direction;
        public float offsetX;
        public float offsetY;
        public float offsetZ;
        public int position;
        
        public structureTemp(final double distance, final int supportBlock, final List<Vec3d> to_place, final int position) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = -1;
            this.position = position;
        }
        
        public void replaceValues(final double distance, final int supportBlock, final List<Vec3d> to_place, final int direction, final float offsetX, final float offsetZ, final float offsetY, final int position) {
            this.distance = distance;
            this.supportBlock = supportBlock;
            this.to_place = to_place;
            this.direction = direction;
            this.offsetX = offsetX;
            this.offsetZ = offsetZ;
            this.offsetY = offsetY;
            this.position = position;
        }
    }
}
