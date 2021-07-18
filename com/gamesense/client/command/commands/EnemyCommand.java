// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.client.command.Command;

@Declaration(name = "Enemy", syntax = "enemy list/add/del [player]", alias = { "enemy", "enemies", "e" })
public class EnemyCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        final String main = message[0];
        if (main.equalsIgnoreCase("list")) {
            MessageBus.sendClientPrefixMessage("Enemies: " + SocialManager.getEnemiesByName() + "!");
            return;
        }
        final String value = message[1];
        if (main.equalsIgnoreCase("add") && !SocialManager.isEnemy(value)) {
            SocialManager.addEnemy(value);
            MessageBus.sendCommandMessage("Added enemy: " + value.toUpperCase() + "!", true);
        }
        else if (main.equalsIgnoreCase("del") && SocialManager.isEnemy(value)) {
            SocialManager.delEnemy(value);
            MessageBus.sendCommandMessage("Deleted enemy: " + value.toUpperCase() + "!", true);
        }
    }
}
