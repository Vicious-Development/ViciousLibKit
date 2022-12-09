package com.vicious.viciouslibkit.data;

import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslibkit.data.worldstorage.PluginChunkData;
import com.vicious.viciouslibkit.util.LibKitDirectories;
import com.vicious.viciouslibkit.util.vector.ChunkPos;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    public static Path getChunkDirForDataDoNotCreate(World w, ChunkPos p, String dataId) {
        return FileUtil.toPath(getChunkdirDoNotCreate(w,p).toAbsolutePath().toString() + "/" + dataId);
    }

    private static Path getChunkdirDoNotCreate(World w, ChunkPos p) {
        return FileUtil.toPath(getWorldViciousDir(w).toAbsolutePath().toString() + "/" + p.x + "_" + p.z);
    }

    public static Path getWorldDirForDataDoNotCreate(World w, String dataID) {
        return FileUtil.toPath(getWorldViciousDir(w).toAbsolutePath().toString() + "/" + dataID);
    }
    public static Path getWorldDirForData(World w, String dataID) {
        return FileUtil.createDirectoryIfDNE(getWorldViciousDir(w).toAbsolutePath().toString() + "/" + dataID);
    }

    public static Path getDirForEntities(){
        return FileUtil.createDirectoryIfDNE(LibKitDirectories.worldPersistentDataDir + "/entities");
    }

    public static Path getDirForEntity(Entity e){
        return FileUtil.createDirectoryIfDNE(getDirForEntities().toAbsolutePath().toString() + "/" + e.getUniqueId());
    }
}
