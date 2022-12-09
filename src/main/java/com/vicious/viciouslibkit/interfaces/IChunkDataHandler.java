package com.vicious.viciouslibkit.interfaces;

import com.vicious.viciouslibkit.block.blockinstance.BlockInstance;
import com.vicious.viciouslibkit.data.WorldDataDirectory;
import com.vicious.viciouslibkit.util.vector.ChunkPos;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

import java.io.File;
import java.nio.file.Path;

public interface IChunkDataHandler extends IDataHandler{
    void load(Chunk c);
    void unload(Chunk c);
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
}
