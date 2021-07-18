// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import java.util.function.Predicate;
import com.gamesense.api.util.misc.MessageBus;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.api.util.player.social.SocialManager;
import org.lwjgl.input.Mouse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.RayTraceResult;
import me.zero.alpine.listener.EventHandler;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import me.zero.alpine.listener.Listener;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "MCF", category = Category.Misc)
public class MCF extends Module
{
    @EventHandler
    private final Listener<InputEvent.MouseInputEvent> listener;
    
    public MCF() {
        this.listener = new Listener<InputEvent.MouseInputEvent>(event -> {
            if (MCF.mc.field_71476_x.field_72313_a.equals((Object)RayTraceResult.Type.ENTITY) && MCF.mc.field_71476_x.field_72308_g instanceof EntityPlayer && Mouse.isButtonDown(2)) {
                if (SocialManager.isFriend(MCF.mc.field_71476_x.field_72308_g.func_70005_c_())) {
                    SocialManager.delFriend(MCF.mc.field_71476_x.field_72308_g.func_70005_c_());
                    MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getDisabledColor() + "Removed " + MCF.mc.field_71476_x.field_72308_g.func_70005_c_() + " from friends list");
                }
                else {
                    SocialManager.addFriend(MCF.mc.field_71476_x.field_72308_g.func_70005_c_());
                    MessageBus.sendClientPrefixMessage(ModuleManager.getModule(ColorMain.class).getEnabledColor() + "Added " + MCF.mc.field_71476_x.field_72308_g.func_70005_c_() + " to friends list");
                }
            }
        }, (Predicate<InputEvent.MouseInputEvent>[])new Predicate[0]);
    }
}
