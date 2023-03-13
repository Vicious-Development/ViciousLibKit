package com.vicious.viciouslibkit.interfaces;

import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslibkit.util.LibKitDirectories;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

public interface IDataHandler {
    Plugin getPlugin();
    default String getDataID(){
        return getPlugin().getName() + getClass().getSimpleName();
    }
    default String chunkPath(Chunk c){
        return FileUtil.toPath(viciousWorldPath(c) + "/" + c.getX() + "_" + c.getZ()).toAbsolutePath().toString();
    }
    default String viciousWorldPath(Chunk c){
        return viciousWorldPath(c.getWorld());
    }
    default String viciousWorldPath(World w){
        return FileUtil.createDirectoryIfDNE( worldPath(w)+ "/vicious").toAbsolutePath().toString();
    }
    default String worldPath(World w){
        return FileUtil.toPath(LibKitDirectories.rootDir() + "/" + w.getName()).toAbsolutePath().toString();
    }
}
