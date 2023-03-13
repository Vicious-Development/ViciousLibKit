package com.vicious.viciouslibkit.data.provided.multiblock;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import com.vicious.viciouslibkit.ViciousLibKit;
import com.vicious.viciouslibkit.block.BlockTemplate;
import com.vicious.viciouslibkit.block.blockinstance.BlockInstance;
import com.vicious.viciouslibkit.data.DataTypeNotFoundException;
import com.vicious.viciouslibkit.data.worldstorage.PluginChunkData;
import com.vicious.viciouslibkit.data.worldstorage.PluginWorldData;
import com.vicious.viciouslibkit.interfaces.IChunkDataHandler;
import com.vicious.viciouslibkit.services.multiblock.MBReconstructor;
import com.vicious.viciouslibkit.services.multiblock.MultiBlockBoundingBox;
import com.vicious.viciouslibkit.services.multiblock.MultiBlockService;
import com.vicious.viciouslibkit.util.vector.ChunkPos;
import com.vicious.viciouslibkit.util.map.PositionMap;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class MultiBlockChunkDataHandler implements IChunkDataHandler {
    private final List<MultiBlockBoundingBox> boxesToRemove = new ArrayList<>();
    private final PositionMap<MultiBlockInstance> multiblocks = new PositionMap<>();
    private final List<MultiBlockBoundingBox> boundingBoxes = new ArrayList<>();
    @Override
    public void load(Chunk c) {
        ChunkPos cpos = new ChunkPos(c.getX(),c.getZ());
        for (File file : getFiles(c)) {
            int underscoreidx = file.getName().indexOf("_");
            String mbID = file.getName().substring(0, underscoreidx);
            Class<? extends MultiBlockInstance> cls = MultiBlockService.idMap.getV(mbID);
            if(cls == null) {
                ViciousLibKit.logger().warning("Multiblock type previously registered no longer exists. Multiblock ID:" + mbID);
                return;
            }
            MBReconstructor<?> constructor = MultiBlockService.reconstructors.get(cls);
            MultiBlockInstance mbi = constructor.construct(cls, c.getWorld(), UUID.fromString(file.getName().substring(underscoreidx + 1).replace(".json", "")), cpos);
            mbi.executeIfInitialized((x) ->{
                try {
                    if(!multiblocks.containsKey(mbi.xyz.value())) {
                        mbi.initValidTemplate();
                        addMultiblock(mbi);
                    }
                } catch (Exception e){
                    ViciousLibKit.logger().warning(e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    }

    @Override
    public void unload(Chunk c) {
        save(c);
        multiblocks.forEach((v,mbi)->{
            mbi.invalidate(this);
        });
        multiblocks.clear();
        boundingBoxes.clear();
    }

    @Override
    public void save(Chunk c) {
        multiblocks.forEach((v,mbi)->{
            mbi.save();
        });
    }

    @Override
    public void handleBlockChange(Block b) {
        checkBlockUpdate(b);
    }

    @Override
    public void handleBlockChange(BlockInstance bi, Block b) {
        checkBlockUpdate(bi,b);
    }

    public void addMultiblock(MultiBlockInstance mbi){
        if(checkExists(mbi.xyz.value())) return;
        multiblocks.put(mbi.xyz.value(), mbi);
        try {
            addBoundingBox(mbi);
        } catch (Exception e){
            ViciousLibKit.logger().warning(e.getMessage());
            e.printStackTrace();
        }
        mbi.validate();
        mbi.save();
    }
    public MultiBlockInstance getMultiblock(SQLVector3i pos){
        return multiblocks.get(pos);
    }
    public boolean checkExists(SQLVector3i l) {
        return multiblocks.containsKey(l);
    }
    public void addBoundingBox(MultiBlockInstance mbi){
        updateBoxes();
        SQLVector3i v = mbi.xyz.value();
        BlockTemplate template = mbi.getTemplate();
        Vector ot = template.getOriginTranslation();
        //North-West-Bottom corner
        v = new SQLVector3i(v.x+ot.getBlockX(),v.y+ot.getBlockY(),v.z+ot.getBlockZ());
        //North-West-Bottom to South-East-Top corners.
        MultiBlockBoundingBox box = new MultiBlockBoundingBox(v.x,v.y,v.z,v.x+template.maxX, v.y+template.maxY, v.z+template.maxZ);
        mbi.setBox(box);
        cycleChunks(box,(l)->l.add(box));
    }
    private void cycleChunks(MultiBlockBoundingBox box, Consumer<List<MultiBlockBoundingBox>> consumer){
        int x1 = box.x1 >> 4;
        int z1 = box.z1 >> 4;
        int x2 = box.x2 >> 4;
        int z2 = box.z2 >> 4;
        ChunkPos pos = new ChunkPos(x1,z1);
        while(pos.x <= x2){
            while(pos.z <= z2) {
                PluginWorldData data = PluginWorldData.getWorldData(box.mbi.world);
                PluginChunkData cdat = data.getChunkData(pos);
                try {
                    MultiBlockChunkDataHandler dataHandler = cdat.getDataHandler(MultiBlockChunkDataHandler.class);
                    consumer.accept(dataHandler.boundingBoxes);
                } catch (DataTypeNotFoundException ex){
                    ViciousLibKit.logger().severe("THE MULTIBLOCK DATA HANDLER HAS NOT BEEN REGISTERED!!!");
                    ex.printStackTrace();
                }
                pos = pos.add(0, 1);
            }
            pos = new ChunkPos(pos.x+1,z1);
        }
    }
    public void checkBlockUpdate(Block b){
        Location l = b.getLocation();
        //TODO: Further optimize by looping only once.
        updateBoxes();
        for (MultiBlockBoundingBox box : boundingBoxes) {
            if(box.isWithin(l)){
                box.mbi.revalidate(b, this);
            }
        }
    }

    private void updateBoxes() {
        //Avoid concurrent mods.
        for (MultiBlockBoundingBox box : boxesToRemove) {
            cycleChunks(box,(k)->k.remove(box));
        }
        boxesToRemove.clear();
    }

    public void checkBlockUpdate(BlockInstance bi, Block b) {
        Location l = b.getLocation();
        updateBoxes();
        //TODO: Further optimize by looping only once.
        for (MultiBlockBoundingBox box : boundingBoxes) {
            if(box.isWithin(l)){
                box.mbi.revalidate(b,bi, this);
            }
        }
    }
    public void removeMultiBlock(MultiBlockInstance mbi)  {
        multiblocks.remove(mbi.xyz.value());
        removeBox(mbi.box);
        try {
            Files.deleteIfExists(mbi.PATH);
        } catch(IOException e){
            ViciousLibKit.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }
    private void removeBox(MultiBlockBoundingBox box) {
        boxesToRemove.add(box);
    }

    @Override
    public Plugin getPlugin() {
        return ViciousLibKit.INSTANCE;
    }
}
