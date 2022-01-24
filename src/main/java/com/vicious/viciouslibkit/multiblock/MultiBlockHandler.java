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
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MultiBlockHandler {
    private static Map<World,MultiBlockWorldData> multiblocks = new HashMap<>();

    public static void handleBlockEvent(BlockEvent ev) {
        Block b = ev.getBlock();
        multiblocks.get(b.getWorld()).checkBlockUpdate(b);
    }

    public static void registerMultiblock(Class<?> cls, BlockTemplate template)  {
        MultiBlockInstance.templates.put(cls,template);
    }
    public static MultiBlockState create(Class<?> cls, World w, Location l){
        SQLVector3i v = new SQLVector3i(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        MultiBlockState isValid = MultiBlockInstance.validate(w,l,cls);
        if(!isValid.isValid()) return isValid;
        addMultiblock(new MultiBlockInstance(cls,w,l, UUID.randomUUID()));
        return isValid;
    }

    private static void addMultiblock(MultiBlockInstance mbi) {
        World w = mbi.world.value();
        if(!multiblocks.containsKey(w)) multiblocks.put(w, new MultiBlockWorldData(w));
        MultiBlockWorldData dat = multiblocks.get(w);
        dat.addMultiBlock(mbi);
        save(mbi);
    }
    public static void load(Chunk c){
        ChunkPos cpos = new ChunkPos(c.getX(),c.getZ());
        Path p = getChunkDir(cpos);
        File dir = new File(p.toAbsolutePath().toString());
        for (File file : dir.listFiles()) {
          addMultiblock(new MultiBlockInstance(UUID.fromString(file.getName().replace(".json","")),cpos));
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

