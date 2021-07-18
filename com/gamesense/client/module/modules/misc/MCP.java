// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import net.minecraft.item.Item;
import com.gamesense.api.util.player.InventoryUtil;
import net.minecraft.item.ItemEnderPearl;
import org.lwjgl.input.Mouse;
import net.minecraft.util.math.RayTraceResult;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "MCP", category = Category.Misc)
public class MCP extends Module
{
    @Override
    public void onUpdate() {
        final RayTraceResult.Type type = MCP.mc.field_71476_x.field_72313_a;
        if (type.equals((Object)RayTraceResult.Type.MISS) && Mouse.isButtonDown(2)) {
            final int oldSlot = MCP.mc.field_71439_g.field_71071_by.field_70461_c;
            final int pearlSlot = InventoryUtil.findFirstItemSlot((Class<? extends Item>)ItemEnderPearl.class, 0, 8);
            if (pearlSlot != -1) {
                MCP.mc.field_71439_g.field_71071_by.field_70461_c = pearlSlot;
                MCP.mc.func_147121_ag();
                MCP.mc.field_71439_g.field_71071_by.field_70461_c = oldSlot;
            }
        }
    }
}
