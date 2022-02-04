package com.vicious.viciouslibkit.data;

import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslibkit.data.worldstorage.PluginChunkData;
import com.vicious.viciouslibkit.util.ChunkPos;
import com.vicious.viciouslibkit.util.LibKitDirectories;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

/**
 * Manages all worldstorage dependent and persistent data for plugins.
 * Data will be stored to the worldstorage folder that matches the level-name property in server-properties.
 * This ensures that the data is deleted when the worldstorage is deleted.
 */
public class WorldDataDirectory {
    public Map<UUID, Map<ChunkPos, PluginChunkData>> map = new HashMap<>();
    public static File[] getFilesOfTypeInChunk(World w, ChunkPos p, String dataId){
        File dir = new File(getChunkDirForData(w,p,dataId).toAbsolutePath().toString());
        return dir.listFiles();
    }
    public static Path getChunkDirForData(World w, Location l, String dataId){
        return FileUtil.createDirectoryIfDNE(getChunkdir(w,ChunkPos.fromBlockPos(l)).toAbsolutePath().toString() + "/" + dataId);
    }
    public static Path getChunkDirForData(World w, ChunkPos p, String dataId){
        return FileUtil.createDirectoryIfDNE(getChunkdir(w,p).toAbsolutePath().toString() + "/" + dataId);
    }
    public static Path getChunkdir(World w, ChunkPos p){
        return FileUtil.createDirectoryIfDNE(getWorldViciousDir(w).toAbsolutePath().toString() + "/" + p.x + "_" + p.z);
    }
    public static Path getWorldViciousDir(World w){
        return FileUtil.createDirectoryIfDNE(getWorldDir(w).toAbsolutePath().toString() + "/" + "vicious");
    }
    public static Path getWorldDir(World w){
        return FileUtil.createDirectoryIfDNE(LibKitDirectories.rootDir() + "/" + w.getName());
    }
}
