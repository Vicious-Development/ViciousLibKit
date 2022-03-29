package com.vicious.viciouslibkit.item;

import com.google.common.collect.HashBiMap;
import com.vicious.viciouslibkit.util.map.BiMap;
import org.apache.commons.lang.NullArgumentException;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;


/**
 * For plugins implementing custom namespaced items.
 */
public class NamespacedItem extends ItemType<Integer, ItemMeta>{
    private static Set<NamespacedKey> knownKeys = new HashSet<>();
    //The integer type of an item is dependent on the order items are registered here. They will NOT remain the same.
    private static BiMap<String,Integer> customItemIntMapping = new BiMap<>();
    private static int nextID = 0;
    //SourceKey differentiates between different item addition plugins. Necessary when items share the same id but are in fact different.
    protected NamespacedKey sourceKey;

    public NamespacedItem(ItemStack stack) throws NotCustomItemException{
        ItemMeta meta = stack.getItemMeta();
        if(meta == null) throw new NotCustomItemException("The stack MUST have an ItemMeta!");
        String id = null;
        NamespacedKey skey = null;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        for (NamespacedKey knownKey : knownKeys) {
            try{
                skey = knownKey;
                id = pdc.get(knownKey, PersistentDataType.STRING);
                if(id != null) break;
            } catch (Exception ignored){}
        }
        for (NamespacedKey key : pdc.getKeys()) {
            if(key.getKey().equals("id")){
                knownKeys.add(key);
                skey = key;
                id = pdc.get(key, PersistentDataType.STRING);
                if(id != null) {
                    break;
                }
            }
        }
        if(id == null) throw new NotCustomItemException("The stack MUST have an id key");
        type=getIntMapping(id);
        this.meta=stack.getItemMeta();
        sourceKey=skey;
    }
    public NamespacedItem(Integer type, ItemMeta meta, NamespacedKey key){
        super(type,meta);
        sourceKey=key;
    }

    public NamespacedItem(Integer type) {
        super(type);
    }
    public NamespacedItem(Integer type, ItemMeta meta) {
        super(type, meta);
    }

    public static Integer getCustomID(ItemStack stack){
        ItemMeta meta = stack.getItemMeta();
        if(meta == null) return null;
        else{
            PersistentDataContainer pdc = meta.getPersistentDataContainer();
            for (NamespacedKey knownKey : knownKeys) {
                try{
                    String id = pdc.get(knownKey, PersistentDataType.STRING);
                    if(id == null) continue;
                    return getIntMapping(id);
                } catch (Exception ignored){}
            }
            for (NamespacedKey key : pdc.getKeys()) {
                if(key.getKey().equals("id")){
                    knownKeys.add(key);
                    String id = pdc.get(key, PersistentDataType.STRING);
                    if(id == null) continue;
                    return getIntMapping(id);
                }
            }
        }
        return null;
    }
    public String getName(){
        return customItemIntMapping.getK(type);
    }

    @Override
    public boolean isType(ItemType<?, ?> type, boolean ignoreMeta) {
        if(!super.isType(type, ignoreMeta)) return false;
        else if(type instanceof NamespacedItem) {
            return ((NamespacedItem) type).sourceKey.equals(sourceKey);
        }
        else return false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(this.type, ((NamespacedItem) o).type) && this.meta.equals(((NamespacedItem) o).meta) && sourceKey.equals(((NamespacedItem) o).sourceKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), sourceKey);
    }

    private static Integer getIntMapping(String id) {
        Integer num = customItemIntMapping.getV(id);
        if(num == null){
            num = nextID;
            customItemIntMapping.put(id,num);
            nextID++;
        }
        return num;
    }
}
