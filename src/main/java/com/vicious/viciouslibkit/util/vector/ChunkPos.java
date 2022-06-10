package com.vicious.viciouslibkit.util.vector;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.Objects;

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
    public static ChunkPos fromBlockPos(Vector l) {
        return new ChunkPos(l.getBlockX() >> 4, l.getBlockZ() >> 4);
    }
    public static ChunkPos fromChunk(Chunk c) {
        return new ChunkPos(c.getX(), c.getZ());
    }

    public ChunkPos add(int x, int z){
        return new ChunkPos(this.x+x,this.z+z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(z, x);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChunkPos chunkPos = (ChunkPos) o;
        return z == chunkPos.z && x == chunkPos.x;
    }

    @Override
    public String toString() {
        return "ChunkPos(" + x + "," + z + ')';
    }
}
