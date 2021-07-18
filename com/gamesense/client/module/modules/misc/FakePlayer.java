// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import net.minecraft.init.Enchantments;
import net.minecraft.world.GameType;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.item.Item;
import net.minecraft.init.Items;
import com.gamesense.api.setting.values.BooleanSetting;
import net.minecraft.item.ItemStack;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "FakePlayer", category = Category.Misc)
public class FakePlayer extends Module
{
    private final ItemStack[] armors;
    BooleanSetting playerStacked;
    
    public FakePlayer() {
        this.armors = new ItemStack[] { new ItemStack((Item)Items.field_151175_af), new ItemStack((Item)Items.field_151173_ae), new ItemStack((Item)Items.field_151163_ad), new ItemStack((Item)Items.field_151161_ac) };
        this.playerStacked = this.registerBoolean("Player Stacked", false);
    }
    
    public void onEnable() {
        if (FakePlayer.mc.field_71439_g == null || FakePlayer.mc.field_71439_g.field_70128_L) {
            this.disable();
            return;
        }
        final EntityOtherPlayerMP clonedPlayer = new EntityOtherPlayerMP((World)FakePlayer.mc.field_71441_e, new GameProfile(UUID.fromString("fdee323e-7f0c-4c15-8d1c-0f277442342a"), "Fit"));
        clonedPlayer.func_82149_j((Entity)FakePlayer.mc.field_71439_g);
        clonedPlayer.field_70759_as = FakePlayer.mc.field_71439_g.field_70759_as;
        clonedPlayer.field_70177_z = FakePlayer.mc.field_71439_g.field_70177_z;
        clonedPlayer.field_70125_A = FakePlayer.mc.field_71439_g.field_70125_A;
        clonedPlayer.func_71033_a(GameType.SURVIVAL);
        clonedPlayer.func_70606_j(20.0f);
        FakePlayer.mc.field_71441_e.func_73027_a(-1234, (Entity)clonedPlayer);
        if (this.playerStacked.getValue()) {
            for (int i = 0; i < 4; ++i) {
                final ItemStack item = this.armors[i];
                item.func_77966_a((i == 2) ? Enchantments.field_185297_d : Enchantments.field_180310_c, 4);
                clonedPlayer.field_71071_by.field_70460_b.set(i, (Object)item);
            }
        }
        clonedPlayer.func_70636_d();
    }
    
    public void onDisable() {
        if (FakePlayer.mc.field_71441_e != null) {
            FakePlayer.mc.field_71441_e.func_73028_b(-1234);
        }
    }
}
