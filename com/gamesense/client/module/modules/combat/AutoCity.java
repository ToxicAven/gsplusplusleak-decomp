// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.combat;

import com.gamesense.api.util.render.RenderUtil;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.block.state.IBlockState;
import com.gamesense.api.util.world.combat.DamageUtil;
import net.minecraft.init.Blocks;
import java.util.HashMap;
import java.util.Set;
import com.gamesense.api.event.events.RenderEvent;
import java.util.Optional;
import java.util.Iterator;
import net.minecraft.network.Packet;
import net.minecraft.util.EnumFacing;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.item.Item;
import com.gamesense.api.util.player.InventoryUtil;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.init.Items;
import java.util.Collection;
import java.util.ArrayList;
import com.gamesense.api.util.world.HoleUtil;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import net.minecraft.entity.Entity;
import com.gamesense.api.util.world.EntityUtil;
import com.gamesense.api.util.player.PlayerUtil;
import java.util.function.Predicate;
import net.minecraft.block.BlockAir;
import com.gamesense.api.util.render.GSColor;
import java.util.Arrays;
import me.zero.alpine.listener.EventHandler;
import com.gamesense.api.event.events.BlockChangeEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "AutoCity", category = Category.Combat)
public class AutoCity extends Module
{
    DoubleSetting range;
    IntegerSetting down;
    IntegerSetting sides;
    IntegerSetting depth;
    DoubleSetting minDamage;
    DoubleSetting maxDamage;
    BooleanSetting ignoreCrystals;
    ModeSetting target;
    BooleanSetting switchPick;
    ModeSetting mineMode;
    ModeSetting renderMode;
    IntegerSetting width;
    ColorSetting color;
    private BlockPos blockMine;
    private BlockPos blockCrystal;
    private int oldSlot;
    private EntityPlayer aimTarget;
    private boolean isMining;
    private boolean packet;
    private boolean blockInside;
    private boolean finalY;
    private boolean noHole;
    private boolean noPossible;
    private boolean done;
    @EventHandler
    private final Listener<BlockChangeEvent> totemPopEventListener;
    
    public AutoCity() {
        this.range = this.registerDouble("Range", 6.0, 0.0, 8.0);
        this.down = this.registerInteger("Down", 1, 0, 3);
        this.sides = this.registerInteger("Sides", 1, 0, 4);
        this.depth = this.registerInteger("Depth", 3, 0, 10);
        this.minDamage = this.registerDouble("Min Damage", 5.0, 0.0, 10.0);
        this.maxDamage = this.registerDouble("Max Self Damage", 7.0, 0.0, 20.0);
        this.ignoreCrystals = this.registerBoolean("Ignore Crystals", true);
        this.target = this.registerMode("Target", Arrays.asList("Nearest", "Looking"), "Nearest");
        this.switchPick = this.registerBoolean("Switch Pick", true);
        this.mineMode = this.registerMode("Mine Mode", Arrays.asList("Packet", "Vanilla"), "Packet");
        this.renderMode = this.registerMode("Render", Arrays.asList("Outline", "Fill", "Both", "None"), "Both");
        this.width = this.registerInteger("Width", 1, 1, 10);
        this.color = this.registerColor("Color", new GSColor(102, 51, 153));
        this.totemPopEventListener = new Listener<BlockChangeEvent>(event -> {
            if (AutoCity.mc.field_71439_g != null && AutoCity.mc.field_71441_e != null) {
                if (event.getBlock() != null && event.getPosition() != null && this.blockMine != null) {
                    if (event.getPosition() == this.blockMine && event.getBlock() instanceof BlockAir) {
                        if (!this.packet && this.oldSlot != -1) {
                            AutoCity.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
                        }
                        this.done = true;
                    }
                }
            }
        }, (Predicate<BlockChangeEvent>[])new Predicate[0]);
    }
    
    public void onEnable() {
        this.aimTarget = null;
        final BlockPos blockPos = null;
        this.blockCrystal = blockPos;
        this.blockMine = blockPos;
        final boolean isMining = false;
        this.done = isMining;
        this.noPossible = isMining;
        this.noHole = isMining;
        this.finalY = isMining;
        this.blockInside = isMining;
        this.packet = isMining;
        this.isMining = isMining;
    }
    
    public void onDisable() {
        if (AutoCity.mc.field_71439_g == null) {
            return;
        }
        if (this.blockInside) {
            this.setDisabledMessage("Detected block inside... AutoCity turned OFF!");
        }
        else if (this.noHole) {
            this.setDisabledMessage("Enemy is not in a hole... AutoCity turned OFF!");
        }
        else if (this.finalY) {
            this.setDisabledMessage("Not correct y... AutoCity turned OFF!");
        }
        else if (this.noPossible) {
            this.setDisabledMessage("Enemy moved away from the hole... AutoCity turned OFF!");
        }
        else {
            this.setDisabledMessage("AutoCity turned OFF!");
        }
    }
    
    @Override
    public void onUpdate() {
        if (AutoCity.mc.field_71439_g == null || AutoCity.mc.field_71441_e == null) {
            return;
        }
        if (this.isMining) {
            if (this.done) {
                this.disable();
            }
            else if (!this.packet) {
                this.breakBlock();
            }
            return;
        }
        if (this.target.getValue().equals("Nearest")) {
            this.aimTarget = PlayerUtil.findClosestTarget(this.range.getValue(), this.aimTarget);
        }
        else if (this.target.getValue().equals("Looking")) {
            this.aimTarget = PlayerUtil.findLookingPlayer(this.range.getValue());
        }
        if (this.aimTarget == null) {
            return;
        }
        List<BlockPos> blocks = EntityUtil.getBlocksIn((Entity)this.aimTarget);
        if (blocks.size() == 0) {
            this.blockInside = true;
            this.disable();
            return;
        }
        int minY = Integer.MAX_VALUE;
        for (final BlockPos block : blocks) {
            final int y = block.func_177956_o();
            if (y < minY) {
                minY = y;
            }
        }
        if (this.aimTarget.field_70163_u % 1.0 > 0.2) {
            ++minY;
        }
        final int finalMinY = minY;
        blocks = blocks.stream().filter(blockPos -> blockPos.func_177956_o() == finalMinY).collect((Collector<? super Object, ?, List<BlockPos>>)Collectors.toList());
        final Optional<BlockPos> any = blocks.stream().findAny();
        if (!any.isPresent()) {
            this.finalY = true;
            this.disable();
            return;
        }
        final HoleUtil.HoleInfo holeInfo = HoleUtil.isHole(any.get(), false, true);
        if (holeInfo.getType() == HoleUtil.HoleType.NONE || holeInfo.getSafety() == HoleUtil.BlockSafety.UNBREAKABLE) {
            this.noHole = true;
            this.disable();
            return;
        }
        final List<BlockPos> sides = new ArrayList<BlockPos>();
        for (final BlockPos block2 : blocks) {
            sides.addAll(this.cityableSides(block2, HoleUtil.getUnsafeSides(block2).keySet(), this.aimTarget));
        }
        if (sides.size() > 0) {
            this.blockMine = sides.get(0);
            double distance = AutoCity.mc.field_71439_g.func_174818_b(this.blockMine);
            for (final BlockPos poss : sides) {
                if (AutoCity.mc.field_71439_g.func_174818_b(this.blockMine) < distance) {
                    this.blockMine = poss;
                    distance = AutoCity.mc.field_71439_g.func_174818_b(this.blockMine);
                }
            }
            if (AutoCity.mc.field_71439_g.func_184614_ca().func_77973_b() != Items.field_151046_w && this.switchPick.getValue()) {
                this.oldSlot = AutoCity.mc.field_71439_g.field_71071_by.field_70461_c;
                final int slot = InventoryUtil.findFirstItemSlot((Class<? extends Item>)ItemPickaxe.class, 0, 9);
                if (slot != 1) {
                    AutoCity.mc.field_71439_g.field_71071_by.field_70461_c = slot;
                }
            }
            final String s = this.mineMode.getValue();
            switch (s) {
                case "Packet": {
                    AutoCity.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
                    AutoCity.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, this.blockMine, EnumFacing.UP));
                    AutoCity.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.blockMine, EnumFacing.UP));
                    this.isMining = true;
                    this.packet = true;
                    AutoCity.mc.field_71439_g.field_71071_by.field_70461_c = this.oldSlot;
                }
                case "Vanilla": {
                    this.breakBlock();
                    this.isMining = true;
                    break;
                }
            }
            this.breakBlock();
            this.isMining = true;
            return;
        }
        this.noPossible = true;
        this.disable();
    }
    
    private void breakBlock() {
        AutoCity.mc.field_71439_g.func_184609_a(EnumHand.MAIN_HAND);
        AutoCity.mc.field_71442_b.func_180512_c(this.blockMine, EnumFacing.UP);
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (this.blockMine == null) {
            return;
        }
        this.renderBox(this.blockMine);
    }
    
    private List<BlockPos> cityableSides(final BlockPos centre, final Set<HoleUtil.BlockOffset> weakSides, final EntityPlayer player) {
        final List<BlockPos> cityableSides = new ArrayList<BlockPos>();
        final HashMap<BlockPos, HoleUtil.BlockOffset> directions = new HashMap<BlockPos, HoleUtil.BlockOffset>();
        for (final HoleUtil.BlockOffset weakSide : weakSides) {
            final BlockPos pos = weakSide.offset(centre);
            if (AutoCity.mc.field_71441_e.func_180495_p(pos).func_177230_c() != Blocks.field_150350_a) {
                directions.put(pos, weakSide);
            }
        }
        BlockPos pos2;
        BlockPos pos3;
        List<BlockPos> square;
        IBlockState holder;
        final Iterator<BlockPos> iterator2;
        BlockPos pos4;
        final List<BlockPos> list;
        directions.forEach((blockPos, blockOffset) -> {
            if (blockOffset == HoleUtil.BlockOffset.DOWN) {
                return;
            }
            else {
                pos2 = blockOffset.left(blockPos.func_177979_c((int)this.down.getValue()), this.sides.getValue());
                pos3 = blockOffset.forward(blockOffset.right(blockPos, this.sides.getValue()), this.depth.getValue());
                square = EntityUtil.getSquare(pos2, pos3);
                holder = AutoCity.mc.field_71441_e.func_180495_p(blockPos);
                AutoCity.mc.field_71441_e.func_175698_g(blockPos);
                square.iterator();
                while (iterator2.hasNext()) {
                    pos4 = iterator2.next();
                    if (this.canPlaceCrystal(pos4.func_177977_b(), this.ignoreCrystals.getValue()) && DamageUtil.calculateDamage(pos4.func_177958_n() + 0.5, pos4.func_177956_o(), pos4.func_177952_p() + 0.5, (Entity)player) >= this.minDamage.getValue()) {
                        if (DamageUtil.calculateDamage(pos4.func_177958_n() + 0.5, pos4.func_177956_o(), pos4.func_177952_p() + 0.5, (Entity)AutoCity.mc.field_71439_g) <= this.maxDamage.getValue()) {
                            list.add(blockPos);
                            break;
                        }
                        else {
                            break;
                        }
                    }
                }
                AutoCity.mc.field_71441_e.func_175656_a(blockPos, holder);
                return;
            }
        });
        return cityableSides;
    }
    
    private boolean canPlaceCrystal(final BlockPos blockPos, final boolean ignoreCrystal) {
        final BlockPos boost = blockPos.func_177982_a(0, 1, 0);
        final BlockPos boost2 = blockPos.func_177982_a(0, 2, 0);
        final AxisAlignedBB axisAlignedBB = new AxisAlignedBB(boost, boost2);
        if (AutoCity.mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150357_h && AutoCity.mc.field_71441_e.func_180495_p(blockPos).func_177230_c() != Blocks.field_150343_Z) {
            return false;
        }
        if (AutoCity.mc.field_71441_e.func_180495_p(boost).func_177230_c() != Blocks.field_150350_a) {
            return false;
        }
        if (AutoCity.mc.field_71441_e.func_180495_p(boost2).func_177230_c() != Blocks.field_150350_a) {
            return false;
        }
        if (!ignoreCrystal) {
            return AutoCity.mc.field_71441_e.func_72872_a((Class)Entity.class, axisAlignedBB).isEmpty();
        }
        final List<Entity> entityList = (List<Entity>)AutoCity.mc.field_71441_e.func_72872_a((Class)Entity.class, axisAlignedBB);
        entityList.removeIf(entity -> entity instanceof EntityEnderCrystal);
        return entityList.isEmpty();
    }
    
    private void renderBox(final BlockPos blockPos) {
        final GSColor gsColor1 = new GSColor(this.color.getValue(), 255);
        final GSColor gsColor2 = new GSColor(this.color.getValue(), 50);
        final String s = this.renderMode.getValue();
        switch (s) {
            case "Both": {
                RenderUtil.drawBox(blockPos, 1.0, gsColor2, 63);
                RenderUtil.drawBoundingBox(blockPos, 1.0, this.width.getValue(), gsColor1);
                break;
            }
            case "Outline": {
                RenderUtil.drawBoundingBox(blockPos, 1.0, this.width.getValue(), gsColor1);
                break;
            }
            case "Fill": {
                RenderUtil.drawBox(blockPos, 1.0, gsColor2, 63);
                break;
            }
        }
    }
}
