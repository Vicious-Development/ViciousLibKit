package com.vicious.viciouslibkit.services.multiblock;

import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslibkit.block.BlockTemplate;
import com.vicious.viciouslibkit.block.blockinstance.BlockInstance;
import com.vicious.viciouslibkit.data.WorldDataDirectory;
import com.vicious.viciouslibkit.data.provided.multiblock.MultiBlockInstance;
import com.vicious.viciouslibkit.data.worldstorage.PluginWorldData;
import com.vicious.viciouslibkit.util.map.BiMap;
import com.vicious.viciouslibkit.util.vector.ChunkPos;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.nio.file.Path;
import java.util.*;

public class MultiBlockService {
    public static final String DATAID = "multiblock";

    public static BiMap<String,Class<? extends MultiBlockInstance>> idMap = new BiMap<>();
    public static Map<Class<? extends MultiBlockInstance>,MBConstructor<?>> registry = new HashMap<>();
    public static Map<Class<? extends MultiBlockInstance>,MBReconstructor<?>> reconstructors = new HashMap<>();
    public static Map<Material, List<Class<? extends MultiBlockInstance>>> multiblockBuilders = new EnumMap<>(Material.class);

    public static void handleBlockChange(Block b) {
        PluginWorldData.handleBlockChange(b);
    }
    public static void handleBlockChange(BlockInstance blockInstance, Block b) {
        PluginWorldData.handleBlockChange(blockInstance,b);
    }
    public static void registerClickListener(Material mat, Class<? extends MultiBlockInstance> mbic){
        if(!multiblockBuilders.containsKey(mat)) multiblockBuilders.put(mat,new ArrayList<>());
        multiblockBuilders.get(mat).add(mbic);
    }

    public static <T extends MultiBlockInstance> void registerMultiblock(Class<T> cls, String mbID, BlockTemplate template, MBConstructor<T> constructor, MBReconstructor<T> reconstructor)  {
        MultiBlockInstance.templates.put(cls,template);
        idMap.put(mbID,cls);
        registry.put(cls,constructor);
        reconstructors.put(cls,reconstructor);

    }
    public static Path getMBPath(World w, Location l, Class<? extends MultiBlockInstance> mbic, UUID id) {
        return FileUtil.toPath(WorldDataDirectory.getChunkDirForData(w,ChunkPos.fromBlockPos(l), DATAID).toAbsolutePath() + "/" + idMap.getK(mbic) + "_" + id + ".json");
    }
    public static Path getMBPath(World w, ChunkPos c, Class<? extends MultiBlockInstance> mbic, UUID id) {
        return FileUtil.toPath(WorldDataDirectory.getChunkDirForData(w,c, DATAID).toAbsolutePath() + "/" + idMap.getK(mbic) + "_" + id + ".json");
    }
}

