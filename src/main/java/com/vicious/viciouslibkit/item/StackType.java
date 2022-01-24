package com.vicious.viciouslibkit.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class StackType {
    public Material material;
    public ItemMeta meta;
    public StackType(Material material, ItemMeta meta){
        this.meta=meta;
        this.material=material;
    }

    public StackType(ItemStack stack) {
        this.meta=stack.getItemMeta();
        this.material=stack.getType();
    }
    public boolean isType(ItemStack stack){
        return meta.equals(stack.getItemMeta()) && stack.getType() == material;
    }
    public String toString(){
        return "StackType: " + material + " : " + meta;
    }
}
