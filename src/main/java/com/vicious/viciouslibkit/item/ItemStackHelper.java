package com.vicious.viciouslibkit.item;

import org.bukkit.inventory.ItemStack;

public class ItemStackHelper {
    /**
     * Transfers as much as possible from 'from' and places it in 'to'
     */
    public static void transferTo(ItemStack from, ItemStack to){
        int maxMoved = to.getMaxStackSize()-to.getAmount();
        if(from.getAmount() < maxMoved){
            addTo(to, from.getAmount());
            from.setAmount(0);
        }
        else{
            max(to);
            addTo(from,-maxMoved);
        }
    }
    public static void addTo(ItemStack toAdd, int i){
        toAdd.setAmount(toAdd.getAmount()+i);
    }
    public static void max(ItemStack s){
        s.setAmount(s.getMaxStackSize());
    }
}
