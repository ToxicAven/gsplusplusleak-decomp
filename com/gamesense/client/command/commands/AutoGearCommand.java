// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import java.io.Writer;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import java.io.IOException;
import com.gamesense.client.module.modules.combat.PistonCrystal;
import java.io.Reader;
import java.io.FileReader;
import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.gamesense.api.util.misc.MessageBus;
import java.util.HashMap;
import com.gamesense.client.command.Command;

@Declaration(name = "AutoGear", syntax = "gear set/save/del/list [name]", alias = { "gear", "gr", "kit" })
public class AutoGearCommand extends Command
{
    private static final String pathSave = "GameSense/Misc/AutoGear.json";
    private static final HashMap<String, String> errorMessage;
    
    @Override
    public void onCommand(final String command, final String[] message) {
        final String lowerCase = message[0].toLowerCase();
        switch (lowerCase) {
            case "list": {
                if (message.length == 1) {
                    this.listMessage();
                    break;
                }
                errorMessage("NoPar");
                break;
            }
            case "set": {
                if (message.length == 2) {
                    this.set(message[1]);
                    break;
                }
                errorMessage("NoPar");
                break;
            }
            case "save":
            case "add":
            case "create": {
                if (message.length == 2) {
                    this.save(message[1]);
                    break;
                }
                errorMessage("NoPar");
                break;
            }
            case "del": {
                if (message.length == 2) {
                    this.delete(message[1]);
                    break;
                }
                errorMessage("NoPar");
                break;
            }
            default: {
                MessageBus.sendCommandMessage("AutoGear message is: gear set/save/del/list [name]", true);
                break;
            }
        }
    }
    
    private void listMessage() {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader("GameSense/Misc/AutoGear.json")).getAsJsonObject();
            for (int lenghtJson = completeJson.entrySet().size(), i = 0; i < lenghtJson; ++i) {
                final String item = new JsonParser().parse((Reader)new FileReader("GameSense/Misc/AutoGear.json")).getAsJsonObject().entrySet().toArray()[i].toString().split("=")[0];
                if (!item.equals("pointer")) {
                    PistonCrystal.printDebug("Kit avaible: " + item, false);
                }
            }
        }
        catch (IOException e) {
            errorMessage("NoEx");
        }
    }
    
    private void delete(final String name) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader("GameSense/Misc/AutoGear.json")).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                completeJson.remove(name);
                if (completeJson.get("pointer").getAsString().equals(name)) {
                    completeJson.addProperty("pointer", "none");
                }
                this.saveFile(completeJson, name, "deleted");
            }
            else {
                errorMessage("NoEx");
            }
        }
        catch (IOException e) {
            errorMessage("NoEx");
        }
    }
    
    private void set(final String name) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader("GameSense/Misc/AutoGear.json")).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                completeJson.addProperty("pointer", name);
                this.saveFile(completeJson, name, "selected");
            }
            else {
                errorMessage("NoEx");
            }
        }
        catch (IOException e) {
            errorMessage("NoEx");
        }
    }
    
    private void save(final String name) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader("GameSense/Misc/AutoGear.json")).getAsJsonObject();
            if (completeJson.get(name) != null && !name.equals("pointer")) {
                errorMessage("Exist");
                return;
            }
        }
        catch (IOException e) {
            completeJson.addProperty("pointer", "none");
        }
        final StringBuilder jsonInventory = new StringBuilder();
        for (final ItemStack item : AutoGearCommand.mc.field_71439_g.field_71071_by.field_70462_a) {
            jsonInventory.append(item.func_77973_b().getRegistryName().toString() + item.func_77960_j()).append(" ");
        }
        completeJson.addProperty(name, jsonInventory.toString());
        this.saveFile(completeJson, name, "saved");
    }
    
    private void saveFile(final JsonObject completeJson, final String name, final String operation) {
        try {
            final BufferedWriter bw = new BufferedWriter(new FileWriter("GameSense/Misc/AutoGear.json"));
            bw.write(completeJson.toString());
            bw.close();
            PistonCrystal.printDebug("Kit " + name + " " + operation, false);
        }
        catch (IOException e) {
            errorMessage("Saving");
        }
    }
    
    private static void errorMessage(final String e) {
        PistonCrystal.printDebug("Error: " + AutoGearCommand.errorMessage.get(e), true);
    }
    
    public static String getCurrentSet() {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader("GameSense/Misc/AutoGear.json")).getAsJsonObject();
            if (!completeJson.get("pointer").getAsString().equals("none")) {
                return completeJson.get("pointer").getAsString();
            }
        }
        catch (IOException ex) {}
        errorMessage("NoEx");
        return "";
    }
    
    public static String getInventoryKit(final String kit) {
        JsonObject completeJson = new JsonObject();
        try {
            completeJson = new JsonParser().parse((Reader)new FileReader("GameSense/Misc/AutoGear.json")).getAsJsonObject();
            return completeJson.get(kit).getAsString();
        }
        catch (IOException ex) {
            errorMessage("NoEx");
            return "";
        }
    }
    
    static {
        errorMessage = new HashMap<String, String>() {
            {
                this.put("NoPar", "Not enough parameters");
                this.put("Exist", "This kit arleady exist");
                this.put("Saving", "Error saving the file");
                this.put("NoEx", "Kit not found");
            }
        };
    }
}
