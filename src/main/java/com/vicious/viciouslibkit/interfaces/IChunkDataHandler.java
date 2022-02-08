package com.vicious.viciouslibkit.interfaces;

import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslibkit.block.BlockInstance;
import com.vicious.viciouslibkit.data.WorldDataDirectory;
import com.vicious.viciouslibkit.util.ChunkPos;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

public interface IChunkDataHandler {
    void load(Chunk c);
    void save(Chunk c);
    void handleBlockChange(Block b);
    void handleBlockChange(BlockInstance bi, Block b);
    default File[] getFiles(Chunk c){
        ChunkPos cpos = new ChunkPos(c.getX(),c.getZ());
        Path p = WorldDataDirectory.getChunkDirForDataDoNotCreate(c.getWorld(),cpos, getDataID());
        File dir = new File(p.toAbsolutePath().toString());
        if(!dir.exists()) return new File[0];
        return dir.listFiles();
    }

    String getDataID();
}
