package com.vicious.viciouslibkit.item;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StackType stackType = (StackType) o;
        return material == stackType.material && Objects.equals(meta, stackType.meta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(material, meta);
    }
}
