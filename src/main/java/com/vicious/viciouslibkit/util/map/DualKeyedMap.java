package com.vicious.viciouslibkit.util.map;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Maps a dual key value pair.
 */
public class DualKeyedMap<K1,K2,V>  {
    private Supplier<Map<K1,Map<K2,V>>> m1Supplier = HashMap::new;
    private Supplier<Map<K2,V>> m2Supplier = HashMap::new;
    private Map<K1,Map<K2,V>> m1;
    public DualKeyedMap(){
        m1 = this.m1Supplier.get();
    }
    public DualKeyedMap(Supplier<Map<K1,Map<K2,V>>> m1Supplier, Supplier<Map<K2,V>> m2Supplier){
        if(m1Supplier != null) this.m1Supplier=m1Supplier;
        if(m1Supplier != null) this.m2Supplier=m2Supplier;
        m1 = this.m1Supplier.get();
    }
    public Map<K2,V> get(K1 k1){
        return m1.get(k1);
    }
    public void put(K1 k1, K2 k2, V v){
        ensureExists(k1);
        get(k1).put(k2,v);
    }
    public V putIfAbsent(K1 k1, K2 k2, V v){
        ensureExists(k1);
        return get(k1).putIfAbsent(k2,v);
    }
    public void replaceInner(K1 k1, K2 k2, V v){
        ensureExists(k1);
        get(k1).replace(k2,v);
    }
    public void remove(K1 k1, K2 k2){
        ensureExists(k1);
        get(k1).remove(k2);
        if(get(k1).size() <= 0) remove(k1);
    }
    public void remove(K1 k1){
        m1.remove(k1);
    }
    public void clear(){
        m1.forEach((k,v)->{
            v.clear();
        });
        m1.clear();
    }
    public void clear(K1 k){
        m1.get(k).clear();
    }

    private void ensureExists(K1 k1) {
        m1.putIfAbsent(k1,m2Supplier.get());
    }
}
