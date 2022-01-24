package com.vicious.viciouslibkit.multiblock;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslibkit.block.BlockTemplate;
import com.vicious.viciouslibkit.util.ChunkPos;
import com.vicious.viciouslibkit.util.LibKitDirectories;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockEvent;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MultiBlockHandler {
    private static Map<World,MultiBlockWorldData> multiblocks = new HashMap<>();
    private static Map<Class<? extends MultiBlockInstance>,MBConstructor<?>> registry = new HashMap<>();
    private static Map<Class<? extends MultiBlockInstance>,MBReconstructor<?>> reconstructors = new HashMap<>();

    public static void handleBlockEvent(BlockEvent ev) {
        Block b = ev.getBlock();
        multiblocks.get(b.getWorld()).checkBlockUpdate(b);
    }

    public static <T extends MultiBlockInstance> void registerMultiblock(Class<T> cls, BlockTemplate template, MBConstructor<T> constructor, MBReconstructor<T> reconstructor)  {
        MultiBlockInstance.templates.put(cls,template);
        registry.put(cls,constructor);
        reconstructors.put(cls,reconstructor);
    }
    public static <T extends MultiBlockInstance> MultiBlockState create(Class<T> cls, World w, Location l){
        SQLVector3i v = new SQLVector3i(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        MultiBlockState isValid = MultiBlockInstance.validate(w,l,cls);
        if(!isValid.isValid()) return isValid;
        addMultiblock(registry.get(cls).construct(cls,w,l,isValid.orientation,UUID.randomUUID()));
        return isValid;
    }

    private static void addMultiblock(MultiBlockInstance mbi) {
        World w = mbi.world.value();
        if(!multiblocks.containsKey(w)) multiblocks.put(w, new MultiBlockWorldData(w));
        MultiBlockWorldData dat = multiblocks.get(w);
        dat.addMultiBlock(mbi);
        save(mbi);
    }
    public static void load(Chunk c) throws ClassNotFoundException {
        ChunkPos cpos = new ChunkPos(c.getX(),c.getZ());
        if(!Files.exists(FileUtil.toPath(LibKitDirectories.multiblockDataDir + "/" + cpos.x + "_" + cpos.z))) return;
        Path p = getChunkDir(cpos);
        File dir = new File(p.toAbsolutePath().toString());
        for (File file : dir.listFiles()) {
            Class<?> cls = Class.forName(file.getName().substring(0,file.getName().indexOf("_")));
            MBReconstructor<?> constructor = reconstructors.get(cls);
            addMultiblock(constructor.construct(cls,UUID.fromString(file.getName().replace(".json","")),cpos));
        }
    }
    public static void save(MultiBlockInstance mbi){
        ChunkPos cpos = ChunkPos.fromBlockPos(mbi.xyz.value());
        mbi.save();
    }
    public static Path getChunkDir(ChunkPos cpos){
        return FileUtil.createDirectoryIfDNE(LibKitDirectories.multiblockDataDir + "/" + cpos.x + "_" + cpos.z );
    }

}

