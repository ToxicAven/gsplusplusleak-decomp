// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.client.module.modules.misc;

import com.gamesense.api.util.misc.MessageBus;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Scanner;
import java.net.URL;
import java.util.LinkedList;
import java.util.Random;
import java.util.List;
import com.gamesense.api.setting.values.IntegerSetting;
import com.gamesense.client.module.Category;
import com.gamesense.client.module.Module;

@Declaration(name = "PhysicsSpammer", category = Category.Misc)
public class PhysicsSpammer extends Module
{
    IntegerSetting minDelay;
    IntegerSetting maxDelay;
    private final List<String> cache;
    private long lastTime;
    private long delay;
    private final Random random;
    
    public PhysicsSpammer() {
        this.minDelay = this.registerInteger("Min Delay", 5, 1, 100);
        this.maxDelay = this.registerInteger("Max Delay", 5, 1, 100);
        this.cache = new LinkedList<String>();
        this.random = new Random(System.currentTimeMillis());
        this.updateTimes();
    }
    
    @Override
    public void onUpdate() {
        if (this.delay > Math.max(this.minDelay.getValue(), this.maxDelay.getValue())) {
            this.delay = Math.max(this.minDelay.getValue(), this.maxDelay.getValue());
        }
        else if (this.delay < Math.min(this.minDelay.getValue(), this.maxDelay.getValue())) {
            this.delay = Math.min(this.minDelay.getValue(), this.maxDelay.getValue());
        }
        if (System.currentTimeMillis() >= this.lastTime + 1000L * this.delay) {
            if (this.cache.size() == 0) {
                try {
                    final Scanner scanner = new Scanner(new URL("http://snarxiv.org/").openStream());
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.startsWith("<p>")) {
                            if (line.startsWith("<p><a")) {
                                continue;
                            }
                            if (line.startsWith("<p>Links to:")) {
                                continue;
                            }
                            line = line.substring(3);
                            while (true) {
                                final int pos = line.indexOf(". ");
                                if (pos < 0) {
                                    break;
                                }
                                this.cache.add(line.substring(0, pos + 1));
                                line = line.substring(pos + 2);
                            }
                            this.cache.add(line);
                        }
                    }
                    scanner.close();
                }
                catch (MalformedURLException ex) {}
                catch (IOException ex2) {}
            }
            if (this.cache.size() == 0) {
                this.cache.add("Error! :(");
            }
            MessageBus.sendServerMessage("> " + this.cache.get(0));
            this.cache.remove(0);
            this.updateTimes();
        }
    }
    
    private void updateTimes() {
        this.lastTime = System.currentTimeMillis();
        final int bound = Math.abs(this.maxDelay.getValue() - this.minDelay.getValue());
        this.delay = ((bound == 0) ? 0 : this.random.nextInt(bound)) + Math.min(this.maxDelay.getValue(), this.minDelay.getValue());
    }
}
