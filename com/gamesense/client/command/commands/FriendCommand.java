// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.client.command.Command;

@Declaration(name = "Friend", syntax = "friend list/add/del [player]", alias = { "friend", "friends", "f" })
public class FriendCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        final String main = message[0];
        if (main.equalsIgnoreCase("list")) {
            MessageBus.sendClientPrefixMessage("Friends: " + SocialManager.getFriendsByName() + "!");
            return;
        }
        final String value = message[1];
        if (main.equalsIgnoreCase("add") && !SocialManager.isFriend(value)) {
            SocialManager.addFriend(value);
            MessageBus.sendCommandMessage("Added friend: " + value.toUpperCase() + "!", true);
        }
        else if (main.equalsIgnoreCase("del") && SocialManager.isFriend(value)) {
            SocialManager.delFriend(value);
            MessageBus.sendCommandMessage("Deleted friend: " + value.toUpperCase() + "!", true);
        }
    }
}
