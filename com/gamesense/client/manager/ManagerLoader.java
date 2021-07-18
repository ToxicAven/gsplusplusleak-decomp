// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.manager;

import java.util.ArrayList;
import net.minecraftforge.common.MinecraftForge;
import com.gamesense.client.GameSense;
import com.gamesense.client.manager.managers.TotemPopManager;
import com.gamesense.client.manager.managers.PlayerPacketManager;
import com.gamesense.client.manager.managers.ClientEventManager;
import java.util.List;

public class ManagerLoader
{
    private static final List<Manager> managers;
    
    public static void init() {
        register(ClientEventManager.INSTANCE);
        register(PlayerPacketManager.INSTANCE);
        register(TotemPopManager.INSTANCE);
    }
    
    private static void register(final Manager manager) {
        ManagerLoader.managers.add(manager);
        GameSense.EVENT_BUS.subscribe(manager);
        MinecraftForge.EVENT_BUS.register((Object)manager);
    }
    
    static {
        managers = new ArrayList<Manager>();
    }
}
