// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.config;

import com.gamesense.client.module.modules.misc.AutoRespawn;
import com.gamesense.client.module.modules.misc.AutoReply;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.lukflug.panelstudio.ConfigList;
import com.gamesense.client.clickgui.GuiConfig;
import com.google.gson.JsonArray;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.api.util.font.CFontRenderer;
import java.awt.Font;
import com.gamesense.client.GameSense;
import com.gamesense.client.command.CommandManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.InputStream;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.setting.SettingsManager;
import java.io.Reader;
import java.io.InputStreamReader;
import com.google.gson.JsonParser;
import java.nio.file.OpenOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import java.util.Iterator;
import java.io.IOException;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;

public class LoadConfig
{
    private static final String fileName = "gs++/";
    private static final String moduleName = "Modules/";
    private static final String mainName = "Main/";
    private static final String miscName = "Misc/";
    
    public static void init() {
        try {
            loadModules();
            loadEnabledModules();
            loadModuleKeybinds();
            loadDrawnModules();
            loadToggleMessageModules();
            loadCommandPrefix();
            loadCustomFont();
            loadFriendsList();
            loadEnemiesList();
            loadSpecialNames();
            loadClickGUIPositions();
            loadAutoGG();
            loadAutoReply();
            loadAutoRespawn();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void loadModules() throws IOException {
        final String moduleLocation = "gs++/Modules/";
        for (final Module module : ModuleManager.getModules()) {
            try {
                loadModuleDirect(moduleLocation, module);
            }
            catch (IOException e) {
                System.out.println(module.getName());
                e.printStackTrace();
            }
        }
    }
    
    private static void loadModuleDirect(final String moduleLocation, final Module module) throws IOException {
        if (!Files.exists(Paths.get(moduleLocation + module.getName() + ".json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(moduleLocation + module.getName() + ".json", new String[0]), new OpenOption[0]);
        JsonObject moduleObject;
        try {
            moduleObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        }
        catch (IllegalStateException e) {
            return;
        }
        if (moduleObject.get("Module") == null) {
            return;
        }
        final JsonObject settingObject = moduleObject.get("Settings").getAsJsonObject();
        for (final Setting setting : SettingsManager.getSettingsForModule(module)) {
            final JsonElement dataObject = settingObject.get(setting.getConfigName());
            try {
                if (dataObject == null || !dataObject.isJsonPrimitive()) {
                    continue;
                }
                if (setting instanceof BooleanSetting) {
                    setting.setValue(dataObject.getAsBoolean());
                }
                else if (setting instanceof IntegerSetting) {
                    setting.setValue(dataObject.getAsInt());
                }
                else if (setting instanceof DoubleSetting) {
                    setting.setValue(dataObject.getAsDouble());
                }
                else if (setting instanceof ColorSetting) {
                    ((ColorSetting)setting).fromInteger(dataObject.getAsInt());
                }
                else {
                    if (!(setting instanceof ModeSetting)) {
                        continue;
                    }
                    setting.setValue(dataObject.getAsString());
                }
            }
            catch (NumberFormatException e2) {
                System.out.println(setting.getConfigName() + " " + module.getName());
                System.out.println(dataObject);
            }
        }
        inputStream.close();
    }
    
    private static void loadEnabledModules() throws IOException {
        final String enabledLocation = "gs++/Main/";
        if (!Files.exists(Paths.get(enabledLocation + "Toggle.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(enabledLocation + "Toggle.json", new String[0]), new OpenOption[0]);
        final JsonObject moduleObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Modules") == null) {
            return;
        }
        final JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (final Module module : ModuleManager.getModules()) {
            final JsonElement dataObject = settingObject.get(module.getName());
            if (dataObject != null && dataObject.isJsonPrimitive() && dataObject.getAsBoolean()) {
                try {
                    module.enable();
                }
                catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        inputStream.close();
    }
    
    private static void loadModuleKeybinds() throws IOException {
        final String bindLocation = "gs++/Main/";
        if (!Files.exists(Paths.get(bindLocation + "Bind.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(bindLocation + "Bind.json", new String[0]), new OpenOption[0]);
        final JsonObject moduleObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Modules") == null) {
            return;
        }
        final JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (final Module module : ModuleManager.getModules()) {
            final JsonElement dataObject = settingObject.get(module.getName());
            if (dataObject != null && dataObject.isJsonPrimitive()) {
                module.setBind(dataObject.getAsInt());
            }
        }
        inputStream.close();
    }
    
    private static void loadDrawnModules() throws IOException {
        final String drawnLocation = "gs++/Main/";
        if (!Files.exists(Paths.get(drawnLocation + "Drawn.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(drawnLocation + "Drawn.json", new String[0]), new OpenOption[0]);
        final JsonObject moduleObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Modules") == null) {
            return;
        }
        final JsonObject settingObject = moduleObject.get("Modules").getAsJsonObject();
        for (final Module module : ModuleManager.getModules()) {
            final JsonElement dataObject = settingObject.get(module.getName());
            if (dataObject != null && dataObject.isJsonPrimitive()) {
                module.setDrawn(dataObject.getAsBoolean());
            }
        }
        inputStream.close();
    }
    
    private static void loadToggleMessageModules() throws IOException {
        final String toggleMessageLocation = "gs++/Main/";
        if (!Files.exists(Paths.get(toggleMessageLocation + "ToggleMessages.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(toggleMessageLocation + "ToggleMessages.json", new String[0]), new OpenOption[0]);
        final JsonObject moduleObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (moduleObject.get("Modules") == null) {
            return;
        }
        final JsonObject toggleObject = moduleObject.get("Modules").getAsJsonObject();
        for (final Module module : ModuleManager.getModules()) {
            final JsonElement dataObject = toggleObject.get(module.getName());
            if (dataObject != null && dataObject.isJsonPrimitive()) {
                module.setToggleMsg(dataObject.getAsBoolean());
            }
        }
        inputStream.close();
    }
    
    private static void loadCommandPrefix() throws IOException {
        final String prefixLocation = "gs++/Main/";
        if (!Files.exists(Paths.get(prefixLocation + "CommandPrefix.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(prefixLocation + "CommandPrefix.json", new String[0]), new OpenOption[0]);
        final JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Prefix") == null) {
            return;
        }
        final JsonElement prefixObject = mainObject.get("Prefix");
        if (prefixObject != null && prefixObject.isJsonPrimitive()) {
            CommandManager.setCommandPrefix(prefixObject.getAsString());
        }
        inputStream.close();
    }
    
    private static void loadCustomFont() throws IOException {
        final String fontLocation = "gs++/Misc/";
        if (!Files.exists(Paths.get(fontLocation + "CustomFont.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(fontLocation + "CustomFont.json", new String[0]), new OpenOption[0]);
        final JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Font Name") == null || mainObject.get("Font Size") == null) {
            return;
        }
        final JsonElement fontNameObject = mainObject.get("Font Name");
        String name = null;
        if (fontNameObject != null && fontNameObject.isJsonPrimitive()) {
            name = fontNameObject.getAsString();
        }
        final JsonElement fontSizeObject = mainObject.get("Font Size");
        int size = -1;
        if (fontSizeObject != null && fontSizeObject.isJsonPrimitive()) {
            size = fontSizeObject.getAsInt();
        }
        if (name != null && size != -1) {
            (GameSense.INSTANCE.cFontRenderer = new CFontRenderer(new Font(name, 0, size), true, true)).setFont(new Font(name, 0, size));
            GameSense.INSTANCE.cFontRenderer.setAntiAlias(true);
            GameSense.INSTANCE.cFontRenderer.setFractionalMetrics(true);
            GameSense.INSTANCE.cFontRenderer.setFontName(name);
            GameSense.INSTANCE.cFontRenderer.setFontSize(size);
        }
        inputStream.close();
    }
    
    private static void loadFriendsList() throws IOException {
        final String friendLocation = "gs++/Misc/";
        if (!Files.exists(Paths.get(friendLocation + "Friends.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(friendLocation + "Friends.json", new String[0]), new OpenOption[0]);
        final JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Friends") == null) {
            return;
        }
        final JsonArray friendObject = mainObject.get("Friends").getAsJsonArray();
        friendObject.forEach(object -> SocialManager.addFriend(object.getAsString()));
        inputStream.close();
    }
    
    private static void loadSpecialNames() throws IOException {
        final String friendLocation = "gs++/Misc/";
        if (!Files.exists(Paths.get(friendLocation + "SpecialNames.json", new String[0]), new LinkOption[0])) {
            for (final String defaultValue : new String[] { "nocatsnolife", "gamesense", "gs", "sable", "phantom826", "doogie13", "soulbond", "vqk", "gaynal", "anonymousplayer", "lambdaclient", "\u2063", "\u0262\ua731" }) {
                SocialManager.addSpecialName(defaultValue);
            }
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(friendLocation + "SpecialNames.json", new String[0]), new OpenOption[0]);
        final JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("SpecialNames") == null) {
            return;
        }
        final JsonArray friendObject = mainObject.get("SpecialNames").getAsJsonArray();
        friendObject.forEach(object -> SocialManager.addSpecialName(object.getAsString()));
        inputStream.close();
    }
    
    private static void loadEnemiesList() throws IOException {
        final String enemyLocation = "gs++/Misc/";
        if (!Files.exists(Paths.get(enemyLocation + "Enemies.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(enemyLocation + "Enemies.json", new String[0]), new OpenOption[0]);
        final JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Enemies") == null) {
            return;
        }
        final JsonArray enemyObject = mainObject.get("Enemies").getAsJsonArray();
        enemyObject.forEach(object -> SocialManager.addEnemy(object.getAsString()));
        inputStream.close();
    }
    
    private static void loadClickGUIPositions() throws IOException {
        GameSense.INSTANCE.gameSenseGUI.gui.loadConfig(new GuiConfig("gs++/Main/"));
    }
    
    private static void loadAutoGG() throws IOException {
        final String fileLocation = "gs++/Misc/";
        if (!Files.exists(Paths.get(fileLocation + "AutoGG.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(fileLocation + "AutoGG.json", new String[0]), new OpenOption[0]);
        final JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Messages") == null) {
            return;
        }
        final JsonArray messageObject = mainObject.get("Messages").getAsJsonArray();
        messageObject.forEach(object -> AutoGG.addAutoGgMessage(object.getAsString()));
        inputStream.close();
    }
    
    private static void loadAutoReply() throws IOException {
        final String fileLocation = "gs++/Misc/";
        if (!Files.exists(Paths.get(fileLocation + "AutoReply.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(fileLocation + "AutoReply.json", new String[0]), new OpenOption[0]);
        final JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("AutoReply") == null) {
            return;
        }
        final JsonObject arObject = mainObject.get("AutoReply").getAsJsonObject();
        final JsonElement dataObject = arObject.get("Message");
        if (dataObject != null && dataObject.isJsonPrimitive()) {
            AutoReply.setReply(dataObject.getAsString());
        }
        inputStream.close();
    }
    
    private static void loadAutoRespawn() throws IOException {
        final String fileLocation = "gs++/Misc/";
        if (!Files.exists(Paths.get(fileLocation + "AutoRespawn.json", new String[0]), new LinkOption[0])) {
            return;
        }
        final InputStream inputStream = Files.newInputStream(Paths.get(fileLocation + "AutoRespawn.json", new String[0]), new OpenOption[0]);
        final JsonObject mainObject = new JsonParser().parse((Reader)new InputStreamReader(inputStream)).getAsJsonObject();
        if (mainObject.get("Message") == null) {
            return;
        }
        final JsonElement dataObject = mainObject.get("Message");
        if (dataObject != null && dataObject.isJsonPrimitive()) {
            AutoRespawn.setAutoRespawnMessage(dataObject.getAsString());
        }
        inputStream.close();
    }
}
