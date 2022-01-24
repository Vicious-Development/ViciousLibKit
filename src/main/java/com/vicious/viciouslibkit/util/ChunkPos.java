package com.vicious.viciouslibkit.util;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import org.bukkit.Location;

public class ChunkPos {
    public final int z;
    public final int x;

    public ChunkPos(int x, int z){
        this.x=x;
        this.z=z;
    }
    public static ChunkPos fromBlockPos(Location l){
        return new ChunkPos(l.getBlockX() >> 4, l.getBlockZ() >> 4);
    }

    public static ChunkPos fromBlockPos(SQLVector3i l) {
        return new ChunkPos(l.x >> 4, l.z >> 4);
    }

    public ChunkPos add(int x, int z){
        return new ChunkPos(this.x+x,this.z+z);
    }
}
