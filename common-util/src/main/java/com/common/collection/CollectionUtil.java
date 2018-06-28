package com.common.collection;

import java.util.Map;

public class CollectionUtil {

    public static <K, V> void putIfNotNull(Map<K, V> map, K key, V value) {
        if (value != null)
            map.put(key, value);
    }

}
