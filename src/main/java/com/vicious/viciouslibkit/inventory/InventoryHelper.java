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
    public static int extract(ItemStack stack, Inventory target, boolean ignoreNBT){
        ItemStack[] contents = target.getContents();
        int count = stack.getAmount();
        if(ignoreNBT) {
            for (int i = 0; i < contents.length; i++) {
                if (count <= 0) break;
                if (contents[i] == null) continue;
                if (contents[i].getType() == stack.getType()) {
                    int fcount = count - contents[i].getAmount();
                    contents[i].setAmount(Math.max(0, contents[i].getAmount() - count));
                    count = fcount;
                }
            }
        }
        else{
            for (int i = 0; i < contents.length; i++) {
                if (count <= 0) break;
                if (contents[i] == null) continue;
                if (contents[i].isSimilar(stack)) {
                    int fcount = count - contents[i].getAmount();
                    contents[i].setAmount(Math.max(0, contents[i].getAmount() - count));
                    count = fcount;
                }
            }
        }
        return count;
    }
}
