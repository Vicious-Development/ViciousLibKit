package com.vicious.viciouslibkit.util.map;

import com.vicious.viciouslibkit.worldcrafting.WorldCraftingLocation;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class LocationItemMap extends HashMap<WorldCraftingLocation,ItemStackMap> {
    /**
     * @param stack the stack to add.
     * @return if the location was already present.
     */
    public boolean add(WorldCraftingLocation l, ItemStack stack){
        boolean existed = verifyExistence(l);
        get(l).add(stack);
        return existed;
    }
    public boolean add(WorldCraftingLocation l, ItemStackMap stacks){
        boolean existed = verifyExistence(l);
        get(l).combine(stacks);
        return existed;
    }

    public ItemStackMap get(WorldCraftingLocation key) {
        verifyExistence(key);
        return super.get(key);
    }

    private boolean verifyExistence(WorldCraftingLocation l) {
        if(containsKey(l)) {
            System.out.println("EXISTS: " + l);
            return true;
        }
        put(l,new ItemStackMap());
        return false;
    }
}
