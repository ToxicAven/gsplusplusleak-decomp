// 
// Decompiled by Procyon v0.5.36
// 

package com.gamesense.api.util.misc;

import java.util.Iterator;
import java.util.function.ToIntFunction;

public class CollectionUtil
{
    public static <T> T maxOrNull(final Iterable<T> iterable, final ToIntFunction<T> block) {
        int value = Integer.MIN_VALUE;
        T maxElement = null;
        for (final T element : iterable) {
            final int newValue = block.applyAsInt(element);
            if (newValue > value) {
                value = newValue;
                maxElement = element;
            }
        }
        return maxElement;
    }
}
