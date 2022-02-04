package com.vicious.viciouslibkit.worldcrafting;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import com.vicious.viciouslibkit.util.LibKitUtil;
import com.vicious.viciouslibkit.util.map.ItemStackMap;
import com.vicious.viciouslibkit.util.map.LocationItemMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Hopper;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InWorldItemCrafting {
    public static Map<World, LocationItemMap> inWorldCraftingItems = new HashMap<>();
    public static ItemStackMap getInWorldCraftingAt(Location l) {
        verifyExistence(l);
        return inWorldCraftingItems.get(l.getWorld()).get(new SQLVector3i(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
    }

    public static void emptyLocation(World w,Location l) {
        ItemStackMap resultMap = getInWorldCraftingAt(l);
        if(resultMap == null) return;
        spawnResultItem(resultMap.getStacks(),w,l);
    }
    private static void spawnResultItem(List<ItemStack> result, World w, Location l) {
        Block b = w.getBlockAt(l.add(0,-2,0));
        if(b.getType() == Material.HOPPER){
            Hopper hopper = (Hopper) b.getState();
            Map<Integer,ItemStack> remainder = hopper.getInventory().addItem(result.toArray(new ItemStack[0]));
            for (ItemStack re : remainder.values()) {
                dropSpawn(re,w,l);
            }
        }
        else {
            for (ItemStack itemStack : result) {
                dropSpawn(itemStack,w,l);
            }
        }
    }
    public static boolean verifyExistence(Location l){
        if(inWorldCraftingItems.containsKey(l.getWorld())) {
            return true;
        }
        inWorldCraftingItems.put(l.getWorld(), new LocationItemMap());
        return false;
    }
    private static void dropSpawn(ItemStack result, World w, Location l){
        Item item = w.dropItem(l, result);
        item.setVelocity(LibKitUtil.zeroVelocity());
    }
}
