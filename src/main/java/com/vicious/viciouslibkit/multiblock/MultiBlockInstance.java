package com.vicious.viciouslibkit.multiblock;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import com.vicious.viciouslib.database.tracking.JSONTrackable;
import com.vicious.viciouslib.database.tracking.values.TrackableEnum;
import com.vicious.viciouslib.database.tracking.values.TrackableObject;
import com.vicious.viciouslib.database.tracking.values.TrackableValue;
import com.vicious.viciouslibkit.block.BlockInstance;
import com.vicious.viciouslibkit.block.BlockTemplate;
import com.vicious.viciouslibkit.util.ChunkPos;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class MultiBlockInstance extends JSONTrackable<MultiBlockInstance> {
    static{
        TrackableObject.jsonparsers.put(World.class,(j,t)-> Bukkit.getWorld(UUID.fromString(j.getString(t.name))));
        TrackableValue.universalConverters.put(World.class,(w)-> ((World)w).getUID().toString());
    }
    public static Map<Class<?>, Function<Block,MultiBlockState>> validators = new HashMap<>();
    public static Map<Class<?>, BlockTemplate> templates = new HashMap<>();
    public Class<?> type;
    public TrackableObject<World> world = add(new TrackableObject<>("w",()->null,this));
    //Same as a vector3i just with SQL support i guess. We're not using the sql part so whatever.
    public TrackableObject<SQLVector3i> xyz = add(new TrackableObject<>("p",()->new SQLVector3i(0,0,0),this));
    public TrackableEnum<BlockFace> facing = add(new TrackableEnum<>("f",()->BlockFace.NORTH,this));
    public MultiBlockBoundingBox box;
    private BlockTemplate validTemplate;
    private final UUID id;

    public MultiBlockInstance(Class<?> mbType, World w, Location l, BlockFace dir, UUID id) {
        super(MultiBlockHandler.getChunkDir(ChunkPos.fromBlockPos(l)).toAbsolutePath() + "/" + mbType.getCanonicalName() + "_" + id + ".json");
        type = mbType;
        world.setWithoutUpdate(w);
        xyz.setWithoutUpdate(new SQLVector3i(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
        facing.setWithoutUpdate(dir);
        validTemplate=templates.get(type).rotate(dir);
        this.id=id;
    }
    public MultiBlockInstance(Class<?> type, UUID id, ChunkPos cpos){
        super(MultiBlockHandler.getChunkDir(cpos).toAbsolutePath() + "/" + type.getCanonicalName() + "_" + id + ".json");
        this.type=type;
        this.id = id;
    }
    public MultiBlockInstance setBox(MultiBlockBoundingBox box) {
        this.box = box;
        box.mbi = this;
        return this;
    }
    public void revalidate(Block b, MultiBlockWorldData dat) {
        if(!box.revalidateAtPos(b,validTemplate)){
            invalidate(dat);
        }
    }

    protected void invalidate(MultiBlockWorldData dat) {
        dat.removeMultiBlock(this);
    }

    /**  N      E      S      W
     * p      b a p  k j b      k
     * a c    j c      c a    c j
     * b j k  k          p  p a b
     *
     */
    public static MultiBlockState validate(World w, Location startPos, Class<?> type){
        BlockTemplate blocks = templates.get(type);
        //NORTH ORIENTATION
        startPos = startPos.clone().add(-blocks.startX,-blocks.startY,-blocks.startZ);
        BlockFace orientation = BlockFace.NORTH;
        MultiBlockState state = validate(blocks,w,startPos);
        if(state.facing(orientation).isValid()) return state;
        //EAST ORIENTATION
        startPos = startPos.clone().add(blocks.startX,-blocks.startY,-blocks.startZ);
        orientation = BlockFace.EAST;
        state = bestCase(state, validate(blocks.rotate(orientation),w,startPos));
        if(state.facing(orientation).isValid()) return state;
        //SOUTH ORIENTATION
        startPos = startPos.clone().add(blocks.startX,-blocks.startY,blocks.startZ);
        orientation = BlockFace.SOUTH;
        state = bestCase(state, validate(blocks.rotate(orientation),w,startPos));
        if(state.facing(orientation).isValid()) return state;
        //WEST ORIENTATION
        startPos = startPos.clone().add(-blocks.startX,-blocks.startY,blocks.startZ);
        orientation = BlockFace.WEST;
        state = bestCase(state, validate(blocks.rotate(orientation),w,startPos));
        return state.facing(orientation);
    }
    private static MultiBlockState bestCase(MultiBlockState state1, MultiBlockState state2){
        if(state1.count > state2.count) return state1;
        return state2;
    }
    private static MultiBlockState validate(BlockTemplate blocks, World w, Location startPos){
        long count = 0;
        for (int x = 0; x < blocks.maxX; x++) {
            for (int y = 0; y < blocks.maxY; y++) {
                for (int z = 0; z < blocks.maxZ; z++) {
                    Location l = startPos.clone().add(x,y,z);
                    BlockInstance instance = blocks.get(x,y,z);
                    if(instance == null);
                    else if(!instance.matches(w.getBlockAt(l))) return new MultiBlockState(MultiBlockState.State.INVALID,new BlockInstance(w.getBlockAt(l)).verboseInfo() + " at (" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + ") is invalid, expected: " + instance.verboseInfo(),count);
                    count++;
                }
            }
        }
        return new MultiBlockState(MultiBlockState.State.VALID,null,count);
    }
    public BlockTemplate getTemplate() {
        return templates.get(this.type);
    }
}
