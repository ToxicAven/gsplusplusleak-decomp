// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.render;

import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.tileentity.TileEntityEnderChest;
import com.gamesense.api.util.render.RenderUtil;
import net.minecraft.world.World;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.monster.EntityMob;
import com.gamesense.client.module.ModuleManager;
import com.gamesense.client.module.modules.gui.ColorMain;
import com.gamesense.api.util.player.social.SocialManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.item.EntityEnderCrystal;
import com.gamesense.api.event.events.RenderEvent;
import java.util.Arrays;
import com.gamesense.api.util.render.GSColor;
import com.gamesense.api.setting.values.BooleanSetting;
import com.gamesense.api.setting.values.ModeSetting;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.api.setting.values.ColorSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "ESP", category = Category.Render)
public class ESP extends Module
{
    ColorSetting mainColor;
    IntegerSetting range;
    IntegerSetting width;
    ModeSetting playerESPMode;
    ModeSetting mobESPMode;
    BooleanSetting entityRender;
    BooleanSetting itemRender;
    BooleanSetting containerRender;
    BooleanSetting glowCrystals;
    GSColor playerColor;
    GSColor mobColor;
    GSColor mainIntColor;
    GSColor containerColor;
    int opacityGradient;
    
    public ESP() {
        this.mainColor = this.registerColor("Color");
        this.range = this.registerInteger("Range", 100, 10, 260);
        this.width = this.registerInteger("Line Width", 2, 1, 5);
        this.playerESPMode = this.registerMode("Player", Arrays.asList("None", "Glowing", "Box", "Direction"), "Box");
        this.mobESPMode = this.registerMode("Mob", Arrays.asList("None", "Glowing", "Box", "Direction"), "Box");
        this.entityRender = this.registerBoolean("Entity", false);
        this.itemRender = this.registerBoolean("Item", true);
        this.containerRender = this.registerBoolean("Container", false);
        this.glowCrystals = this.registerBoolean("Glow Crystal", false);
    }
    
    @Override
    public void onWorldRender(final RenderEvent event) {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     3: getfield        net/minecraft/client/Minecraft.field_71441_e:Lnet/minecraft/client/multiplayer/WorldClient;
        //     6: getfield        net/minecraft/client/multiplayer/WorldClient.field_72996_f:Ljava/util/List;
        //     9: invokeinterface java/util/List.stream:()Ljava/util/stream/Stream;
        //    14: invokedynamic   BootstrapMethod #0, test:()Ljava/util/function/Predicate;
        //    19: invokeinterface java/util/stream/Stream.filter:(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;
        //    24: aload_0         /* this */
        //    25: invokedynamic   BootstrapMethod #1, test:(Lcom/gamesense/client/module/modules/render/ESP;)Ljava/util/function/Predicate;
        //    30: invokeinterface java/util/stream/Stream.filter:(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;
        //    35: aload_0         /* this */
        //    36: invokedynamic   BootstrapMethod #2, accept:(Lcom/gamesense/client/module/modules/render/ESP;)Ljava/util/function/Consumer;
        //    41: invokeinterface java/util/stream/Stream.forEach:(Ljava/util/function/Consumer;)V
        //    46: aload_0         /* this */
        //    47: getfield        com/gamesense/client/module/modules/render/ESP.containerRender:Lcom/gamesense/api/setting/values/BooleanSetting;
        //    50: invokevirtual   com/gamesense/api/setting/values/BooleanSetting.getValue:()Ljava/lang/Object;
        //    53: checkcast       Ljava/lang/Boolean;
        //    56: invokevirtual   java/lang/Boolean.booleanValue:()Z
        //    59: ifeq            98
        //    62: getstatic       com/gamesense/client/module/modules/render/ESP.mc:Lnet/minecraft/client/Minecraft;
        //    65: getfield        net/minecraft/client/Minecraft.field_71441_e:Lnet/minecraft/client/multiplayer/WorldClient;
        //    68: getfield        net/minecraft/client/multiplayer/WorldClient.field_147482_g:Ljava/util/List;
        //    71: invokeinterface java/util/List.stream:()Ljava/util/stream/Stream;
        //    76: aload_0         /* this */
        //    77: invokedynamic   BootstrapMethod #3, test:(Lcom/gamesense/client/module/modules/render/ESP;)Ljava/util/function/Predicate;
        //    82: invokeinterface java/util/stream/Stream.filter:(Ljava/util/function/Predicate;)Ljava/util/stream/Stream;
        //    87: aload_0         /* this */
        //    88: invokedynamic   BootstrapMethod #4, accept:(Lcom/gamesense/client/module/modules/render/ESP;)Ljava/util/function/Consumer;
        //    93: invokeinterface java/util/stream/Stream.forEach:(Ljava/util/function/Consumer;)V
        //    98: return         
        //    StackMapTable: 00 01 FB 00 62
        // 
        // The error that occurred was:
        // 
        // java.lang.NullPointerException
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.generateNameForVariable(NameVariables.java:264)
        //     at com.strobel.decompiler.languages.java.ast.NameVariables.assignNamesToVariables(NameVariables.java:198)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:276)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:99)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:782)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:675)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:552)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:519)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:161)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:150)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:125)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:330)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:251)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:126)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void onDisable() {
        ESP.mc.field_71441_e.field_72996_f.stream().forEach(entity -> {
            if ((entity instanceof EntityEnderCrystal || entity instanceof EntityPlayer || entity instanceof EntityCreature || entity instanceof EntitySlime || entity instanceof EntitySquid) && entity.func_184202_aL()) {
                entity.func_184195_f(false);
            }
        });
    }
    
    private void defineEntityColors(final Entity entity) {
        if (entity instanceof EntityPlayer) {
            if (SocialManager.isFriend(entity.func_70005_c_())) {
                this.playerColor = ModuleManager.getModule(ColorMain.class).getFriendGSColor();
            }
            else if (SocialManager.isEnemy(entity.func_70005_c_())) {
                this.playerColor = ModuleManager.getModule(ColorMain.class).getEnemyGSColor();
            }
            else {
                this.playerColor = new GSColor(this.mainColor.getValue(), this.opacityGradient);
            }
        }
        if (entity instanceof EntityMob) {
            this.mobColor = new GSColor(255, 0, 0, this.opacityGradient);
        }
        else if (entity instanceof EntityAnimal || entity instanceof EntitySquid) {
            this.mobColor = new GSColor(0, 255, 0, this.opacityGradient);
        }
        else {
            this.mobColor = new GSColor(255, 165, 0, this.opacityGradient);
        }
        if (entity instanceof EntitySlime) {
            this.mobColor = new GSColor(255, 0, 0, this.opacityGradient);
        }
        if (entity != null) {
            this.mainIntColor = new GSColor(this.mainColor.getValue(), this.opacityGradient);
        }
    }
    
    private boolean rangeEntityCheck(final Entity entity) {
        if (entity.func_70032_d((Entity)ESP.mc.field_71439_g) > this.range.getValue()) {
            return false;
        }
        if (entity.func_70032_d((Entity)ESP.mc.field_71439_g) >= 180.0f) {
            this.opacityGradient = 50;
        }
        else if (entity.func_70032_d((Entity)ESP.mc.field_71439_g) >= 130.0f && entity.func_70032_d((Entity)ESP.mc.field_71439_g) < 180.0f) {
            this.opacityGradient = 100;
        }
        else if (entity.func_70032_d((Entity)ESP.mc.field_71439_g) >= 80.0f && entity.func_70032_d((Entity)ESP.mc.field_71439_g) < 130.0f) {
            this.opacityGradient = 150;
        }
        else if (entity.func_70032_d((Entity)ESP.mc.field_71439_g) >= 30.0f && entity.func_70032_d((Entity)ESP.mc.field_71439_g) < 80.0f) {
            this.opacityGradient = 200;
        }
        else {
            this.opacityGradient = 255;
        }
        return true;
    }
    
    private boolean rangeTileCheck(final TileEntity tileEntity) {
        if (tileEntity.func_145835_a(ESP.mc.field_71439_g.field_70165_t, ESP.mc.field_71439_g.field_70163_u, ESP.mc.field_71439_g.field_70161_v) > this.range.getValue() * this.range.getValue()) {
            return false;
        }
        if (tileEntity.func_145835_a(ESP.mc.field_71439_g.field_70165_t, ESP.mc.field_71439_g.field_70163_u, ESP.mc.field_71439_g.field_70161_v) >= 32400.0) {
            this.opacityGradient = 50;
        }
        else if (tileEntity.func_145835_a(ESP.mc.field_71439_g.field_70165_t, ESP.mc.field_71439_g.field_70163_u, ESP.mc.field_71439_g.field_70161_v) >= 16900.0 && tileEntity.func_145835_a(ESP.mc.field_71439_g.field_70165_t, ESP.mc.field_71439_g.field_70163_u, ESP.mc.field_71439_g.field_70161_v) < 32400.0) {
            this.opacityGradient = 100;
        }
        else if (tileEntity.func_145835_a(ESP.mc.field_71439_g.field_70165_t, ESP.mc.field_71439_g.field_70163_u, ESP.mc.field_71439_g.field_70161_v) >= 6400.0 && tileEntity.func_145835_a(ESP.mc.field_71439_g.field_70165_t, ESP.mc.field_71439_g.field_70163_u, ESP.mc.field_71439_g.field_70161_v) < 16900.0) {
            this.opacityGradient = 150;
        }
        else if (tileEntity.func_145835_a(ESP.mc.field_71439_g.field_70165_t, ESP.mc.field_71439_g.field_70163_u, ESP.mc.field_71439_g.field_70161_v) >= 900.0 && tileEntity.func_145835_a(ESP.mc.field_71439_g.field_70165_t, ESP.mc.field_71439_g.field_70163_u, ESP.mc.field_71439_g.field_70161_v) < 6400.0) {
            this.opacityGradient = 200;
        }
        else {
            this.opacityGradient = 255;
        }
        return true;
    }
}
