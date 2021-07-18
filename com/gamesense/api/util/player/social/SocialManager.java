// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.player.social;

import java.util.Iterator;
import java.util.ArrayList;

public class SocialManager
{
    private static ArrayList<Friend> friends;
    private static ArrayList<Enemy> enemies;
    private static ArrayList<SpecialNames> SpecialNames;
    
    public static void init() {
        SocialManager.friends = new ArrayList<Friend>();
        SocialManager.enemies = new ArrayList<Enemy>();
        SocialManager.SpecialNames = new ArrayList<SpecialNames>();
    }
    
    public static ArrayList<Friend> getFriends() {
        return SocialManager.friends;
    }
    
    public static ArrayList<Enemy> getEnemies() {
        return SocialManager.enemies;
    }
    
    public static ArrayList<SpecialNames> getSpecialNames() {
        return SocialManager.SpecialNames;
    }
    
    public static ArrayList<String> getFriendsByName() {
        final ArrayList<String> friendNames = new ArrayList<String>();
        getFriends().forEach(friend -> friendNames.add(friend.getName()));
        return friendNames;
    }
    
    public static ArrayList<String> getEnemiesByName() {
        final ArrayList<String> enemyNames = new ArrayList<String>();
        getEnemies().forEach(enemy -> enemyNames.add(enemy.getName()));
        return enemyNames;
    }
    
    public static boolean isFriend(final String name) {
        boolean value = false;
        for (final Friend friend : getFriends()) {
            if (friend.getName().equalsIgnoreCase(name)) {
                value = true;
                break;
            }
        }
        return value;
    }
    
    public static boolean isEnemy(final String name) {
        boolean value = false;
        for (final Enemy enemy : getEnemies()) {
            if (enemy.getName().equalsIgnoreCase(name)) {
                value = true;
                break;
            }
        }
        return value;
    }
    
    public static boolean isSpecial(final String name) {
        boolean value = false;
        for (final SpecialNames enemy : getSpecialNames()) {
            if (enemy.getName().equalsIgnoreCase(name)) {
                value = true;
                break;
            }
        }
        return value;
    }
    
    public static Friend getFriend(final String name) {
        return getFriends().stream().filter(friend -> friend.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    
    public static Enemy getEnemy(final String name) {
        return getEnemies().stream().filter(enemy -> enemy.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    
    public static SpecialNames getSpecialNames(final String name) {
        return getSpecialNames().stream().filter(enemy -> enemy.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
    
    public static ArrayList<String> getSpecialNamesString() {
        final ArrayList<String> out = new ArrayList<String>();
        try {
            for (final SpecialNames name : getSpecialNames()) {
                out.add(name.getName());
            }
        }
        catch (OutOfMemoryError outOfMemoryError) {}
        return out;
    }
    
    public static void addFriend(final String name) {
        getFriends().add(new Friend(name));
    }
    
    public static void delFriend(final String name) {
        getFriends().remove(getFriend(name));
    }
    
    public static void addEnemy(final String name) {
        getEnemies().add(new Enemy(name));
    }
    
    public static void delEnemy(final String name) {
        getEnemies().remove(getEnemy(name));
    }
    
    public static void delSpecial(final String name) {
        getSpecialNames().remove(getSpecialNames(name));
    }
    
    public static void addSpecialName(final String name) {
        getSpecialNames().add(new SpecialNames(name));
    }
    
    public static void removeSpecialName(final String name) {
        getSpecialNames().remove(getSpecialNames(name));
    }
}
