package com.vicious.viciouslibkit.util.map;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class LocationItemMap extends HashMap<SQLVector3i,ItemStackMap> {
    /**
     * @param stack the stack to add.
     * @return if the location was already present.
     */
    public boolean add(SQLVector3i l, ItemStack stack){
        boolean existed = verifyExistence(l);
        get(l).add(stack);
        return existed;
    }
    public boolean add(SQLVector3i l, ItemStackMap stacks){
        boolean existed = verifyExistence(l);
        get(l).combine(stacks);
        return existed;
    }

    public ItemStackMap get(SQLVector3i key) {
        verifyExistence(key);
        return super.get(key);
    }

    private boolean verifyExistence(SQLVector3i l) {
        if(containsKey(l)) {
            return true;
        }
        put(l,new ItemStackMap());
        return false;
    }
}
