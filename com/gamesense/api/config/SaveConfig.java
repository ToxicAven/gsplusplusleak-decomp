// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.config;

import com.gamesense.client.module.modules.misc.AutoRespawn;
import com.gamesense.client.module.modules.misc.AutoReply;
import com.gamesense.client.module.modules.misc.AutoGG;
import com.lukflug.panelstudio.ConfigList;
import com.gamesense.client.clickgui.GuiConfig;
import com.gamesense.api.util.player.social.Enemy;
import com.gamesense.api.util.player.social.Friend;
import com.gamesense.api.util.player.social.SpecialNames;
import com.gamesense.api.util.player.social.SocialManager;
import com.google.gson.JsonArray;
import com.gamesense.client.command.CommandManager;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.Setting;
import com.gamesense.api.setting.SettingsManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonObject;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import com.google.gson.GsonBuilder;
import java.util.Iterator;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import java.io.File;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Paths;
import com.gamesense.client.GameSense;
import java.io.IOException;

public class SaveConfig
{
    public static final String fileName = "gs++/";
    private static final String moduleName = "Modules/";
    private static final String mainName = "Main/";
    private static final String miscName = "Misc/";
    
    public static void init() {
        try {
            saveConfig();
            saveModules();
            saveEnabledModules();
            saveModuleKeybinds();
            saveDrawnModules();
            saveToggleMessagesModules();
            saveCommandPrefix();
            saveCustomFont();
            saveFriendsList();
            saveEnemiesList();
            saveSpecialNames();
            saveClickGUIPositions();
            saveAutoGG();
            saveAutoReply();
            saveAutoRespawn();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        GameSense.LOGGER.info("Saved Config!");
    }
    
    private static void saveConfig() throws IOException {
        if (!Files.exists(Paths.get("gs++/", new String[0]), new LinkOption[0])) {
            Files.createDirectories(Paths.get("gs++/", new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
        }
        if (!Files.exists(Paths.get("gs++/Modules/", new String[0]), new LinkOption[0])) {
            Files.createDirectories(Paths.get("gs++/Modules/", new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
        }
        if (!Files.exists(Paths.get("gs++/Main/", new String[0]), new LinkOption[0])) {
            Files.createDirectories(Paths.get("gs++/Main/", new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
        }
        if (!Files.exists(Paths.get("gs++/Misc/", new String[0]), new LinkOption[0])) {
            Files.createDirectories(Paths.get("gs++/Misc/", new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
        }
    }
    
    private static void registerFiles(final String location, final String name) throws IOException {
        if (Files.exists(Paths.get("gs++/" + location + name + ".json", new String[0]), new LinkOption[0])) {
            final File file = new File("gs++/" + location + name + ".json");
            file.delete();
        }
        Files.createFile(Paths.get("gs++/" + location + name + ".json", new String[0]), (FileAttribute<?>[])new FileAttribute[0]);
    }
    
    private static void saveModules() throws IOException {
        for (final Module module : ModuleManager.getModules()) {
            try {
                saveModuleDirect(module);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static void saveModuleDirect(final Module module) throws IOException {
        registerFiles("Modules/", module.getName());
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("gs++/Modules/" + module.getName() + ".json"), StandardCharsets.UTF_8);
        final JsonObject moduleObject = new JsonObject();
        final JsonObject settingObject = new JsonObject();
        moduleObject.add("Module", (JsonElement)new JsonPrimitive(module.getName()));
        for (final Setting setting : SettingsManager.getSettingsForModule(module)) {
            if (setting instanceof BooleanSetting) {
                settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive(Boolean.valueOf(((BooleanSetting)setting).getValue())));
            }
            else if (setting instanceof IntegerSetting) {
                settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive((Number)((Setting<Number>)setting).getValue()));
            }
            else if (setting instanceof DoubleSetting) {
                settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive((Number)((Setting<Number>)setting).getValue()));
            }
            else if (setting instanceof ColorSetting) {
                settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive((Number)((ColorSetting)setting).toInteger()));
            }
            else {
                if (!(setting instanceof ModeSetting)) {
                    continue;
                }
                settingObject.add(setting.getConfigName(), (JsonElement)new JsonPrimitive((String)((ModeSetting)setting).getValue()));
            }
        }
        moduleObject.add("Settings", (JsonElement)settingObject);
        final String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveEnabledModules() throws IOException {
        registerFiles("Main/", "Toggle");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("gs++/Main/Toggle.json"), StandardCharsets.UTF_8);
        final JsonObject moduleObject = new JsonObject();
        final JsonObject enabledObject = new JsonObject();
        for (final Module module : ModuleManager.getModules()) {
            enabledObject.add(module.getName(), (JsonElement)new JsonPrimitive(Boolean.valueOf(module.isEnabled())));
        }
        moduleObject.add("Modules", (JsonElement)enabledObject);
        final String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveModuleKeybinds() throws IOException {
        registerFiles("Main/", "Bind");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("gs++/Main/Bind.json"), StandardCharsets.UTF_8);
        final JsonObject moduleObject = new JsonObject();
        final JsonObject bindObject = new JsonObject();
        for (final Module module : ModuleManager.getModules()) {
            bindObject.add(module.getName(), (JsonElement)new JsonPrimitive((Number)module.getBind()));
        }
        moduleObject.add("Modules", (JsonElement)bindObject);
        final String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveDrawnModules() throws IOException {
        registerFiles("Main/", "Drawn");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("gs++/Main/Drawn.json"), StandardCharsets.UTF_8);
        final JsonObject moduleObject = new JsonObject();
        final JsonObject drawnObject = new JsonObject();
        for (final Module module : ModuleManager.getModules()) {
            drawnObject.add(module.getName(), (JsonElement)new JsonPrimitive(Boolean.valueOf(module.isDrawn())));
        }
        moduleObject.add("Modules", (JsonElement)drawnObject);
        final String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveToggleMessagesModules() throws IOException {
        registerFiles("Main/", "ToggleMessages");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("gs++/Main/ToggleMessages.json"), StandardCharsets.UTF_8);
        final JsonObject moduleObject = new JsonObject();
        final JsonObject toggleMessagesObject = new JsonObject();
        for (final Module module : ModuleManager.getModules()) {
            toggleMessagesObject.add(module.getName(), (JsonElement)new JsonPrimitive(Boolean.valueOf(module.isToggleMsg())));
        }
        moduleObject.add("Modules", (JsonElement)toggleMessagesObject);
        final String jsonString = gson.toJson(new JsonParser().parse(moduleObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveCommandPrefix() throws IOException {
        registerFiles("Main/", "CommandPrefix");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("gs++/Main/CommandPrefix.json"), StandardCharsets.UTF_8);
        final JsonObject prefixObject = new JsonObject();
        prefixObject.add("Prefix", (JsonElement)new JsonPrimitive(CommandManager.getCommandPrefix()));
        final String jsonString = gson.toJson(new JsonParser().parse(prefixObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveCustomFont() throws IOException {
        registerFiles("Misc/", "CustomFont");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("gs++/Misc/CustomFont.json"), StandardCharsets.UTF_8);
        final JsonObject fontObject = new JsonObject();
        fontObject.add("Font Name", (JsonElement)new JsonPrimitive(GameSense.INSTANCE.cFontRenderer.getFontName()));
        fontObject.add("Font Size", (JsonElement)new JsonPrimitive((Number)GameSense.INSTANCE.cFontRenderer.getFontSize()));
        final String jsonString = gson.toJson(new JsonParser().parse(fontObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveSpecialNames() throws IOException {
        registerFiles("Misc/", "SpecialNames");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("gs++/Misc/SpecialNames.json"), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();
        final JsonArray friendArray = new JsonArray();
        for (final SpecialNames friend : SocialManager.getSpecialNames()) {
            friendArray.add(friend.getName());
        }
        mainObject.add("SpecialNames", (JsonElement)friendArray);
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveFriendsList() throws IOException {
        registerFiles("Misc/", "Friends");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("gs++/Misc/Friends.json"), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();
        final JsonArray friendArray = new JsonArray();
        for (final Friend friend : SocialManager.getFriends()) {
            friendArray.add(friend.getName());
        }
        mainObject.add("Friends", (JsonElement)friendArray);
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveEnemiesList() throws IOException {
        registerFiles("Misc/", "Enemies");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("gs++/Misc/Enemies.json"), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();
        final JsonArray enemyArray = new JsonArray();
        for (final Enemy enemy : SocialManager.getEnemies()) {
            enemyArray.add(enemy.getName());
        }
        mainObject.add("Enemies", (JsonElement)enemyArray);
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveClickGUIPositions() throws IOException {
        registerFiles("Main/", "ClickGUI");
        GameSense.INSTANCE.gameSenseGUI.gui.saveConfig(new GuiConfig("gs++/Main/"));
    }
    
    private static void saveAutoGG() throws IOException {
        registerFiles("Misc/", "AutoGG");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("gs++/Misc/AutoGG.json"), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();
        final JsonArray messageArray = new JsonArray();
        for (final String autoGG : AutoGG.getAutoGgMessages()) {
            messageArray.add(autoGG);
        }
        mainObject.add("Messages", (JsonElement)messageArray);
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveAutoReply() throws IOException {
        registerFiles("Misc/", "AutoReply");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("gs++/Misc/AutoReply.json"), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();
        final JsonObject messageObject = new JsonObject();
        messageObject.add("Message", (JsonElement)new JsonPrimitive(AutoReply.getReply()));
        mainObject.add("AutoReply", (JsonElement)messageObject);
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
    
    private static void saveAutoRespawn() throws IOException {
        registerFiles("Misc/", "AutoRespawn");
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final OutputStreamWriter fileOutputStreamWriter = new OutputStreamWriter(new FileOutputStream("gs++/Misc/AutoRespawn.json"), StandardCharsets.UTF_8);
        final JsonObject mainObject = new JsonObject();
        mainObject.add("Message", (JsonElement)new JsonPrimitive(AutoRespawn.getAutoRespawnMessages()));
        final String jsonString = gson.toJson(new JsonParser().parse(mainObject.toString()));
        fileOutputStreamWriter.write(jsonString);
        fileOutputStreamWriter.close();
    }
}
