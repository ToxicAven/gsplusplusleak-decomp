// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import java.util.Iterator;
import java.util.Collection;
import com.gamesense.client.command.CommandManager;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.Style;
import com.mojang.realmsclient.gui.ChatFormatting;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.ModuleManager;
import net.minecraft.util.text.TextComponentString;
import com.gamesense.client.command.Command;

@Declaration(name = "Modules", syntax = "modules (click to toggle)", alias = { "modules", "module", "modulelist", "mod", "mods" })
public class ModulesCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        final TextComponentString msg = new TextComponentString("§7Modules: §f ");
        final Collection<Module> modules = ModuleManager.getModules();
        final int size = modules.size();
        int index = 0;
        for (final Module module : modules) {
            msg.func_150257_a(new TextComponentString((module.isEnabled() ? ChatFormatting.GREEN : ChatFormatting.RED) + module.getName() + "§7" + ((index == size - 1) ? "" : ", ")).func_150255_a(new Style().func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_TEXT, (ITextComponent)new TextComponentString(module.getCategory().name()))).func_150241_a(new ClickEvent(ClickEvent.Action.RUN_COMMAND, CommandManager.getCommandPrefix() + "toggle " + module.getName()))));
            ++index;
        }
        msg.func_150257_a((ITextComponent)new TextComponentString(ChatFormatting.GRAY + "!"));
        ModulesCommand.mc.field_71456_v.func_146158_b().func_146227_a((ITextComponent)msg);
    }
}
