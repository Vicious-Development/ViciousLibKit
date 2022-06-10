package com.vicious.viciouslibkit.services.netnode;

import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslibkit.ViciousLibKit;
import com.vicious.viciouslibkit.data.WorldDataDirectory;
import com.vicious.viciouslibkit.data.provided.netnode.NetChunkDataHandler;
import com.vicious.viciouslibkit.data.provided.netnode.NetNode;
import com.vicious.viciouslibkit.util.vector.ChunkPos;
import com.vicious.viciouslibkit.util.map.BiMap;
import com.vicious.viciouslibkit.util.map.DualKeyedMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NetNodeService {
    public static BiMap<String,Class<? extends NetNode>> idMap = new BiMap<>();
    public static Map<Class<? extends NetNode>, NNConstructor<? extends NetNode>> registry = new HashMap<>();
    public static Map<Class<? extends NetNode>, NNReconstructor<? extends NetNode>> reconstructors = new HashMap<>();
    public static DualKeyedMap<Material,Material, Class<? extends NetNode>> netnodeBuilders = new DualKeyedMap<>(()->new EnumMap<>(Material.class),()->new EnumMap<>(Material.class));
    public static void registerClickListener(Material item, Material block, Class<? extends NetNode> nnc, String pluginName){
        if(netnodeBuilders.putIfAbsent(item,block,nnc) != null){
            ViciousLibKit.logger().severe("A plugin attempted to register a NetNode builder that already existed. " +
                    "\nThis is BAD and will cause this plugin to function improperly: " + pluginName +
                    "\nFailed to register for Item:" + item + " and block " + block);
        }
    }

    public static <T extends NetNode> void registerNetNode(Class<T> cls, String nnID, NNConstructor<T> constructor, NNReconstructor<T> reconstructor)  {
        idMap.put(nnID,cls);
        registry.put(cls,constructor);
        reconstructors.put(cls,reconstructor);
    }
    public static Path getNNPath(World w, Location l, Class<? extends NetNode> nnc, UUID id) {
        return FileUtil.toPath(WorldDataDirectory.getChunkDirForData(w, ChunkPos.fromBlockPos(l), NetChunkDataHandler.DATAID).toAbsolutePath() + "/" + idMap.getK(nnc) + "_" + id + ".json");
    }
    public static Path getNNPath(World w, ChunkPos c, Class<? extends NetNode> nnc, UUID id) {
        return FileUtil.toPath(WorldDataDirectory.getChunkDirForData(w,c, NetChunkDataHandler.DATAID).toAbsolutePath() + "/" + idMap.getK(nnc) + "_" + id + ".json");
    }
}
