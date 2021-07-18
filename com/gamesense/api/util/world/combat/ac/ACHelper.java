// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.world.combat.ac;

import java.util.concurrent.Executors;
import net.minecraft.world.World;
import com.gamesense.client.GameSense;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayer;
import com.gamesense.api.util.world.combat.CrystalUtil;
import com.gamesense.api.util.world.combat.DamageUtil;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Callable;
import com.gamesense.api.util.world.combat.ac.threads.ACCalculate;
import java.util.function.Predicate;
import net.minecraft.entity.Entity;
import com.gamesense.api.util.world.EntityUtil;
import java.util.ArrayList;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import me.zero.alpine.listener.Listener;
import net.minecraft.util.math.BlockPos;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.entity.item.EntityEnderCrystal;
import java.util.List;
import net.minecraft.client.Minecraft;

public enum ACHelper
{
    INSTANCE;
    
    private static final Minecraft mc;
    private static final List<CrystalInfo.PlaceInfo> EMPTY_LIST;
    private static final EntityEnderCrystal GENERIC_CRYSTAL;
    public static final ThreadPoolExecutor executor;
    private static final ExecutorService mainExecutors;
    private Future<List<CrystalInfo.PlaceInfo>> mainThreadOutput;
    private final ConcurrentHashMap<BlockPos, EntityEnderCrystal> placedCrystals;
    private ACSettings settings;
    private List<BlockPos> possiblePlacements;
    private List<EntityEnderCrystal> targetableCrystals;
    private final List<PlayerInfo> targetsInfo;
    private List<BlockPos> threadPlacements;
    @EventHandler
    private final Listener<EntityJoinWorldEvent> entitySpawnListener;
    
    private ACHelper() {
        this.placedCrystals = new ConcurrentHashMap<BlockPos, EntityEnderCrystal>();
        this.settings = null;
        this.possiblePlacements = new ArrayList<BlockPos>();
        this.targetableCrystals = new ArrayList<EntityEnderCrystal>();
        this.targetsInfo = new ArrayList<PlayerInfo>();
        this.threadPlacements = new ArrayList<BlockPos>();
        final Entity entity;
        EntityEnderCrystal crystal;
        BlockPos crystalPos;
        this.entitySpawnListener = new Listener<EntityJoinWorldEvent>(event -> {
            entity = event.getEntity();
            if (entity instanceof EntityEnderCrystal && this.settings != null && this.settings.breakMode.equalsIgnoreCase("Own")) {
                crystal = (EntityEnderCrystal)entity;
                crystalPos = EntityUtil.getPosition((Entity)crystal);
                synchronized (this.placedCrystals) {
                    this.placedCrystals.computeIfPresent(crystalPos, (i, j) -> crystal);
                }
            }
        }, (Predicate<EntityJoinWorldEvent>[])new Predicate[0]);
    }
    
    public void startCalculations(final long timeout) {
        if (this.mainThreadOutput != null) {
            this.mainThreadOutput.cancel(true);
        }
        this.mainThreadOutput = ACHelper.mainExecutors.submit((Callable<List<CrystalInfo.PlaceInfo>>)new ACCalculate(this.settings, this.targetsInfo, this.threadPlacements, timeout));
    }
    
    public List<CrystalInfo.PlaceInfo> getOutput(final boolean wait) {
        if (this.mainThreadOutput == null) {
            return ACHelper.EMPTY_LIST;
        }
        if (wait) {
            while (!this.mainThreadOutput.isDone() && !this.mainThreadOutput.isCancelled()) {}
        }
        else {
            if (!this.mainThreadOutput.isDone()) {
                return null;
            }
            if (this.mainThreadOutput.isCancelled()) {
                return ACHelper.EMPTY_LIST;
            }
        }
        List<CrystalInfo.PlaceInfo> output = ACHelper.EMPTY_LIST;
        try {
            output = this.mainThreadOutput.get();
        }
        catch (InterruptedException ex) {}
        catch (ExecutionException ex2) {}
        this.mainThreadOutput = null;
        return output;
    }
    
    public void recalculateValues(final ACSettings settings, final PlayerInfo self, final float armourPercent, final double enemyDistance) {
        this.settings = settings;
        final double entityRangeSq = enemyDistance * enemyDistance;
        final List<EntityPlayer> targets = (List<EntityPlayer>)ACHelper.mc.field_71441_e.field_73010_i.stream().filter(entity -> self.entity.func_70068_e(entity) <= entityRangeSq).filter(entity -> !EntityUtil.basicChecksEntity(entity)).filter(entity -> entity.func_110143_aJ() > 0.0f).collect(Collectors.toList());
        this.targetableCrystals = (List<EntityEnderCrystal>)ACHelper.mc.field_71441_e.field_72996_f.stream().filter(entity -> entity instanceof EntityEnderCrystal).map(entity -> entity).collect(Collectors.toList());
        final boolean own = settings.breakMode.equalsIgnoreCase("Own");
        if (own) {
            synchronized (this.placedCrystals) {
                this.targetableCrystals.removeIf(crystal -> !this.placedCrystals.containsKey(EntityUtil.getPosition(crystal)));
                this.placedCrystals.values().removeIf(crystal -> crystal.field_70128_L);
            }
        }
        final float damage;
        this.targetableCrystals.removeIf(crystal -> {
            damage = DamageUtil.calculateDamageThreaded(crystal.field_70165_t, crystal.field_70163_u, crystal.field_70161_v, self);
            if (damage > settings.maxSelfDamage) {
                return true;
            }
            else {
                return (settings.antiSuicide && damage > self.health) || self.entity.func_70068_e((Entity)crystal) >= settings.breakRangeSq;
            }
        });
        final float damage2;
        (this.possiblePlacements = CrystalUtil.findCrystalBlocks(settings.placeRange, settings.endCrystalMode)).removeIf(crystal -> {
            damage2 = DamageUtil.calculateDamageThreaded(crystal.func_177958_n() + 0.5, crystal.func_177956_o() + 1.0, crystal.func_177952_p() + 0.5, settings.player);
            if (damage2 > settings.maxSelfDamage) {
                return true;
            }
            else {
                return settings.antiSuicide && damage2 > settings.player.health;
            }
        });
        this.threadPlacements = CrystalUtil.findCrystalBlocksExcludingCrystals(settings.placeRange, settings.endCrystalMode);
        this.targetsInfo.clear();
        for (final EntityPlayer target : targets) {
            this.targetsInfo.add(new PlayerInfo(target, armourPercent));
        }
    }
    
    public void onPlaceCrystal(final BlockPos target) {
        if (this.settings.breakMode.equalsIgnoreCase("Own")) {
            final BlockPos up = target.func_177984_a();
            synchronized (this.placedCrystals) {
                this.placedCrystals.put(up, ACHelper.GENERIC_CRYSTAL);
            }
        }
    }
    
    public void onEnable() {
        GameSense.EVENT_BUS.subscribe(this);
    }
    
    public void onDisable() {
        GameSense.EVENT_BUS.unsubscribe(this);
        synchronized (this.placedCrystals) {
            this.placedCrystals.clear();
        }
        if (this.mainThreadOutput != null) {
            this.mainThreadOutput.cancel(true);
        }
    }
    
    public ACSettings getSettings() {
        return this.settings;
    }
    
    public List<BlockPos> getPossiblePlacements() {
        return this.possiblePlacements;
    }
    
    public List<EntityEnderCrystal> getTargetableCrystals() {
        return this.targetableCrystals;
    }
    
    static {
        mc = Minecraft.func_71410_x();
        EMPTY_LIST = new ArrayList<CrystalInfo.PlaceInfo>();
        GENERIC_CRYSTAL = new EntityEnderCrystal((World)null, 398.0, 398.0, 398.0);
        executor = (ThreadPoolExecutor)Executors.newCachedThreadPool();
        mainExecutors = Executors.newSingleThreadExecutor();
    }
}
