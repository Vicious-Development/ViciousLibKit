package com.vicious.viciouslibkit.data.worldstorage;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import com.vicious.viciouslibkit.block.blockinstance.BlockInstance;
import com.vicious.viciouslibkit.data.provided.multiblock.MultiBlockInstance;
import com.vicious.viciouslibkit.data.provided.multiblock.MultiBlockChunkDataHandler;
import com.vicious.viciouslibkit.interfaces.IChunkDataHandler;
import com.vicious.viciouslibkit.services.multiblock.MultiBlockService;
import com.vicious.viciouslibkit.services.multiblock.MultiBlockState;
import com.vicious.viciouslibkit.util.ChunkPos;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PluginWorldData {
    //Global
    private static final Map<UUID,PluginWorldData> map = new HashMap<>();
    public static PluginWorldData getWorldData(World w){
        ensureExists(w);
        return map.get(w.getUID());
    }
    public static PluginWorldData getWorldData(UUID id){
        ensureExists(id);
        return map.get(id);
    }
    public static PluginChunkData getChunkData(World w, Location l){
        return getWorldData(w).getChunkData(ChunkPos.fromBlockPos(l));
    }
    public static PluginChunkData getChunkData(World w, ChunkPos c){
        return getWorldData(w).getChunkData(c);
    }
    public static <T extends IChunkDataHandler> T getChunkDataHandler(World w, ChunkPos c, Class<T> handlerClass){
        return getWorldData(w).getChunkData(c).getDataHandlerExceptionless(handlerClass);
    }
    public static void ensureExists(World w){
        if(!map.containsKey(w.getUID())) map.put(w.getUID(),new PluginWorldData(w.getUID()));
    }
    public static void ensureExists(UUID id){
        if(!map.containsKey(id)) map.put(id,new PluginWorldData(id));
    }

    public static void loadChunk(Chunk chunk) {
        PluginWorldData data = getWorldData(chunk.getWorld());
        data.initChunk(chunk);
    }

    public static void unloadChunk(Chunk chunk) {
        PluginWorldData data = getWorldData(chunk.getWorld());
        data.removeChunk(chunk);
    }
    public static MultiBlockState createMultiblockOn(Block b){
        List<Class<? extends MultiBlockInstance>> mbiClasses = MultiBlockService.multiblockBuilders.get(b.getType());
        MultiBlockState best = new MultiBlockState(MultiBlockState.State.INVALID,null,-1);
        if(mbiClasses == null) return best;
        for (Class<? extends MultiBlockInstance> cls : mbiClasses) {
            best = MultiBlockInstance.bestCase(best, PluginWorldData.createMultiblock(cls, b.getWorld(), b.getLocation()));
            if (best.isValid()) return best;
        }
        return best;
    }
    public static <T extends MultiBlockInstance> MultiBlockState createMultiblock(Class<T> cls, World w, Location l){
        SQLVector3i v = new SQLVector3i(l.getBlockX(), l.getBlockY(), l.getBlockZ());
        ChunkPos cpos = ChunkPos.fromBlockPos(v);
        PluginWorldData wdat = getWorldData(w.getUID());
        PluginChunkData cdat = wdat.getChunkData(cpos);
        MultiBlockChunkDataHandler dh = cdat.getDataHandlerExceptionless(MultiBlockChunkDataHandler.class);
        if(dh.checkExists(v)) return new MultiBlockState(MultiBlockState.State.VALID," Already exists.",0);
        MultiBlockState isValid = MultiBlockInstance.checkValid(w,l,cls);
        if(!isValid.isValid()) return isValid;
        dh.addMultiblock(MultiBlockService.registry.get(cls).construct(cls,w,l,isValid.orientation,isValid.flipped,UUID.randomUUID()));
        return isValid;
    }

    public static void handleBlockChange(Block b) {
        World w = b.getWorld();
        UUID u = w.getUID();
        PluginWorldData wd = getWorldData(u);
        PluginChunkData cd = wd.getChunkData(ChunkPos.fromBlockPos(b.getLocation()));
        cd.handleBlockChange(b);
    }
    public static void handleBlockChange(BlockInstance bi, Block b) {
        World w = b.getWorld();
        UUID u = w.getUID();
        PluginWorldData wd = getWorldData(u);
        PluginChunkData cd = wd.getChunkData(ChunkPos.fromBlockPos(b.getLocation()));
        cd.handleBlockChange(bi,b);
    }

    //Instance
    public Map<ChunkPos,PluginChunkData> chunkMap = new HashMap<>();
    public final UUID WORLDID;
    public PluginWorldData(UUID worldid){
        WORLDID=worldid;
        map.put(worldid,this);
    }

    public void initChunk(Chunk c){
        ChunkPos p = new ChunkPos(c.getX(),c.getZ());
        if(chunkMap.putIfAbsent(p,new PluginChunkData()) == null){
            chunkMap.get(p).load(c);
        }
    }
    public void removeChunk(Chunk c){
        PluginChunkData data = chunkMap.remove(new ChunkPos(c.getX(),c.getZ()));
        data.unload(c);
    }
    public void saveChunkData(PluginChunkData data, Chunk c){
        data.save(c);
    }

    public PluginChunkData getChunkData(ChunkPos pos) {
        chunkMap.putIfAbsent(pos,new PluginChunkData());
        return chunkMap.get(pos);
    }
}
