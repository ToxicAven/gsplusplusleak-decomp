// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command;

import com.gamesense.api.util.misc.MessageBus;
import java.util.Iterator;
import com.gamesense.api.util.misc.ReflectionUtil;
import java.util.ArrayList;

public class CommandManager
{
    private static String commandPrefix;
    private static final String commandPath = "com.gamesense.client.command.commands";
    public static ArrayList<Command> commands;
    public static boolean isValidCommand;
    
    public static void init() {
        for (final Class<?> clazz : ReflectionUtil.findClassesInPath("com.gamesense.client.command.commands")) {
            if (clazz == null) {
                continue;
            }
            if (!Command.class.isAssignableFrom(clazz)) {
                continue;
            }
            try {
                final Command command = (Command)clazz.newInstance();
                addCommand(command);
            }
            catch (InstantiationException | IllegalAccessException ex2) {
                final ReflectiveOperationException ex;
                final ReflectiveOperationException e = ex;
                e.printStackTrace();
            }
        }
    }
    
    public static void addCommand(final Command command) {
        CommandManager.commands.add(command);
    }
    
    public static ArrayList<Command> getCommands() {
        return CommandManager.commands;
    }
    
    public static String getCommandPrefix() {
        return CommandManager.commandPrefix;
    }
    
    public static void setCommandPrefix(final String prefix) {
        CommandManager.commandPrefix = prefix;
    }
    
    public static void callCommand(final String input) {
        final String[] split = input.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        final String command2 = split[0];
        final String args = input.substring(command2.length()).trim();
        CommandManager.isValidCommand = false;
        final String[] array;
        int length;
        int i = 0;
        String string;
        final String anotherString;
        final String s;
        CommandManager.commands.forEach(command -> {
            command.getAlias();
            for (length = array.length; i < length; ++i) {
                string = array[i];
                if (string.equalsIgnoreCase(anotherString)) {
                    CommandManager.isValidCommand = true;
                    try {
                        command.onCommand(s, s.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
                    }
                    catch (Exception e) {
                        MessageBus.sendCommandMessage(command.getSyntax(), true);
                    }
                }
            }
            return;
        });
        if (!CommandManager.isValidCommand) {
            MessageBus.sendCommandMessage("Error! Invalid command!", true);
        }
    }
    
    static {
        CommandManager.commandPrefix = "-";
        CommandManager.commands = new ArrayList<Command>();
        CommandManager.isValidCommand = false;
    }
}
