// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Retention;
import java.lang.annotation.Annotation;
import com.lukflug.panelstudio.Interface;
import com.gamesense.client.GameSense;
import com.lukflug.panelstudio.theme.Theme;
import java.awt.Point;
import com.lukflug.panelstudio.FixedComponent;

public abstract class HUDModule extends Module
{
    protected FixedComponent component;
    protected Point position;
    
    public HUDModule() {
        this.position = new Point(this.getDeclaration().posX(), this.getDeclaration().posZ());
    }
    
    private Declaration getDeclaration() {
        return this.getClass().getAnnotation(Declaration.class);
    }
    
    public abstract void populate(final Theme p0);
    
    public FixedComponent getComponent() {
        return this.component;
    }
    
    public void resetPosition() {
        this.component.setPosition(GameSense.INSTANCE.gameSenseGUI.guiInterface, this.position);
    }
    
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    public @interface Declaration {
        int posX();
        
        int posZ();
    }
}
