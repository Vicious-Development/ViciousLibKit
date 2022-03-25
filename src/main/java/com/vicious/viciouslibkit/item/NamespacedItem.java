package com.vicious.viciouslibkit.item;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.meta.ItemMeta;


/**
 * For plugins implementing custom namespaced items.
 */
public class NamespacedItem extends ItemType<String, ItemMeta>{
    private static Map<NamespacedKey>
    public NamespacedItem(String type) {
        super(type);
        ItemMeta metaTest = getMeta().getPersistentDataContainer().getKeys().forEach((k)->{

        });
    }

    public NamespacedItem(String type, ItemMeta meta) {
        super(type, meta);
    }
}
