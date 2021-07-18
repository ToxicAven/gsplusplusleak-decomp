// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import net.minecraft.util.ResourceLocation;
import java.util.Iterator;
import net.minecraft.init.Items;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.client.renderer.RenderHelper;
import com.gamesense.api.util.font.FontUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import com.gamesense.api.util.player.social.SocialManager;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import net.minecraft.util.text.TextFormatting;
import com.gamesense.client.manager.managers.TotemPopManager;
import net.minecraft.client.renderer.GlStateManager;
import com.gamesense.api.util.render.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import java.util.function.Predicate;
import com.gamesense.api.event.events.RenderEvent;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.util.misc.ColorUtil;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "Nametags", category = Category.Render)
public class Nametags extends Module
{
    IntegerSetting range;
    BooleanSetting renderSelf;
    BooleanSetting showDurability;
    BooleanSetting showItems;
    BooleanSetting showEnchantName;
    BooleanSetting showItemName;
    BooleanSetting showGameMode;
    BooleanSetting showHealth;
    BooleanSetting showPing;
    BooleanSetting showTotem;
    BooleanSetting showEntityID;
    ModeSetting levelColor;
    public BooleanSetting customColor;
    public ColorSetting borderColor;
    
    public Nametags() {
        this.range = this.registerInteger("Range", 100, 10, 260);
        this.renderSelf = this.registerBoolean("Render Self", false);
        this.showDurability = this.registerBoolean("Durability", true);
        this.showItems = this.registerBoolean("Items", true);
        this.showEnchantName = this.registerBoolean("Enchants", true);
        this.showItemName = this.registerBoolean("Item Name", false);
        this.showGameMode = this.registerBoolean("Gamemode", false);
        this.showHealth = this.registerBoolean("Health", true);
        this.showPing = this.registerBoolean("Ping", false);
        this.showTotem = this.registerBoolean("Totem Pops", true);
        this.showEntityID = this.registerBoolean("Entity Id", false);
        this.levelColor = this.registerMode("Level Color", ColorUtil.colors, "Green");
        this.customColor = this.registerBoolean("Custom Color", true);
        this.borderColor = this.registerColor("Border Color", new GSColor(255, 0, 0, 255));
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        if (Nametags.mc.field_71439_g == null || Nametags.mc.field_71441_e == null) {
            return;
        }
        final Vec3d vec3d;
        Nametags.mc.field_71441_e.field_73010_i.stream().filter(this::shouldRender).forEach(entityPlayer -> {
            vec3d = this.findEntityVec3d(entityPlayer);
            this.renderNameTags(entityPlayer, vec3d.field_72450_a, vec3d.field_72448_b, vec3d.field_72449_c);
        });
    }
    
    private boolean shouldRender(final EntityPlayer entityPlayer) {
        return (entityPlayer != Nametags.mc.field_71439_g || this.renderSelf.getValue()) && !entityPlayer.field_70128_L && entityPlayer.func_110143_aJ() > 0.0f && entityPlayer.func_70032_d((Entity)Nametags.mc.field_71439_g) <= this.range.getValue();
    }
    
    private Vec3d findEntityVec3d(final EntityPlayer entityPlayer) {
        final double posX = this.balancePosition(entityPlayer.field_70165_t, entityPlayer.field_70142_S);
        final double posY = this.balancePosition(entityPlayer.field_70163_u, entityPlayer.field_70137_T);
        final double posZ = this.balancePosition(entityPlayer.field_70161_v, entityPlayer.field_70136_U);
        return new Vec3d(posX, posY, posZ);
    }
    
    private double balancePosition(final double newPosition, final double oldPosition) {
        return oldPosition + (newPosition - oldPosition) * Nametags.mc.field_71428_T.field_194147_b;
    }
    
    private void renderNameTags(final EntityPlayer entityPlayer, final double posX, final double posY, final double posZ) {
        final double adjustedY = posY + (entityPlayer.func_70093_af() ? 1.9 : 2.1);
        final String[] name = { this.buildEntityNameString(entityPlayer) };
        RenderUtil.drawNametag(posX, adjustedY, posZ, name, this.findTextColor(entityPlayer), 2);
        this.renderItemsAndArmor(entityPlayer, 0, 0);
        GlStateManager.func_179121_F();
    }
    
    private String buildEntityNameString(final EntityPlayer entityPlayer) {
        String name = entityPlayer.func_70005_c_();
        if (this.showEntityID.getValue()) {
            name = name + " ID: " + entityPlayer.func_145782_y();
        }
        if (this.showGameMode.getValue()) {
            if (entityPlayer.func_184812_l_()) {
                name += " [C]";
            }
            else if (entityPlayer.func_175149_v()) {
                name += " [I]";
            }
            else {
                name += " [S]";
            }
        }
        if (this.showTotem.getValue()) {
            name = name + " [" + TotemPopManager.INSTANCE.getPlayerPopCount(entityPlayer.func_70005_c_()) + "]";
        }
        if (this.showPing.getValue()) {
            int value = 0;
            if (Nametags.mc.func_147114_u() != null && Nametags.mc.func_147114_u().func_175102_a(entityPlayer.func_110124_au()) != null) {
                value = Nametags.mc.func_147114_u().func_175102_a(entityPlayer.func_110124_au()).func_178853_c();
            }
            name = name + " " + value + "ms";
        }
        if (this.showHealth.getValue()) {
            final int health = (int)(entityPlayer.func_110143_aJ() + entityPlayer.func_110139_bj());
            final TextFormatting textFormatting = this.findHealthColor(health);
            name = name + " " + textFormatting + health;
        }
        return name;
    }
    
    private TextFormatting findHealthColor(final int health) {
        if (health <= 0) {
            return TextFormatting.DARK_RED;
        }
        if (health <= 5) {
            return TextFormatting.RED;
        }
        if (health <= 10) {
            return TextFormatting.GOLD;
        }
        if (health <= 15) {
            return TextFormatting.YELLOW;
        }
        if (health <= 20) {
            return TextFormatting.DARK_GREEN;
        }
        return TextFormatting.GREEN;
    }
    
    private GSColor findTextColor(final EntityPlayer entityPlayer) {
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        if (SocialManager.isFriend(entityPlayer.func_70005_c_())) {
            return colorMain.getFriendGSColor();
        }
        if (SocialManager.isEnemy(entityPlayer.func_70005_c_())) {
            return colorMain.getEnemyGSColor();
        }
        if (entityPlayer.func_82150_aj()) {
            return new GSColor(128, 128, 128);
        }
        if (Nametags.mc.func_147114_u() != null && Nametags.mc.func_147114_u().func_175102_a(entityPlayer.func_110124_au()) == null) {
            return new GSColor(239, 1, 71);
        }
        if (entityPlayer.func_70093_af()) {
            return new GSColor(255, 153, 0);
        }
        return new GSColor(255, 255, 255);
    }
    
    private void renderItemsAndArmor(final EntityPlayer entityPlayer, int posX, int posY) {
        final ItemStack mainHandItem = entityPlayer.func_184614_ca();
        final ItemStack offHandItem = entityPlayer.func_184592_cb();
        int armorCount = 3;
        for (int i = 0; i <= 3; ++i) {
            final ItemStack itemStack = (ItemStack)entityPlayer.field_71071_by.field_70460_b.get(armorCount);
            if (!itemStack.func_190926_b()) {
                posX -= 8;
                final int size = EnchantmentHelper.func_82781_a(itemStack).size();
                if (this.showItems.getValue() && size > posY) {
                    posY = size;
                }
            }
            --armorCount;
        }
        if (!mainHandItem.func_190926_b() && (this.showItems.getValue() || (this.showDurability.getValue() && offHandItem.func_77984_f()))) {
            posX -= 8;
            final int enchantSize = EnchantmentHelper.func_82781_a(offHandItem).size();
            if (this.showItems.getValue() && enchantSize > posY) {
                posY = enchantSize;
            }
        }
        if (!mainHandItem.func_190926_b()) {
            final int enchantSize = EnchantmentHelper.func_82781_a(mainHandItem).size();
            if (this.showItems.getValue() && enchantSize > posY) {
                posY = enchantSize;
            }
            int armorY = this.findArmorY(posY);
            if (this.showItems.getValue() || (this.showDurability.getValue() && mainHandItem.func_77984_f())) {
                posX -= 8;
            }
            if (this.showItems.getValue()) {
                this.renderItem(mainHandItem, posX, armorY, posY);
                armorY -= 32;
            }
            if (this.showDurability.getValue() && mainHandItem.func_77984_f()) {
                this.renderItemDurability(mainHandItem, posX, armorY);
            }
            final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
            armorY -= (colorMain.customFont.getValue() ? FontUtil.getFontHeight(colorMain.customFont.getValue()) : Nametags.mc.field_71466_p.field_78288_b);
            if (this.showItemName.getValue()) {
                this.renderItemName(mainHandItem, armorY);
            }
            if (this.showItems.getValue() || (this.showDurability.getValue() && mainHandItem.func_77984_f())) {
                posX += 16;
            }
        }
        int armorCount2 = 3;
        for (int j = 0; j <= 3; ++j) {
            final ItemStack itemStack2 = (ItemStack)entityPlayer.field_71071_by.field_70460_b.get(armorCount2);
            if (!itemStack2.func_190926_b()) {
                int armorY2 = this.findArmorY(posY);
                if (this.showItems.getValue()) {
                    this.renderItem(itemStack2, posX, armorY2, posY);
                    armorY2 -= 32;
                }
                if (this.showDurability.getValue() && itemStack2.func_77984_f()) {
                    this.renderItemDurability(itemStack2, posX, armorY2);
                }
                posX += 16;
            }
            --armorCount2;
        }
        if (!offHandItem.func_190926_b()) {
            int armorY = this.findArmorY(posY);
            if (this.showItems.getValue()) {
                this.renderItem(offHandItem, posX, armorY, posY);
                armorY -= 32;
            }
            if (this.showDurability.getValue() && offHandItem.func_77984_f()) {
                this.renderItemDurability(offHandItem, posX, armorY);
            }
        }
    }
    
    private int findArmorY(final int posY) {
        int posY2 = this.showItems.getValue() ? -26 : -27;
        if (posY > 4) {
            posY2 -= (posY - 4) * 8;
        }
        return posY2;
    }
    
    private void renderItemName(final ItemStack itemStack, final int posY) {
        GlStateManager.func_179098_w();
        GlStateManager.func_179094_E();
        GlStateManager.func_179139_a(0.5, 0.5, 0.5);
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        FontUtil.drawStringWithShadow(colorMain.customFont.getValue(), itemStack.func_82833_r(), -FontUtil.getStringWidth(colorMain.customFont.getValue(), itemStack.func_82833_r()) / 2, posY, new GSColor(255, 255, 255));
        GlStateManager.func_179121_F();
        GlStateManager.func_179090_x();
    }
    
    private void renderItemDurability(final ItemStack itemStack, final int posX, final int posY) {
        float green;
        final float damagePercent = green = (itemStack.func_77958_k() - itemStack.func_77952_i()) / (float)itemStack.func_77958_k();
        if (green > 1.0f) {
            green = 1.0f;
        }
        else if (green < 0.0f) {
            green = 0.0f;
        }
        final float red = 1.0f - green;
        GlStateManager.func_179098_w();
        GlStateManager.func_179094_E();
        GlStateManager.func_179139_a(0.5, 0.5, 0.5);
        final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
        FontUtil.drawStringWithShadow(colorMain.customFont.getValue(), (int)(damagePercent * 100.0f) + "%", posX * 2, posY, new GSColor((int)(red * 255.0f), (int)(green * 255.0f), 0));
        GlStateManager.func_179121_F();
        GlStateManager.func_179090_x();
    }
    
    private void renderItem(final ItemStack itemStack, final int posX, final int posY, final int posY2) {
        GlStateManager.func_179098_w();
        GlStateManager.func_179132_a(true);
        GlStateManager.func_179086_m(256);
        GlStateManager.func_179126_j();
        GlStateManager.func_179118_c();
        final int posY3 = (posY2 > 4) ? ((posY2 - 4) * 8 / 2) : 0;
        Nametags.mc.func_175599_af().field_77023_b = -150.0f;
        RenderHelper.func_74519_b();
        Nametags.mc.func_175599_af().func_180450_b(itemStack, posX, posY + posY3);
        Nametags.mc.func_175599_af().func_175030_a(Nametags.mc.field_71466_p, itemStack, posX, posY + posY3);
        RenderHelper.func_74518_a();
        Nametags.mc.func_175599_af().field_77023_b = 0.0f;
        RenderUtil.prepare();
        GlStateManager.func_179094_E();
        GlStateManager.func_179139_a(0.5, 0.5, 0.5);
        this.renderEnchants(itemStack, posX, posY - 24);
        GlStateManager.func_179121_F();
    }
    
    private void renderEnchants(final ItemStack itemStack, final int posX, int posY) {
        GlStateManager.func_179098_w();
        for (final Enchantment enchantment : EnchantmentHelper.func_82781_a(itemStack).keySet()) {
            if (enchantment == null) {
                continue;
            }
            if (this.showEnchantName.getValue()) {
                final int level = EnchantmentHelper.func_77506_a(enchantment, itemStack);
                final ColorMain colorMain = ModuleManager.getModule(ColorMain.class);
                FontUtil.drawStringWithShadow(colorMain.customFont.getValue(), this.findStringForEnchants(enchantment, level), posX * 2, posY, new GSColor(255, 255, 255));
            }
            posY += 8;
        }
        if (itemStack.func_77973_b().equals(Items.field_151153_ao) && itemStack.func_77962_s()) {
            final ColorMain colorMain2 = ModuleManager.getModule(ColorMain.class);
            FontUtil.drawStringWithShadow(colorMain2.customFont.getValue(), "God", posX * 2, posY, new GSColor(195, 77, 65));
        }
        GlStateManager.func_179090_x();
    }
    
    private String findStringForEnchants(final Enchantment enchantment, final int level) {
        final ResourceLocation resourceLocation = (ResourceLocation)Enchantment.field_185264_b.func_177774_c((Object)enchantment);
        String string = (resourceLocation == null) ? enchantment.func_77320_a() : resourceLocation.toString();
        final int charCount = (level > 1) ? 12 : 13;
        if (string.length() > charCount) {
            string = string.substring(10, charCount);
        }
        return string.substring(0, 1).toUpperCase() + string.substring(1) + ColorUtil.settingToTextFormatting(this.levelColor) + ((level > 1) ? Integer.valueOf(level) : "");
    }
}
