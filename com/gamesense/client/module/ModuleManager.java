// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Iterator;
import com.gamesense.api.util.misc.ReflectionUtil;
import java.util.LinkedHashMap;

public class ModuleManager
{
    private static final String modulePath = "com.gamesense.client.module.modules";
    private static final LinkedHashMap<Class<? extends Module>, Module> modulesClassMap;
    private static final LinkedHashMap<String, Module> modulesNameMap;
    
    public static void init() {
        for (final Category category : Category.values()) {
            for (final Class<?> clazz : ReflectionUtil.findClassesInPath("com.gamesense.client.module.modules." + category.toString().toLowerCase())) {
                if (clazz == null) {
                    continue;
                }
                if (!Module.class.isAssignableFrom(clazz)) {
                    continue;
                }
                try {
                    final Module module = (Module)clazz.newInstance();
                    addMod(module);
                }
                catch (InstantiationException | IllegalAccessException ex2) {
                    final ReflectiveOperationException ex;
                    final ReflectiveOperationException e = ex;
                    e.printStackTrace();
                }
            }
        }
    }
    
    public static void addMod(final Module module) {
        ModuleManager.modulesClassMap.put(module.getClass(), module);
        ModuleManager.modulesNameMap.put(module.getName().toLowerCase(Locale.ROOT), module);
    }
    
    public static Collection<Module> getModules() {
        return ModuleManager.modulesClassMap.values();
    }
    
    public static ArrayList<Module> getModulesInCategory(final Category category) {
        final ArrayList<Module> list = new ArrayList<Module>();
        for (final Module module : ModuleManager.modulesClassMap.values()) {
            if (!module.getCategory().equals(category)) {
                continue;
            }
            list.add(module);
        }
        return list;
    }
    
    public static <T extends Module> T getModule(final Class<T> clazz) {
        return (T)ModuleManager.modulesClassMap.get(clazz);
    }
    
    public static Module getModule(final String name) {
        if (name == null) {
            return null;
        }
        return ModuleManager.modulesNameMap.get(name.toLowerCase(Locale.ROOT));
    }
    
    public static boolean isModuleEnabled(final Class<? extends Module> clazz) {
        final Module module = getModule(clazz);
        return module != null && module.isEnabled();
    }
    
    public static boolean isModuleEnabled(final String name) {
        final Module module = getModule(name);
        return module != null && module.isEnabled();
    }
    
    static {
        modulesClassMap = new LinkedHashMap<Class<? extends Module>, Module>();
        modulesNameMap = new LinkedHashMap<String, Module>();
    }
}
