package com.vicious.viciouslibkit.inventory.optimization;

import com.vicious.viciouslibkit.block.BlockMaterial;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * An optimized inventory searching system that saves the last slot of a found material and checks that first before scanning the entire inventory again.
 */
public class InventorySearcher {
    private IdentityHashMap<Predicate<ItemStack>,Integer> lastSlotMap = new IdentityHashMap<>();
    private Map<Object,Predicate<ItemStack>> predicatorMap = new HashMap<>();
    private int scanFor(Inventory inventory, Predicate<ItemStack> predicate){
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i++) {
            ItemStack stack = contents[i];
            if(predicate.test(stack)){
                if(lastSlotMap.putIfAbsent(predicate,i) != null){
                    lastSlotMap.replace(predicate,i);
                }
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds a slot with a tool within an inventory capable of breaking the provided block.
     */
    public int findItemSlotThatCanBreak(Inventory inventory, Block block){
        BlockMaterial toFind = new BlockMaterial(block.getType());
        Predicate<ItemStack> pred = predicatorMap.computeIfAbsent(toFind, k -> (s) -> !block.getDrops(s).isEmpty());
        return findItemSlot(inventory,pred);
    }
    public ItemStack getItemStackThatCanBreak(Inventory inventory, Block block){
        int slot = findItemSlotThatCanBreak(inventory,block);
        if(slot == -1) return null;
        return inventory.getItem(slot);
    }

    /**
     * Finds a slot with an item matching the predicate's parameters.
     */
    public int findItemSlot(Inventory inventory, Predicate<ItemStack> pred){
        Integer expectedSlot = lastSlotMap.get(pred);
        if(expectedSlot == null){
            return scanFor(inventory, pred);
        }
        ItemStack stack = inventory.getItem(expectedSlot);
        if(stack == null || !pred.test(stack)) return scanFor(inventory, pred);
        return expectedSlot;
    }

    /**
     * Finds a slot with an item matching the material.
     */
    public int findItemSlot(Inventory inventory, Material toFind){
        Predicate<ItemStack> pred = predicatorMap.computeIfAbsent(toFind, k -> (s) -> s.getType() == toFind);
        return findItemSlot(inventory,pred);
    }

    /**
     * Returns an itemstack matching the material.
     */
    public ItemStack getItemStack(Inventory inventory, Material toFind){
        int slot = findItemSlot(inventory,toFind);
        if(slot == -1) return null;
        return inventory.getItem(slot);
    }
}
