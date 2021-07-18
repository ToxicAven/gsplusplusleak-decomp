// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.api.util.misc.ZipUtils;
import java.io.File;
import java.util.Date;
import java.text.SimpleDateFormat;
import com.gamesense.client.command.Command;

@Declaration(name = "BackupConfig", syntax = "backupconfig", alias = { "backupconfig" })
public class BackupConfigCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        final String filename = "gamesense-cofig-backup-p2.3.1.c4-" + new SimpleDateFormat("yyyyMMdd.HHmmss.SSS").format(new Date()) + ".zip";
        ZipUtils.zip(new File("GameSense/"), new File(filename));
        MessageBus.sendCommandMessage("Config successfully saved in " + filename + "!", true);
    }
}
