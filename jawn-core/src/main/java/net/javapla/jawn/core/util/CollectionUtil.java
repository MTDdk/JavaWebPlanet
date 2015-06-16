package net.javapla.jawn.core.util;

import java.util.HashMap;
import java.util.Map;

public class CollectionUtil {
    
    /**
     * Reduces overhead compared to the far more advanced {@link #map(Object...)}.
     * 
     * @param key A single key
     * @param <K> type of the key
     * @param value A single value
     * @param <V> type of the value
     * @return A Modifiable Map with a single entry
     */
    public static final <K, V> Map<K, V> map(K key, V value) {
        Map<K, V> map = new HashMap<>();
        map.put(key, value);
        return map;
    }

    /**
     * Converting a list into a map
     * 
     * @param keyValues An even list of key/value pairs to be converted into a map
     * @param <K> type of the keys
     * @param <V> type of the values
     * 
     * @return The resulting map of the values
     * @throws IllegalArgumentException If the number of <code>keyValues</code> is not even
     */
    @SuppressWarnings("unchecked")
    public static final <K, V> Map<K, V> map(Object... keyValues ) throws IllegalArgumentException {
        //             length % 2 != 0
        if ((keyValues.length & 0b01) != 0) throw new IllegalArgumentException("number of arguments must be even");
        
        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < keyValues.length; i+=2) {
            K key = (K) keyValues[i];
            V value = (V) keyValues[i+1];
            map.put(key, value);
        }
        
        return map;
    }
    
}
