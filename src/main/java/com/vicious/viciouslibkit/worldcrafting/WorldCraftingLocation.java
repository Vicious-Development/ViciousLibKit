package com.vicious.viciouslibkit.worldcrafting;

import org.bukkit.Location;
import org.bukkit.World;

public class WorldCraftingLocation {
    private int x;
    private int y;
    private int z;
    public WorldCraftingLocation(int x, int y, int z){
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public WorldCraftingLocation(Location l) {
        this.x=l.getBlockX();
        this.y=l.getBlockX();
        this.z=l.getBlockX();
    }

    public static WorldCraftingLocation of(int x, int y, int z){
        return new WorldCraftingLocation(x,y,z);
    }
}
