package com.vicious.viciouslibkit.item;

import com.vicious.viciouslibkit.ViciousLibKit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public interface ICustomItem {
    static NamespacedKey customItemKey = new NamespacedKey(ViciousLibKit.INSTANCE,"customitem");
    default ItemStack markCustom(ItemStack stack){
        if(stack.hasItemMeta()){
            ItemMeta meta = stack.getItemMeta();
            meta.getPersistentDataContainer().set(customItemKey,PersistentDataType.STRING,key());
            stack.setItemMeta(meta);
        }
        return stack;
    }
    ItemStack createBaseStack();
    default ItemStack createStack(int amount){
        ItemStack stack = markCustom(createBaseStack());
        stack.setAmount(amount);
        return stack;
    }
    String key();
}
