// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.mixin.mixins;

import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.render.NoRender;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({ LayerBipedArmor.class })
public class MixinLayerBipedArmor
{
    @Inject(method = { "setModelSlotVisible" }, at = { @At("HEAD") }, cancellable = true)
    protected void setModelSlotVisible(final ModelBiped model, final EntityEquipmentSlot slotIn, final CallbackInfo callbackInfo) {
        final NoRender noRender = ModuleManager.getModule(NoRender.class);
        if (noRender.isEnabled() && noRender.armor.getValue()) {
            callbackInfo.cancel();
            switch (slotIn) {
                case HEAD: {
                    model.field_78116_c.field_78806_j = false;
                    model.field_178720_f.field_78806_j = false;
                }
                case CHEST: {
                    model.field_78115_e.field_78806_j = false;
                    model.field_178723_h.field_78806_j = false;
                    model.field_178724_i.field_78806_j = false;
                }
                case LEGS: {
                    model.field_78115_e.field_78806_j = false;
                    model.field_178721_j.field_78806_j = false;
                    model.field_178722_k.field_78806_j = false;
                }
                case FEET: {
                    model.field_178721_j.field_78806_j = false;
                    model.field_178722_k.field_78806_j = false;
                    break;
                }
            }
        }
    }
}
