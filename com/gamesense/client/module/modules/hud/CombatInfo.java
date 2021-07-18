// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.hud;

import com.gamesense.client.module.modules.combat.OffHand;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import java.util.Iterator;
import java.util.List;
import com.gamesense.api.util.world.combat.CrystalUtil;
import net.minecraft.init.Blocks;
import net.minecraft.entity.player.EntityPlayer;
import java.util.ArrayList;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.Comparator;
import com.gamesense.api.util.player.social.SocialManager;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import java.util.function.ToIntFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.init.Items;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.combat.AutoCrystal;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import com.gamesense.api.util.render.GSColor;
import java.util.Arrays;
import net.minecraft.util.math.BlockPos;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.HUDModule;

@Module.Declaration(name = "CombatInfo", category = Category.HUD)
@Declaration(posX = 0, posZ = 150)
public class CombatInfo extends HUDModule
{
    ModeSetting infoType;
    ColorSetting color1;
    ColorSetting color2;
    private final InfoList list;
    private static final BlockPos[] surroundOffset;
    private static final String[] hoosiersModules;
    private static final String[] hoosiersNames;
    
    public CombatInfo() {
        this.infoType = this.registerMode("Type", Arrays.asList("Cyber", "Hoosiers"), "Hoosiers");
        this.color1 = this.registerColor("On", new GSColor(0, 255, 0, 255));
        this.color2 = this.registerColor("Off", new GSColor(255, 0, 0, 255));
        this.list = new InfoList();
    }
    
    @Override
    public void populate(final Theme theme) {
        this.component = new ListComponent(this.getName(), theme.getPanelRenderer(), this.position, this.list);
    }
    
    @Override
    public void onRender() {
        final AutoCrystal autoCrystal = ModuleManager.getModule(AutoCrystal.class);
        this.list.totems = CombatInfo.mc.field_71439_g.field_71071_by.field_70462_a.stream().filter(itemStack -> itemStack.func_77973_b() == Items.field_190929_cY).mapToInt(ItemStack::func_190916_E).sum() + ((CombatInfo.mc.field_71439_g.func_184592_cb().func_77973_b() == Items.field_190929_cY) ? 1 : 0);
        this.list.players = (EntityOtherPlayerMP)CombatInfo.mc.field_71441_e.field_72996_f.stream().filter(entity -> entity instanceof EntityOtherPlayerMP).filter(entity -> !SocialManager.isFriend(entity.func_70005_c_())).filter(e -> CombatInfo.mc.field_71439_g.func_70032_d(e) <= autoCrystal.placeRange.getValue()).map(entity -> entity).min(Comparator.comparing(cl -> CombatInfo.mc.field_71439_g.func_70032_d(cl))).orElse(null);
        this.list.renderLby = false;
        final List<EntityPlayer> entities = new ArrayList<EntityPlayer>((Collection<? extends EntityPlayer>)CombatInfo.mc.field_71441_e.field_73010_i.stream().filter(entityPlayer -> !SocialManager.isFriend(entityPlayer.func_70005_c_())).collect(Collectors.toList()));
        for (final EntityPlayer e2 : entities) {
            int i = 0;
            for (final BlockPos add : CombatInfo.surroundOffset) {
                ++i;
                final BlockPos o = new BlockPos(e2.func_174791_d().field_72450_a, e2.func_174791_d().field_72448_b, e2.func_174791_d().field_72449_c).func_177982_a(add.func_177958_n(), add.func_177956_o(), add.func_177952_p());
                if (CombatInfo.mc.field_71441_e.func_180495_p(o).func_177230_c() == Blocks.field_150343_Z) {
                    if (i == 1 && CrystalUtil.canPlaceCrystal(o.func_177964_d(1).func_177977_b(), autoCrystal.endCrystalMode.getValue())) {
                        this.list.lby = true;
                        this.list.renderLby = true;
                    }
                    else if (i == 2 && CrystalUtil.canPlaceCrystal(o.func_177965_g(1).func_177977_b(), autoCrystal.endCrystalMode.getValue())) {
                        this.list.lby = true;
                        this.list.renderLby = true;
                    }
                    else if (i == 3 && CrystalUtil.canPlaceCrystal(o.func_177970_e(1).func_177977_b(), autoCrystal.endCrystalMode.getValue())) {
                        this.list.lby = true;
                        this.list.renderLby = true;
                    }
                    else if (i == 4 && CrystalUtil.canPlaceCrystal(o.func_177985_f(1).func_177977_b(), autoCrystal.endCrystalMode.getValue())) {
                        this.list.lby = true;
                        this.list.renderLby = true;
                    }
                }
                else {
                    this.list.lby = false;
                    this.list.renderLby = true;
                }
            }
        }
    }
    
    private static int getPing() {
        int p = -1;
        if (CombatInfo.mc.field_71439_g == null || CombatInfo.mc.func_147114_u() == null || CombatInfo.mc.func_147114_u().func_175104_a(CombatInfo.mc.field_71439_g.func_70005_c_()) == null) {
            p = -1;
        }
        else {
            p = CombatInfo.mc.func_147114_u().func_175104_a(CombatInfo.mc.field_71439_g.func_70005_c_()).func_178853_c();
        }
        return p;
    }
    
    static {
        surroundOffset = new BlockPos[] { new BlockPos(0, 0, -1), new BlockPos(1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(-1, 0, 0) };
        hoosiersModules = new String[] { "AutoCrystal", "KillAura", "Surround", "AutoTrap", "SelfTrap" };
        hoosiersNames = new String[] { "AC", "KA", "SU", "AT", "ST" };
    }
    
    private class InfoList implements HUDList
    {
        public int totems;
        public EntityOtherPlayerMP players;
        public boolean renderLby;
        public boolean lby;
        
        private InfoList() {
            this.totems = 0;
            this.players = null;
            this.renderLby = false;
            this.lby = false;
        }
        
        @Override
        public int getSize() {
            if (CombatInfo.this.infoType.getValue().equals("Hoosiers")) {
                return CombatInfo.hoosiersModules.length;
            }
            if (CombatInfo.this.infoType.getValue().equals("Cyber")) {
                return this.renderLby ? 6 : 5;
            }
            return 0;
        }
        
        @Override
        public String getItem(final int index) {
            if (CombatInfo.this.infoType.getValue().equals("Hoosiers")) {
                if (ModuleManager.isModuleEnabled(CombatInfo.hoosiersModules[index])) {
                    return CombatInfo.hoosiersNames[index] + ": ON";
                }
                return CombatInfo.hoosiersNames[index] + ": OFF";
            }
            else {
                if (!CombatInfo.this.infoType.getValue().equals("Cyber")) {
                    return "";
                }
                if (index == 0) {
                    return "gamesense.cc";
                }
                if (index == 1) {
                    return "HTR";
                }
                if (index == 2) {
                    return "PLR";
                }
                if (index == 3) {
                    return "" + this.totems;
                }
                if (index == 4) {
                    return "PING " + getPing();
                }
                return "LBY";
            }
        }
        
        @Override
        public Color getItemColor(final int index) {
            final AutoCrystal autoCrystal = ModuleManager.getModule(AutoCrystal.class);
            if (CombatInfo.this.infoType.getValue().equals("Hoosiers")) {
                if (ModuleManager.isModuleEnabled(CombatInfo.hoosiersModules[index])) {
                    return CombatInfo.this.color1.getValue();
                }
                return CombatInfo.this.color2.getValue();
            }
            else {
                if (!CombatInfo.this.infoType.getValue().equals("Cyber")) {
                    return new Color(255, 255, 255);
                }
                boolean on = false;
                if (index == 0) {
                    on = true;
                }
                else if (index == 1) {
                    if (this.players != null) {
                        on = (CombatInfo.mc.field_71439_g.func_70032_d((Entity)this.players) <= autoCrystal.breakRange.getValue());
                    }
                }
                else if (index == 2) {
                    if (this.players != null) {
                        on = (CombatInfo.mc.field_71439_g.func_70032_d((Entity)this.players) <= autoCrystal.placeRange.getValue());
                    }
                }
                else if (index == 3) {
                    on = (this.totems > 0 && ModuleManager.isModuleEnabled(OffHand.class));
                }
                else if (index == 4) {
                    on = (getPing() <= 100);
                }
                else {
                    on = this.lby;
                }
                if (on) {
                    return CombatInfo.this.color1.getValue();
                }
                return CombatInfo.this.color2.getValue();
            }
        }
        
        @Override
        public boolean sortUp() {
            return false;
        }
        
        @Override
        public boolean sortRight() {
            return false;
        }
    }
}
