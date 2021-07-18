// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.world.combat.ac.threads;

import com.gamesense.api.util.world.combat.ac.ACUtil;
import com.gamesense.api.util.world.combat.ac.PlayerInfo;
import net.minecraft.util.math.BlockPos;
import java.util.List;
import com.gamesense.api.util.world.combat.ac.ACSettings;
import com.gamesense.api.util.world.combat.ac.CrystalInfo;
import java.util.concurrent.Callable;

public class ACSubThread implements Callable<CrystalInfo.PlaceInfo>
{
    private final ACSettings settings;
    private final List<BlockPos> possibleLocations;
    private final PlayerInfo target;
    
    public ACSubThread(final ACSettings setting, final List<BlockPos> possibleLocations, final PlayerInfo target) {
        this.settings = setting;
        this.possibleLocations = possibleLocations;
        this.target = target;
    }
    
    @Override
    public CrystalInfo.PlaceInfo call() {
        return this.getPlacement();
    }
    
    private CrystalInfo.PlaceInfo getPlacement() {
        if (this.possibleLocations == null) {
            return null;
        }
        return ACUtil.calculateBestPlacement(this.settings, this.target, this.possibleLocations);
    }
}
