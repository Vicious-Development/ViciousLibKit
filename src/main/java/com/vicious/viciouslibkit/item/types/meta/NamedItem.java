package com.vicious.viciouslibkit.item.types.meta;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Differentiates items with custom display names.
 */
public class NamedItem extends VanillaItem{
    public NamedItem(Material type) {
        super(type);
    }

    public NamedItem(Material type, ItemMeta meta) {
        super(type, meta);
    }

    public NamedItem(ItemStack stack) {
        super(stack);
    }
}
