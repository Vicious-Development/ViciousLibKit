package com.vicious.viciouslibkit.multiblock;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslibkit.block.BlockInstance;
import com.vicious.viciouslibkit.block.BlockTemplate;
import com.vicious.viciouslibkit.util.ChunkPos;
import com.vicious.viciouslibkit.util.LibKitDirectories;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MultiBlockHandler {
    private static Map<UUID,MultiBlockWorldData> multiblocks = new HashMap<>();
    private static Map<Class<? extends MultiBlockInstance>,MBConstructor<?>> registry = new HashMap<>();
    private static Map<Class<? extends MultiBlockInstance>,MBReconstructor<?>> reconstructors = new HashMap<>();

    public static void handleBlockChange(Block b) {
        if(!multiblocks.containsKey(b.getWorld().getUID())) multiblocks.put(b.getWorld().getUID(),new MultiBlockWorldData(b.getWorld()));
        multiblocks.get(b.getWorld().getUID()).checkBlockUpdate(b);
    }

    public static <T extends MultiBlockInstance> void registerMultiblock(Class<T> cls, BlockTemplate template, MBConstructor<T> constructor, MBReconstructor<T> reconstructor)  {
        MultiBlockInstance.templates.put(cls,template);
        registry.put(cls,constructor);
        reconstructors.put(cls,reconstructor);
    }
    public static <T extends MultiBlockInstance> MultiBlockState create(Class<T> cls, World w, Location l){
        SQLVector3i v = new SQLVector3i(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        if(checkExists(w, v)) return new MultiBlockState(MultiBlockState.State.VALID," Already exists.",0);
        MultiBlockState isValid = MultiBlockInstance.checkValid(w,l,cls);
        if(!isValid.isValid()) return isValid;
        addMultiblock(registry.get(cls).construct(cls,w,l,isValid.orientation,isValid.flipped,UUID.randomUUID()));
        return isValid;
    }

    private static boolean checkExists(World w, SQLVector3i l) {
        if(!multiblocks.containsKey(w.getUID())) multiblocks.put(w.getUID(), new MultiBlockWorldData(w));
        MultiBlockWorldData dat = multiblocks.get(w.getUID());
        return dat.isAreaOccupied(l);
    }

    private static void addMultiblock(MultiBlockInstance mbi) {
        World w = mbi.world;
        if(checkExists(mbi.world,mbi.xyz.value())) return;
        MultiBlockWorldData dat = multiblocks.get(w.getUID());
        dat.addMultiBlock(mbi);
        save(mbi);
        System.out.println("FA: " + mbi.facing.value());
        System.out.println("FL: " + mbi.flipped.value());
        System.out.println("T: " + mbi.getTemplate().getOriginTranslation());
    }
    public static void load(Chunk c) throws ClassNotFoundException {
        ChunkPos cpos = new ChunkPos(c.getX(),c.getZ());
        if(!Files.exists(FileUtil.toPath(getWorldDir(c.getWorld()) + "/" + cpos.x + "_" + cpos.z))) return;
        Path p = getChunkDir(c.getWorld(),cpos);
        File dir = new File(p.toAbsolutePath().toString());
        for (File file : dir.listFiles()) {
            int underscoreidx = file.getName().indexOf("_");
            Class<?> cls = Class.forName(file.getName().substring(0,underscoreidx));
            MBReconstructor<?> constructor = reconstructors.get(cls);
            MultiBlockInstance mbi = constructor.construct(cls,c.getWorld(),UUID.fromString(file.getName().substring(underscoreidx+1).replace(".json","")),cpos);
            mbi.executeIfInitialized((x)->{
                addMultiblock(mbi);
            });
        }
    }
    public static void save(MultiBlockInstance mbi){
        mbi.save();
    }
    public static String getWorldDir(World w){
        return FileUtil.createDirectoryIfDNE(LibKitDirectories.multiblockDataDir + "/" + w.getUID()).toAbsolutePath().toString();
    }
    public static Path getChunkDir(World w, ChunkPos cpos){
        return FileUtil.createDirectoryIfDNE(getWorldDir(w) + "/" + cpos.x + "_" + cpos.z );
    }

    public static void handleBlockChange(BlockInstance blockInstance, Block b) {
        if(!multiblocks.containsKey(b.getWorld().getUID())) multiblocks.put(b.getWorld().getUID(),new MultiBlockWorldData(b.getWorld()));
        multiblocks.get(b.getWorld().getUID()).checkBlockUpdate(blockInstance,b);
    }
}

