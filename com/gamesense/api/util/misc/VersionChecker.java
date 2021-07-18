// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.misc;

import com.gamesense.client.command.CommandManager;
import java.io.IOException;
import java.util.Scanner;
import java.net.URL;

public class VersionChecker
{
    public static String joinMessage;
    
    public static void init() {
    }
    
    private static void checkVersion(final String version) {
        boolean isLatest = true;
        String newVersion = "null";
        if (version.startsWith("d")) {
            return;
        }
        try {
            final URL url = new URL("https://raw.githubusercontent.com/IUDevman/gamesense-assets/main/files/versioncontrol.txt");
            final Scanner scanner = new Scanner(url.openStream());
            final String grabbedVersion = scanner.next();
            if (!version.equalsIgnoreCase(grabbedVersion)) {
                isLatest = false;
                newVersion = grabbedVersion;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            isLatest = true;
        }
        if (!isLatest) {
            VersionChecker.joinMessage = "Version (" + version + ") is outdated! Download the latest version (" + newVersion + ") by typing " + CommandManager.getCommandPrefix() + "releases!";
        }
    }
    
    static {
        VersionChecker.joinMessage = "None";
    }
}
