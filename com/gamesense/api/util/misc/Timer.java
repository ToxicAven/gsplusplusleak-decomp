// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.misc;

public class Timer
{
    private long current;
    
    public Timer() {
        this.current = System.currentTimeMillis();
    }
    
    public boolean hasReached(final long delay) {
        return System.currentTimeMillis() - this.current >= delay;
    }
    
    public boolean hasReached(final long delay, final boolean reset) {
        if (reset) {
            this.reset();
        }
        return System.currentTimeMillis() - this.current >= delay;
    }
    
    public void reset() {
        this.current = System.currentTimeMillis();
    }
    
    public long getTimePassed() {
        return System.currentTimeMillis() - this.current;
    }
    
    public boolean sleep(final long time) {
        if (this.time() >= time) {
            this.reset();
            return true;
        }
        return false;
    }
    
    public long time() {
        return System.currentTimeMillis() - this.current;
    }
}
