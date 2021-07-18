// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.movement;

import net.minecraft.block.BlockLiquid;
import net.minecraft.util.math.MathHelper;
import com.gamesense.api.util.world.HoleUtil;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.material.Material;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "HoleTP", category = Category.Movement)
public class HoleTP extends Module
{
    private int packets;
    private boolean jumped;
    private final double[] oneblockPositions;
    
    public HoleTP() {
        this.oneblockPositions = new double[] { 0.42, 0.75 };
    }
    
    @Override
    public void onUpdate() {
        if (HoleTP.mc.field_71441_e == null || HoleTP.mc.field_71439_g == null || ModuleManager.isModuleEnabled(Speed.class)) {
            return;
        }
        if (!HoleTP.mc.field_71439_g.field_70122_E) {
            if (HoleTP.mc.field_71474_y.field_74314_A.func_151470_d()) {
                this.jumped = true;
            }
        }
        else {
            this.jumped = false;
        }
        if (!this.jumped && HoleTP.mc.field_71439_g.field_70143_R < 0.5 && this.isInHole() && HoleTP.mc.field_71439_g.field_70163_u - this.getNearestBlockBelow() <= 1.125 && HoleTP.mc.field_71439_g.field_70163_u - this.getNearestBlockBelow() <= 0.95 && !this.isOnLiquid() && !this.isInLiquid()) {
            if (!HoleTP.mc.field_71439_g.field_70122_E) {
                ++this.packets;
            }
            if (!HoleTP.mc.field_71439_g.field_70122_E && !HoleTP.mc.field_71439_g.func_70055_a(Material.field_151586_h) && !HoleTP.mc.field_71439_g.func_70055_a(Material.field_151587_i) && !HoleTP.mc.field_71474_y.field_74314_A.func_151470_d() && !HoleTP.mc.field_71439_g.func_70617_f_() && this.packets > 0) {
                final BlockPos blockPos = new BlockPos(HoleTP.mc.field_71439_g.field_70165_t, HoleTP.mc.field_71439_g.field_70163_u, HoleTP.mc.field_71439_g.field_70161_v);
                for (final double position : this.oneblockPositions) {
                    HoleTP.mc.field_71439_g.field_71174_a.func_147297_a((Packet)new CPacketPlayer.Position((double)(blockPos.func_177958_n() + 0.5f), HoleTP.mc.field_71439_g.field_70163_u - position, (double)(blockPos.func_177952_p() + 0.5f), true));
                }
                HoleTP.mc.field_71439_g.func_70107_b((double)(blockPos.func_177958_n() + 0.5f), this.getNearestBlockBelow() + 0.1, (double)(blockPos.func_177952_p() + 0.5f));
                this.packets = 0;
            }
        }
    }
    
    private boolean isInHole() {
        final BlockPos blockPos = new BlockPos(HoleTP.mc.field_71439_g.field_70165_t, HoleTP.mc.field_71439_g.field_70163_u, HoleTP.mc.field_71439_g.field_70161_v);
        final IBlockState blockState = HoleTP.mc.field_71441_e.func_180495_p(blockPos);
        return this.isBlockValid(blockState, blockPos);
    }
    
    private double getNearestBlockBelow() {
        for (int y = (int)Math.floor(HoleTP.mc.field_71439_g.field_70163_u); y > 0.0; --y) {
            if (!(HoleTP.mc.field_71441_e.func_180495_p(new BlockPos(HoleTP.mc.field_71439_g.field_70165_t, (double)y, HoleTP.mc.field_71439_g.field_70161_v)).func_177230_c() instanceof BlockSlab) && HoleTP.mc.field_71441_e.func_180495_p(new BlockPos(HoleTP.mc.field_71439_g.field_70165_t, (double)y, HoleTP.mc.field_71439_g.field_70161_v)).func_177230_c().func_176223_P().func_185890_d((IBlockAccess)HoleTP.mc.field_71441_e, new BlockPos(0, 0, 0)) != null) {
                return y + 1;
            }
        }
        return -1.0;
    }
    
    private boolean isBlockValid(final IBlockState blockState, final BlockPos blockPos) {
        return blockState.func_177230_c() == Blocks.field_150350_a && HoleTP.mc.field_71439_g.func_174818_b(blockPos) >= 1.0 && HoleTP.mc.field_71441_e.func_180495_p(blockPos.func_177984_a()).func_177230_c() == Blocks.field_150350_a && HoleTP.mc.field_71441_e.func_180495_p(blockPos.func_177981_b(2)).func_177230_c() == Blocks.field_150350_a && this.isSafeHole(blockPos);
    }
    
    private boolean isSafeHole(final BlockPos blockPos) {
        return HoleUtil.isHole(blockPos, true, false).getType() != HoleUtil.HoleType.NONE;
    }
    
    private boolean isOnLiquid() {
        final double y = HoleTP.mc.field_71439_g.field_70163_u - 0.03;
        for (int x = MathHelper.func_76128_c(HoleTP.mc.field_71439_g.field_70165_t); x < MathHelper.func_76143_f(HoleTP.mc.field_71439_g.field_70165_t); ++x) {
            for (int z = MathHelper.func_76128_c(HoleTP.mc.field_71439_g.field_70161_v); z < MathHelper.func_76143_f(HoleTP.mc.field_71439_g.field_70161_v); ++z) {
                final BlockPos pos = new BlockPos(x, MathHelper.func_76128_c(y), z);
                if (HoleTP.mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private boolean isInLiquid() {
        final double y = HoleTP.mc.field_71439_g.field_70163_u + 0.01;
        for (int x = MathHelper.func_76128_c(HoleTP.mc.field_71439_g.field_70165_t); x < MathHelper.func_76143_f(HoleTP.mc.field_71439_g.field_70165_t); ++x) {
            for (int z = MathHelper.func_76128_c(HoleTP.mc.field_71439_g.field_70161_v); z < MathHelper.func_76143_f(HoleTP.mc.field_71439_g.field_70161_v); ++z) {
                final BlockPos pos = new BlockPos(x, (int)y, z);
                if (HoleTP.mc.field_71441_e.func_180495_p(pos).func_177230_c() instanceof BlockLiquid) {
                    return true;
                }
            }
        }
        return false;
    }
}
