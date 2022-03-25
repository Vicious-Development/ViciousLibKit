package com.vicious.viciouslibkit.item;

import org.bukkit.Material;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Uses the bukkit Material Enum. This covers all vanilla items.
 */
public class VanillaItem extends ItemType<Material, ItemMeta>{
    public VanillaItem(Material type) {
        super(type);
    }

    public VanillaItem(Material type, ItemMeta meta) {
        super(type, meta);
    }
}
