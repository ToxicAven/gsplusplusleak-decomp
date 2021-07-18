// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.hud;

import com.gamesense.client.module.ModuleManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.text.TextComponentString;
import com.lukflug.panelstudio.hud.HUDList;
import com.lukflug.panelstudio.hud.ListComponent;
import com.lukflug.panelstudio.theme.Theme;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;
import com.gamesense.client.module.HUDModule;

@Module.Declaration(name = "Notifications", category = Category.HUD)
@Declaration(posX = 0, posZ = 50)
public class Notifications extends HUDModule
{
    public BooleanSetting sortUp;
    public BooleanSetting sortRight;
    public BooleanSetting disableChat;
    private static final NotificationsList list;
    private static int waitCounter;
    
    public Notifications() {
        this.sortUp = this.registerBoolean("Sort Up", false);
        this.sortRight = this.registerBoolean("Sort Right", false);
        this.disableChat = this.registerBoolean("No Chat Msg", true);
    }
    
    @Override
    public void populate(final Theme theme) {
        this.component = new ListComponent(this.getName(), theme.getPanelRenderer(), this.position, Notifications.list);
    }
    
    @Override
    public void onUpdate() {
        if (Notifications.waitCounter < 500) {
            ++Notifications.waitCounter;
            return;
        }
        Notifications.waitCounter = 0;
        if (Notifications.list.list.size() > 0) {
            Notifications.list.list.remove(0);
        }
    }
    
    public void addMessage(final TextComponentString m) {
        if (Notifications.list.list.size() < 3) {
            Notifications.list.list.remove(m);
            Notifications.list.list.add(m);
        }
        else {
            Notifications.list.list.remove(0);
            Notifications.list.list.remove(m);
            Notifications.list.list.add(m);
        }
    }
    
    static {
        list = new NotificationsList();
    }
    
    private static class NotificationsList implements HUDList
    {
        public List<TextComponentString> list;
        
        private NotificationsList() {
            this.list = new ArrayList<TextComponentString>();
        }
        
        @Override
        public int getSize() {
            return this.list.size();
        }
        
        @Override
        public String getItem(final int index) {
            return this.list.get(index).func_150265_g();
        }
        
        @Override
        public Color getItemColor(final int index) {
            return new Color(255, 255, 255);
        }
        
        @Override
        public boolean sortUp() {
            return ModuleManager.getModule(Notifications.class).sortUp.isOn();
        }
        
        @Override
        public boolean sortRight() {
            return ModuleManager.getModule(Notifications.class).sortRight.isOn();
        }
    }
}
