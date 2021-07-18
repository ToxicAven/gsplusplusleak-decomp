// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client;

import me.zero.alpine.EventManager;
import org.apache.logging.log4j.LogManager;
import com.gamesense.api.config.LoadConfig;
import com.gamesense.api.util.render.CapeUtil;
import com.gamesense.client.manager.ManagerLoader;
import com.gamesense.client.command.CommandManager;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.setting.SettingsManager;
import java.awt.Font;
import com.gamesense.api.util.misc.VersionChecker;
import org.lwjgl.opengl.Display;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import com.gamesense.client.clickgui.GameSenseGUI;
import com.gamesense.api.util.font.CFontRenderer;
import me.zero.alpine.EventBus;
import org.apache.logging.log4j.Logger;
import net.minecraftforge.fml.common.Mod;

@Mod(modid = "gs++", name = "gs++", version = "p2.3.1.c4")
public class GameSense
{
    public static final String MODNAME = "gs++";
    public static final String MODID = "gs++";
    public static final String MODVER = "p2.3.1.c4";
    public static final Logger LOGGER;
    public static final EventBus EVENT_BUS;
    @Mod.Instance
    public static GameSense INSTANCE;
    public CFontRenderer cFontRenderer;
    public GameSenseGUI gameSenseGUI;
    
    public GameSense() {
        GameSense.INSTANCE = this;
    }
    
    @Mod.EventHandler
    public void init(final FMLInitializationEvent event) {
        Display.setTitle("gs++ p2.3.1.c4");
        GameSense.LOGGER.info("Starting up gs++ p2.3.1.c4!");
        this.startClient();
        GameSense.LOGGER.info("Finished initialization for gs++ p2.3.1.c4!");
    }
    
    private void startClient() {
        VersionChecker.init();
        GameSense.LOGGER.info("Version checked!");
        this.cFontRenderer = new CFontRenderer(new Font("Verdana", 0, 18), true, true);
        GameSense.LOGGER.info("Custom font initialized!");
        SettingsManager.init();
        GameSense.LOGGER.info("Settings initialized!");
        SocialManager.init();
        GameSense.LOGGER.info("Friends/Enemies initialized!");
        ModuleManager.init();
        GameSense.LOGGER.info("Modules initialized!");
        CommandManager.init();
        GameSense.LOGGER.info("Commands initialized!");
        ManagerLoader.init();
        GameSense.LOGGER.info("Managers initialized!");
        this.gameSenseGUI = new GameSenseGUI();
        GameSense.LOGGER.info("GUI initialized!");
        CapeUtil.init();
        GameSense.LOGGER.info("Capes initialized!");
        LoadConfig.init();
        GameSense.LOGGER.info("Config initialized!");
    }
    
    static {
        LOGGER = LogManager.getLogger("gs++");
        EVENT_BUS = new EventManager();
    }
}
