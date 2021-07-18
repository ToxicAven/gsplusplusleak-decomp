// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin;

import java.util.Map;
import javax.annotation.Nullable;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.launch.MixinBootstrap;
import com.gamesense.client.GameSense;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("gs++")
@IFMLLoadingPlugin.MCVersion("1.12.2")
public class GameSenseMixinLoader implements IFMLLoadingPlugin
{
    private static boolean isObfuscatedEnvironment;
    
    public GameSenseMixinLoader() {
        GameSense.LOGGER.info("Mixins initialized");
        MixinBootstrap.init();
        Mixins.addConfiguration("mixins.gamesense.json");
        MixinEnvironment.getDefaultEnvironment().setObfuscationContext("searge");
        GameSense.LOGGER.info(MixinEnvironment.getDefaultEnvironment().getObfuscationContext());
    }
    
    public String[] getASMTransformerClass() {
        return new String[0];
    }
    
    public String getModContainerClass() {
        return null;
    }
    
    @Nullable
    public String getSetupClass() {
        return null;
    }
    
    public void injectData(final Map<String, Object> data) {
        GameSenseMixinLoader.isObfuscatedEnvironment = data.get("runtimeDeobfuscationEnabled");
    }
    
    public String getAccessTransformerClass() {
        return null;
    }
    
    static {
        GameSenseMixinLoader.isObfuscatedEnvironment = false;
    }
}
