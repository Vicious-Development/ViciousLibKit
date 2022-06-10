package com.vicious.viciouslibkit.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class ToolUtil {
    public static void damage(ItemStack stack, int damage){
        if(stack.getItemMeta() instanceof Damageable) {
            Damageable meta = (Damageable) stack.getItemMeta();
            meta.setDamage(meta.getDamage()-damage);
            stack.setItemMeta(meta);
        }
    }
    public static void damage(ItemStack stack){
        damage(stack,1);
    }

    public static boolean willNotBreak(ItemStack stack) {
        if(stack.getItemMeta() instanceof Damageable){
            return ((Damageable) stack.getItemMeta()).getDamage() > 0;
        }
        else return true;
    }
}
