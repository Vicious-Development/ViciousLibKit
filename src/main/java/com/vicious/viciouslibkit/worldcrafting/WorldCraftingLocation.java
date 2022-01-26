package com.vicious.viciouslibkit.worldcrafting;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WorldCraftingLocation that = (WorldCraftingLocation) o;
        return x == that.x && y == that.y && z == that.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
