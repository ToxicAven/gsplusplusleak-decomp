// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;
import net.minecraft.client.Minecraft;

public abstract class Command
{
    protected static final Minecraft mc;
    private final String name;
    private final String[] alias;
    private final String syntax;
    
    public Command() {
        this.name = this.getDeclaration().name();
        this.alias = this.getDeclaration().alias();
        this.syntax = this.getDeclaration().syntax();
    }
    
    private Declaration getDeclaration() {
        return this.getClass().getAnnotation(Declaration.class);
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getSyntax() {
        return CommandManager.getCommandPrefix() + this.syntax;
    }
    
    public String[] getAlias() {
        return this.alias;
    }
    
    public abstract void onCommand(final String p0, final String[] p1);
    
    static {
        mc = Minecraft.func_71410_x();
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface Declaration {
        String name();
        
        String syntax();
        
        String[] alias();
    }
}
