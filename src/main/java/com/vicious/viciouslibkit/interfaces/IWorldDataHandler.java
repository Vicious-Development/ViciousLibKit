package com.vicious.viciouslibkit.interfaces;

import com.vicious.viciouslibkit.block.blockinstance.BlockInstance;
import com.vicious.viciouslibkit.data.WorldDataDirectory;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.File;
import java.nio.file.Path;

public interface IWorldDataHandler extends IDataHandler{
    void load(World w);
    void unload(World w);
    void save(World w);
    void handleBlockChange(Block b);
    void handleBlockChange(BlockInstance bi, Block b);
    default File[] getFiles(World w){
        Path p = WorldDataDirectory.getWorldDirForDataDoNotCreate(w, getDataID());
        File dir = new File(p.toAbsolutePath().toString());
        if(!dir.exists()) return new File[0];
        return dir.listFiles();
    }
}
