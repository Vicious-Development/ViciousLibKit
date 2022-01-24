package com.vicious.viciouslibkit.multiblock;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import com.vicious.viciouslibkit.Viciouslibkit;
import com.vicious.viciouslibkit.block.BlockTemplate;
import com.vicious.viciouslibkit.util.ChunkPos;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MultiBlockWorldData {
    private World world;
    private Map<SQLVector3i,MultiBlockInstance> multiblocks = new HashMap<>();
    private Map<ChunkPos, List<MultiBlockBoundingBox>> boundingBoxes = new HashMap<>();
    public MultiBlockWorldData(World w){
        this.world=w;
    }
    /**
     * Adds a multiblock bounding box the the chunkpos.
     */
    public void addBoundingBox(MultiBlockInstance mbi){
        SQLVector3i v = mbi.xyz.value();
        BlockTemplate template = mbi.getTemplate();
        MultiBlockBoundingBox box = new MultiBlockBoundingBox(v.x,v.y,v.z,template.maxX, template.maxY, template.maxZ);
        mbi.setBox(box);
        cycleChunks(box,(l)->l.add(box));
    }
    public void checkBlockUpdate(Block b){
        Location l = b.getLocation();
        ChunkPos cpos = ChunkPos.fromBlockPos(l);
        List<MultiBlockBoundingBox> boxes = boundingBoxes.get(cpos);
        if(boxes == null) return;
        for (MultiBlockBoundingBox box : boxes) {
            if(box.isWithin(l)){
                box.mbi.revalidate(b, this);
            }
        }
    }

    public void removeMultiBlock(MultiBlockInstance mbi)  {
        multiblocks.remove(mbi.xyz.value());
        removeBox(mbi.box);
        try {
            Files.deleteIfExists(mbi.PATH);
        } catch(IOException e){
            Viciouslibkit.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }

    private void removeBox(MultiBlockBoundingBox box) {
        cycleChunks(box,(l)->l.remove(box));
    }
    private void cycleChunks(MultiBlockBoundingBox box, Consumer<List<MultiBlockBoundingBox>> consumer){
        int x1 = box.x1 >> 4;
        int z1 = box.z1 >> 4;
        int x2 = box.x2 >> 4;
        int z2 = box.z2 >> 4;
        ChunkPos pos = new ChunkPos(x1,z1);
        while(pos.x < x2){
            while(pos.z < z2) {
                if (!boundingBoxes.containsKey(pos)) boundingBoxes.put(pos, new ArrayList<>());
                consumer.accept(boundingBoxes.get(pos));
                pos = pos.add(0, 1);
            }
            pos = new ChunkPos(pos.x+1,z1);
        }
    }

    public void addMultiBlock(MultiBlockInstance mbi) {
        multiblocks.put(mbi.xyz.value(), mbi);
        addBoundingBox(mbi);
    }
}
