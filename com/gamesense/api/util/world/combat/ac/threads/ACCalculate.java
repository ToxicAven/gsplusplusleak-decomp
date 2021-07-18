// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.world.combat.ac.threads;

import net.minecraft.entity.Entity;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;
import java.util.Iterator;
import com.gamesense.api.util.world.combat.ac.ACHelper;
import java.util.ArrayList;
import java.util.concurrent.Future;
import net.minecraft.util.math.BlockPos;
import com.gamesense.api.util.world.combat.ac.PlayerInfo;
import com.gamesense.api.util.world.combat.ac.ACSettings;
import com.gamesense.api.util.world.combat.ac.CrystalInfo;
import java.util.List;
import java.util.concurrent.Callable;

public class ACCalculate implements Callable<List<CrystalInfo.PlaceInfo>>
{
    private final ACSettings settings;
    private final List<PlayerInfo> targets;
    private final List<BlockPos> blocks;
    private final long globalTimeoutTime;
    
    public ACCalculate(final ACSettings settings, final List<PlayerInfo> targets, final List<BlockPos> blocks, final long globalTimeoutTime) {
        this.settings = settings;
        this.targets = targets;
        this.blocks = blocks;
        this.globalTimeoutTime = globalTimeoutTime;
    }
    
    @Override
    public List<CrystalInfo.PlaceInfo> call() {
        return this.getPlayers(this.startThreads());
    }
    
    @Nonnull
    private List<Future<CrystalInfo.PlaceInfo>> startThreads() {
        final List<Future<CrystalInfo.PlaceInfo>> output = new ArrayList<Future<CrystalInfo.PlaceInfo>>();
        for (final PlayerInfo target : this.targets) {
            output.add(ACHelper.executor.submit((Callable<CrystalInfo.PlaceInfo>)new ACSubThread(this.settings, this.blocks, target)));
        }
        return output;
    }
    
    private List<CrystalInfo.PlaceInfo> getPlayers(final List<Future<CrystalInfo.PlaceInfo>> input) {
        final List<CrystalInfo.PlaceInfo> place = new ArrayList<CrystalInfo.PlaceInfo>();
        for (final Future<CrystalInfo.PlaceInfo> future : input) {
            while (!future.isDone() && !future.isCancelled() && System.currentTimeMillis() <= this.globalTimeoutTime) {}
            if (future.isDone()) {
                CrystalInfo.PlaceInfo crystal = null;
                try {
                    crystal = future.get();
                }
                catch (InterruptedException ex) {}
                catch (ExecutionException ex2) {}
                if (crystal == null) {
                    continue;
                }
                place.add(crystal);
            }
            else {
                future.cancel(true);
            }
        }
        if (this.settings.crystalPriority.equalsIgnoreCase("Health")) {
            place.sort(Comparator.comparingDouble(i -> -i.target.health));
        }
        else if (this.settings.crystalPriority.equalsIgnoreCase("Closest")) {
            place.sort(Comparator.comparingDouble(i -> -this.settings.player.entity.func_70068_e((Entity)i.target.entity)));
        }
        else {
            place.sort(Comparator.comparingDouble(i -> i.damage));
        }
        return place;
    }
}
