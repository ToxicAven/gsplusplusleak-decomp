// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command.commands;

import com.gamesense.api.setting.Setting;
import com.gamesense.client.module.Module;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.DoubleSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.SettingsManager;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.command.Command;

@Declaration(name = "Set", syntax = "set [module] [setting] value (no color support)", alias = { "set", "setmodule", "changesetting", "setting" })
public class SetCommand extends Command
{
    @Override
    public void onCommand(final String command, final String[] message) {
        final String main = message[0];
        final Module module = ModuleManager.getModule(main);
        if (module == null) {
            MessageBus.sendCommandMessage(this.getSyntax(), true);
            return;
        }
        final Module module2;
        SettingsManager.getSettingsForModule(module).stream().filter(setting -> setting.getConfigName().equalsIgnoreCase(message[1])).forEach(setting -> {
            if (setting instanceof BooleanSetting) {
                if (message[2].equalsIgnoreCase("true") || message[2].equalsIgnoreCase("false")) {
                    setting.setValue(Boolean.parseBoolean(message[2]));
                    MessageBus.sendCommandMessage(module2.getName() + " " + setting.getConfigName() + " set to: " + setting.getValue() + "!", true);
                }
                else {
                    MessageBus.sendCommandMessage(this.getSyntax(), true);
                }
            }
            else if (setting instanceof IntegerSetting) {
                if (Integer.parseInt(message[2]) > ((IntegerSetting)setting).getMax()) {
                    setting.setValue((Boolean)(Object)Integer.valueOf(((IntegerSetting)setting).getMax()));
                }
                if (Integer.parseInt(message[2]) < ((IntegerSetting)setting).getMin()) {
                    setting.setValue((Boolean)(Object)Integer.valueOf(((IntegerSetting)setting).getMin()));
                }
                if (Integer.parseInt(message[2]) < ((IntegerSetting)setting).getMax() && Integer.parseInt(message[2]) > ((IntegerSetting)setting).getMin()) {
                    setting.setValue((Boolean)(Object)Integer.valueOf(Integer.parseInt(message[2])));
                }
                MessageBus.sendCommandMessage(module2.getName() + " " + setting.getConfigName() + " set to: " + setting.getValue() + "!", true);
            }
            else if (setting instanceof DoubleSetting) {
                if (Double.parseDouble(message[2]) > ((DoubleSetting)setting).getMax()) {
                    setting.setValue((Boolean)(Object)Double.valueOf(((DoubleSetting)setting).getMax()));
                }
                if (Double.parseDouble(message[2]) < ((DoubleSetting)setting).getMin()) {
                    setting.setValue((Boolean)(Object)Double.valueOf(((DoubleSetting)setting).getMin()));
                }
                if (Double.parseDouble(message[2]) < ((DoubleSetting)setting).getMax() && Double.parseDouble(message[2]) > ((DoubleSetting)setting).getMin()) {
                    setting.setValue((Boolean)(Object)Double.valueOf(Double.parseDouble(message[2])));
                }
                MessageBus.sendCommandMessage(module2.getName() + " " + setting.getConfigName() + " set to: " + setting.getValue() + "!", true);
            }
            else if (setting instanceof ModeSetting) {
                if (!((ModeSetting)setting).getModes().contains(message[2])) {
                    MessageBus.sendCommandMessage(this.getSyntax(), true);
                }
                else {
                    setting.setValue((Boolean)message[2]);
                    MessageBus.sendCommandMessage(module2.getName() + " " + setting.getConfigName() + " set to: " + setting.getValue() + "!", true);
                }
            }
            else {
                MessageBus.sendCommandMessage(this.getSyntax(), true);
            }
        });
    }
}
