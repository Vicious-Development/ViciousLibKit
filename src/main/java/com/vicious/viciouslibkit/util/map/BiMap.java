package com.vicious.viciouslibkit.util.map;

import java.util.HashMap;
import java.util.Map;


public class BiMap<K,V> {
    private Map<K,V> m1 = new HashMap<>();
    private Map<V,K> m2 = new HashMap<>();
    public void put(K k, V v){
        m1.put(k,v);
        m2.put(v,k);
    }
    public void remove(K k){
        m2.remove(m1.get(k));
        m1.remove(k);
    }
    public V getV(K k){
        return m1.get(k);
    }
    public K getK(V v){
        return m2.get(v);
    }
}
