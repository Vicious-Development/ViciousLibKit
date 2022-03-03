package com.vicious.viciouslibkit.inventory.wrapper;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import com.vicious.viciouslibkit.block.blockinstance.BlockInstance;
import com.vicious.viciouslibkit.interfaces.IChunkDataHandler;
import com.vicious.viciouslibkit.util.LibKitUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;

public class InventoryWrapperChunkHandler implements IChunkDataHandler {
    private static final String DATAID = "InventoryWrapper";
    private final InventoryPositionMap map = new InventoryPositionMap();
    @Override
    public void load(Chunk chunk) {}

    @Override
    public void unload(Chunk chunk) {
        map.forEach((k,v)->{
            v.unwrap();
        });
        map.clear();
    }
    public InventoryWrapper getOrCreateWrapper(Location l){
        World w = l.getWorld();
        SQLVector3i vec = LibKitUtil.fromLocation(l);
        return getOrCreateWrapper(w,vec);
    }
    public InventoryWrapper getOrCreateWrapper(World w, SQLVector3i vec){
        Block b = w.getBlockAt(vec.x, vec.y, vec.z);
        if(b.getState() instanceof Container){
            if(!map.containsKey(vec)) map.put(vec, new InventoryWrapper(((Container)b.getState()).getInventory()));
            return map.get(vec);
        }
        else return null;
    }
    public InventoryWrapper getWrapper(Location l){
        SQLVector3i vec = LibKitUtil.fromLocation(l);
        return getWrapper(vec);
    }

    public InventoryWrapper getWrapper(SQLVector3i vec) {
        return map.get(vec);
    }

    public void removeWrapper(SQLVector3i vec){
        map.remove(vec).unwrap();
    }


    @Override
    public void save(Chunk chunk) {
    }

    @Override
    public void handleBlockChange(Block block) {
    }

    @Override
    public void handleBlockChange(BlockInstance blockInstance, Block block) {
    }

    @Override
    public String getDataID() {
        return DATAID;
    }
}
