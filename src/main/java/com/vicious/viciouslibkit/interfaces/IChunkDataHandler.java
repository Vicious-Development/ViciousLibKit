package com.vicious.viciouslibkit.interfaces;

import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslibkit.block.blockinstance.BlockInstance;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

import java.io.File;

public interface IChunkDataHandler extends IDataHandler{
    void load(Chunk c);
    void unload(Chunk c);
    void save(Chunk c);
    void handleBlockChange(Block b);
    void handleBlockChange(BlockInstance bi, Block b);
    default File[] getFiles(Chunk c){
        File dir = new File(chunkDataPath(c));
        if(!dir.exists()) return new File[0];
        return dir.listFiles();
    }
    default String chunkDataPath(Chunk c){
        return FileUtil.toPath(chunkPath(c) + "/" + getDataID()).toAbsolutePath().toString();
    }

}
