package com.vicious.viciouslibkit.inventory;

import com.vicious.viciouslibkit.item.ItemStackHelper;
import com.vicious.viciouslibkit.util.map.ItemStackMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryHelper {
    public static ItemStackMap toStackMap(Inventory i){
        ItemStackMap ret = new ItemStackMap();
        for (ItemStack s : i.getStorageContents()) {
            ret.add(s);
        }
        return ret;
    }
    public static void moveFrom(Inventory target, List<ItemStack> stacks){
        int lastStacksSize = 0;
        while(!stacks.isEmpty() && lastStacksSize != stacks.size()) {
            lastStacksSize = stacks.size();
            ItemStack toInsert = stacks.get(0);
            for (int i = 0; i < target.getSize(); i++) {
                ItemStack slotStack = target.getItem(i);
                if(slotStack == null){
                    target.setItem(i,toInsert);
                    stacks.remove(0);
                    break;
                }
                else if(slotStack.isSimilar(toInsert)){
                    ItemStackHelper.transferTo(toInsert,slotStack);
                    if(toInsert.getAmount() <= 0){
                        stacks.remove(0);
                        break;
                    }
                }
            }
        }
    }
}
