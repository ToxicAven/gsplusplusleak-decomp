// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.setting;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.List;
import com.gamesense.client.module.Module;
import java.util.ArrayList;

public class SettingsManager
{
    private static ArrayList<Setting> settings;
    
    public static void init() {
        SettingsManager.settings = new ArrayList<Setting>();
    }
    
    public static void addSetting(final Setting setting) {
        SettingsManager.settings.add(setting);
    }
    
    public static ArrayList<Setting> getSettings() {
        return SettingsManager.settings;
    }
    
    public static List<Setting> getSettingsForModule(final Module module) {
        return SettingsManager.settings.stream().filter(setting -> setting.getModule().equals(module)).collect((Collector<? super Object, ?, List<Setting>>)Collectors.toList());
    }
}
