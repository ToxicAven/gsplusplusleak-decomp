// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.combat;

import net.minecraft.item.ItemSkull;
import com.gamesense.api.util.player.InventoryUtil;
import net.minecraft.util.EnumHand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.BlockPos;
import com.gamesense.api.util.world.HoleUtil;
import net.minecraft.entity.Entity;
import com.gamesense.api.util.world.EntityUtil;
import net.minecraft.block.BlockSkull;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import com.gamesense.api.util.player.PlayerUtil;
import com.gamesense.api.util.world.BlockUtil;
import com.gamesense.api.util.player.PlacementUtil;
import com.gamesense.api.util.player.SpoofRotationUtil;
import net.minecraft.util.math.Vec2f;
import java.util.function.Predicate;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.api.util.player.PlayerPacket;
import com.gamesense.api.util.player.RotationUtil;
import com.gamesense.api.event.Phase;
import net.minecraft.util.EnumFacing;
import java.util.ArrayList;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.OnUpdateWalkingPlayerEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.Vec3d;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "AutoSkull", category = Category.Combat)
public class AutoSkull extends Module
{
    BooleanSetting rotate;
    BooleanSetting offHandSkull;
    BooleanSetting onShift;
    BooleanSetting instaActive;
    BooleanSetting disableAfter;
    BooleanSetting forceRotation;
    BooleanSetting noUp;
    BooleanSetting onlyHoles;
    IntegerSetting tickDelay;
    IntegerSetting preSwitch;
    IntegerSetting afterSwitch;
    DoubleSetting playerDistance;
    BooleanSetting autoTrap;
    IntegerSetting BlocksPerTick;
    BooleanSetting phase;
    BooleanSetting ServerRespond;
    BooleanSetting predictPhase;
    IntegerSetting maxTickTries;
    BooleanSetting centerPlayer;
    private static final Vec3d[] AIR;
    private int delayTimeTicks;
    private boolean noObby;
    private boolean activedBefore;
    private int oldSlot;
    private Vec3d lastHitVec;
    private int preRotationTick;
    private int afterRotationTick;
    private int stage;
    private boolean toPhase;
    private boolean alrPlaced;
    private int tickTry;
    private Vec3d centeredBlock;
    @EventHandler
    private final Listener<OnUpdateWalkingPlayerEvent> onUpdateWalkingPlayerEventListener;
    private boolean firstShift;
    private int resetPhase;
    private final ArrayList<EnumFacing> exd;
    
    public AutoSkull() {
        this.rotate = this.registerBoolean("Rotate", true);
        this.offHandSkull = this.registerBoolean("OffHand Skull", false);
        this.onShift = this.registerBoolean("On Shift", false);
        this.instaActive = this.registerBoolean("Insta Active", true);
        this.disableAfter = this.registerBoolean("Disable After", true);
        this.forceRotation = this.registerBoolean("Force Rotation", false);
        this.noUp = this.registerBoolean("No Up", false);
        this.onlyHoles = this.registerBoolean("Only Holes", false);
        this.tickDelay = this.registerInteger("Tick Delay", 5, 0, 10);
        this.preSwitch = this.registerInteger("Pre Switch", 0, 0, 20);
        this.afterSwitch = this.registerInteger("After Switch", 0, 0, 20);
        this.playerDistance = this.registerDouble("Player Distance", 0.0, 0.0, 6.0);
        this.autoTrap = this.registerBoolean("AutoTrap", false);
        this.BlocksPerTick = this.registerInteger("Blocks Per Tick", 4, 0, 10);
        this.phase = this.registerBoolean("Phase", true);
        this.ServerRespond = this.registerBoolean("Server Respond", true);
        this.predictPhase = this.registerBoolean("Predict Phase", true);
        this.maxTickTries = this.registerInteger("Max Tick Try", 100, 1, 200);
        this.centerPlayer = this.registerBoolean("Center Player", false);
        this.delayTimeTicks = 0;
        this.lastHitVec = new Vec3d(-1.0, -1.0, -1.0);
        this.centeredBlock = Vec3d.field_186680_a;
        Vec2f rotation;
        PlayerPacket packet;
        this.onUpdateWalkingPlayerEventListener = new Listener<OnUpdateWalkingPlayerEvent>(event -> {
            if (event.getPhase() != Phase.PRE || !this.rotate.getValue() || this.lastHitVec == null || !this.forceRotation.getValue()) {
                return;
            }
            else {
                rotation = RotationUtil.getRotationTo(this.lastHitVec);
                packet = new PlayerPacket(this, rotation);
                PlayerPacketManager.INSTANCE.addPacket(packet);
                return;
            }
        }, (Predicate<OnUpdateWalkingPlayerEvent>[])new Predicate[0]);
        this.exd = new ArrayList<EnumFacing>() {
            {
                this.add(EnumFacing.DOWN);
                this.add(EnumFacing.UP);
            }
        };
    }
    
    public void onEnable() {
        SpoofRotationUtil.ROTATION_UTIL.onEnable();
        PlacementUtil.onEnable();
        if (AutoSkull.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        final boolean noObby = false;
        this.toPhase = noObby;
        this.activedBefore = noObby;
        this.alrPlaced = noObby;
        this.firstShift = noObby;
        this.noObby = noObby;
        this.lastHitVec = null;
        final int preRotationTick = 0;
        this.tickTry = preRotationTick;
        this.resetPhase = preRotationTick;
        this.stage = preRotationTick;
        this.afterRotationTick = preRotationTick;
        this.preRotationTick = preRotationTick;
        if (this.centerPlayer.getValue() && AutoSkull.mc.field_71439_g.field_70122_E) {
            AutoSkull.mc.field_71439_g.field_70159_w = 0.0;
            AutoSkull.mc.field_71439_g.field_70179_y = 0.0;
        }
        this.centeredBlock = BlockUtil.getCenterOfBlock(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u, AutoSkull.mc.field_71439_g.field_70163_u);
    }
    
    public void onDisable() {
        SpoofRotationUtil.ROTATION_UTIL.onDisable();
        PlacementUtil.onDisable();
        if (AutoSkull.mc.field_71439_g == null) {
            return;
        }
        if (this.noObby) {
            this.setDisabledMessage("Skull not found... AutoSkull turned OFF!");
        }
        if (this.offHandSkull.getValue()) {
            OffHand.removeItem(1);
        }
    }
    
    @Override
    public void onUpdate() {
        if (AutoSkull.mc.field_71439_g == null) {
            this.disable();
            return;
        }
        if (this.noObby) {
            this.disable();
            return;
        }
        if (this.delayTimeTicks < this.tickDelay.getValue()) {
            ++this.delayTimeTicks;
        }
        else {
            this.delayTimeTicks = 0;
            if (this.centerPlayer.getValue() && this.centeredBlock != Vec3d.field_186680_a && AutoSkull.mc.field_71439_g.field_70122_E) {
                PlayerUtil.centerPlayer(this.centeredBlock);
            }
            if (this.toPhase) {
                if (++this.tickTry == this.maxTickTries.getValue()) {
                    AutoSkull.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(AutoSkull.mc.field_71439_g.field_70165_t, (double)((int)AutoSkull.mc.field_71439_g.field_70163_u + 1), AutoSkull.mc.field_71439_g.field_70161_v, AutoSkull.mc.field_71439_g.field_70177_z, AutoSkull.mc.field_71439_g.field_70125_A, AutoSkull.mc.field_71439_g.field_70122_E));
                    AutoSkull.mc.field_71439_g.field_70163_u = (int)AutoSkull.mc.field_71439_g.field_70163_u + 1;
                    this.disable();
                }
                if (BlockUtil.getBlock(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u, AutoSkull.mc.field_71439_g.field_70161_v) instanceof BlockSkull) {
                    if (!AutoSkull.mc.field_71439_g.field_70122_E) {
                        AutoSkull.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u - (AutoSkull.mc.field_71439_g.field_70163_u - (int)AutoSkull.mc.field_71439_g.field_70163_u), AutoSkull.mc.field_71439_g.field_70161_v, AutoSkull.mc.field_71439_g.field_70177_z, AutoSkull.mc.field_71439_g.field_70125_A, AutoSkull.mc.field_71439_g.field_70122_E));
                        return;
                    }
                    switch (this.stage) {
                        case 0: {
                            AutoSkull.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u - 1.0, AutoSkull.mc.field_71439_g.field_70161_v, AutoSkull.mc.field_71439_g.field_70177_z, AutoSkull.mc.field_71439_g.field_70125_A, AutoSkull.mc.field_71439_g.field_70122_E));
                            AutoSkull.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u + 1000.0, AutoSkull.mc.field_71439_g.field_70161_v, AutoSkull.mc.field_71439_g.field_70177_z, AutoSkull.mc.field_71439_g.field_70125_A, AutoSkull.mc.field_71439_g.field_70122_E));
                            break;
                        }
                    }
                }
                else {
                    if (BlockUtil.getBlock(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u, AutoSkull.mc.field_71439_g.field_70161_v).field_149787_q && AutoSkull.mc.field_71439_g.field_70163_u - (int)AutoSkull.mc.field_71439_g.field_70163_u <= 0.5) {
                        this.disable();
                    }
                    if (AutoSkull.mc.field_71439_g.field_70163_u - (int)AutoSkull.mc.field_71439_g.field_70163_u > 0.5) {
                        this.placeBlock(false);
                    }
                    else if (++this.resetPhase == 50) {
                        this.disable();
                    }
                }
                return;
            }
            if (this.onlyHoles.getValue() && HoleUtil.isHole(EntityUtil.getPosition((Entity)AutoSkull.mc.field_71439_g), true, true).getType() == HoleUtil.HoleType.NONE) {
                return;
            }
            SpoofRotationUtil.ROTATION_UTIL.shouldSpoofAngles(true);
            if (this.autoTrap.getValue() && BlockUtil.getBlock(new BlockPos((Vec3i)AutoSkull.mc.field_71439_g.func_180425_c().func_177963_a(0.0, 0.4, 0.0))) instanceof BlockSkull) {
                final EntityPlayer closest = PlayerUtil.findClosestTarget(2.0, null);
                if (closest != null && (int)closest.field_70165_t == (int)AutoSkull.mc.field_71439_g.field_70165_t && (int)closest.field_70161_v == (int)AutoSkull.mc.field_71439_g.field_70161_v && closest.field_70163_u > AutoSkull.mc.field_71439_g.field_70163_u && closest.field_70163_u < AutoSkull.mc.field_71439_g.field_70163_u + 2.0) {
                    for (int blocksPlaced = 0, offsetSteps = 0; blocksPlaced <= this.BlocksPerTick.getValue() && offsetSteps < 10; ++offsetSteps) {
                        final BlockPos offsetPos = new BlockPos(AutoSkull.AIR[offsetSteps]);
                        final BlockPos targetPos = AutoSkull.mc.field_71439_g.func_180425_c().func_177982_a(offsetPos.func_177958_n(), offsetPos.func_177956_o(), offsetPos.func_177952_p());
                        if (this.placeBlock(targetPos)) {
                            ++blocksPlaced;
                        }
                    }
                }
            }
            if (this.instaActive.getValue()) {
                this.placeBlock(true);
                return;
            }
            if (this.onShift.getValue() && AutoSkull.mc.field_71474_y.field_74311_E.func_151470_d()) {
                if (!this.firstShift) {
                    this.placeBlock(true);
                }
                return;
            }
            if (this.firstShift && !AutoSkull.mc.field_71474_y.field_74311_E.func_151470_d()) {
                this.firstShift = false;
            }
            if (this.playerDistance.getValue() != 0.0 && PlayerUtil.findClosestTarget(this.playerDistance.getValue(), null) != null) {
                this.placeBlock(true);
            }
        }
    }
    
    private boolean placeBlock(final BlockPos pos) {
        final EnumHand handSwing = EnumHand.MAIN_HAND;
        final int obsidianSlot = InventoryUtil.findObsidianSlot(false, false);
        if (AutoSkull.mc.field_71439_g.field_71071_by.field_70461_c != obsidianSlot && obsidianSlot != 9) {
            AutoSkull.mc.field_71439_g.field_71071_by.field_70461_c = obsidianSlot;
        }
        return PlacementUtil.place(pos, handSwing, this.rotate.getValue(), true);
    }
    
    private void placeBlock(final boolean changeStatus) {
        final BlockPos pos = new BlockPos(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u + 0.4, AutoSkull.mc.field_71439_g.field_70161_v);
        if (AutoSkull.mc.field_71441_e.func_180495_p(pos).func_185904_a().func_76222_j()) {
            EnumHand handSwing = EnumHand.MAIN_HAND;
            final int skullSlot = InventoryUtil.findSkullSlot(this.offHandSkull.getValue(), this.activedBefore);
            if (skullSlot == -1) {
                this.noObby = true;
                return;
            }
            if (skullSlot == 9) {
                if (!(AutoSkull.mc.field_71439_g.func_184592_cb().func_77973_b() instanceof ItemSkull)) {
                    return;
                }
                handSwing = EnumHand.OFF_HAND;
            }
            if (AutoSkull.mc.field_71439_g.field_71071_by.field_70461_c != skullSlot && skullSlot != 9) {
                this.oldSlot = AutoSkull.mc.field_71439_g.field_71071_by.field_70461_c;
                AutoSkull.mc.field_71439_g.field_71071_by.field_70461_c = skullSlot;
            }
            if (this.preSwitch.getValue() > 0 && this.preRotationTick++ == this.preSwitch.getValue()) {
                this.lastHitVec = new Vec3d((double)pos.field_177962_a, (double)pos.field_177960_b, (double)pos.field_177961_c);
                return;
            }
            Label_0352: {
                if (!this.alrPlaced || !changeStatus) {
                    if (this.noUp.getValue()) {
                        if (PlacementUtil.place(pos, handSwing, this.rotate.getValue(), this.exd)) {
                            break Label_0352;
                        }
                        if (PlacementUtil.place(pos, handSwing, this.rotate.getValue())) {
                            break Label_0352;
                        }
                    }
                    else if (PlacementUtil.place(pos, handSwing, this.rotate.getValue())) {
                        break Label_0352;
                    }
                    this.lastHitVec = null;
                    return;
                }
            }
            this.alrPlaced = true;
            if (this.afterSwitch.getValue() > 0 && this.afterRotationTick++ == this.afterSwitch.getValue()) {
                this.lastHitVec = new Vec3d((double)pos.field_177962_a, (double)pos.field_177960_b, (double)pos.field_177961_c);
                return;
            }
            if (this.oldSlot != -1) {
                AutoSkull.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
                this.oldSlot = -1;
            }
            if (changeStatus) {
                this.firstShift = true;
                final boolean b = true;
                this.alrPlaced = b;
                this.activedBefore = b;
                if (this.offHandSkull.getValue()) {
                    OffHand.removeItem(1);
                }
                if (this.disableAfter.getValue() && !this.phase.getValue()) {
                    this.disable();
                }
                if (this.phase.getValue()) {
                    this.toPhase = true;
                    this.stage = 0;
                    if (this.ServerRespond.getValue()) {
                        AutoSkull.mc.field_71441_e.func_175698_g(new BlockPos(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u, AutoSkull.mc.field_71439_g.field_70161_v));
                    }
                    if (this.predictPhase.getValue()) {
                        AutoSkull.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.PositionRotation(AutoSkull.mc.field_71439_g.field_70165_t, AutoSkull.mc.field_71439_g.field_70163_u + 1.0, AutoSkull.mc.field_71439_g.field_70161_v, AutoSkull.mc.field_71439_g.field_70177_z, AutoSkull.mc.field_71439_g.field_70125_A, AutoSkull.mc.field_71439_g.field_70122_E));
                    }
                }
                final int n = 0;
                this.afterRotationTick = n;
                this.preRotationTick = n;
                this.lastHitVec = null;
                this.centeredBlock = Vec3d.field_186680_a;
            }
        }
    }
    
    static {
        AIR = new Vec3d[] { new Vec3d(-1.0, -1.0, -1.0), new Vec3d(-1.0, 0.0, -1.0), new Vec3d(-1.0, 1.0, -1.0), new Vec3d(-1.0, 2.0, -1.0), new Vec3d(-1.0, 2.0, 0.0), new Vec3d(0.0, 2.0, -1.0), new Vec3d(1.0, 2.0, -1.0), new Vec3d(1.0, 2.0, 0.0), new Vec3d(1.0, 2.0, 1.0), new Vec3d(0.0, 2.0, 1.0) };
    }
}
