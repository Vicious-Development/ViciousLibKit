package com.vicious.viciouslibkit.multiblock;

import com.vicious.viciouslib.LoggerWrapper;
import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import com.vicious.viciouslib.database.tracking.JSONTrackable;
import com.vicious.viciouslib.database.tracking.values.TrackableEnum;
import com.vicious.viciouslib.database.tracking.values.TrackableObject;
import com.vicious.viciouslib.database.tracking.values.TrackableValue;
import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslibkit.block.BlockInstance;
import com.vicious.viciouslibkit.block.BlockTemplate;
import com.vicious.viciouslibkit.util.ChunkPos;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class MultiBlockInstance extends JSONTrackable<MultiBlockInstance> {
    public static Map<Class<?>, BlockTemplate> templates = new HashMap<>();
    public Class<?> type;
    public World world;
    //Same as a vector3i just with SQL support i guess. We're not using the sql part so whatever.
    public TrackableObject<SQLVector3i> xyz = add(new TrackableObject<>("p",()->new SQLVector3i(0,0,0),this));
    public TrackableEnum<BlockFace> facing = add(new TrackableEnum<>("f",()->BlockFace.NORTH,this));
    public TrackableObject<Boolean> flipped = add(new TrackableObject<>("d",()->false,this));
    public MultiBlockBoundingBox box;
    private BlockTemplate validTemplate;
    public final UUID ID;

    public MultiBlockInstance(Class<?> mbType, World w, Location l, BlockFace dir, boolean flipped, UUID id) {
        super(MultiBlockHandler.getChunkDir(w,ChunkPos.fromBlockPos(l)).toAbsolutePath() + "/" + mbType.getCanonicalName() + "_" + id + ".json");
        type = mbType;
        world = w;
        xyz.setWithoutUpdate(new SQLVector3i(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
        facing.setWithoutUpdate(dir);
        this.flipped.setWithoutUpdate(flipped);
        validTemplate=templates.get(type).rotate(dir);
        if(flipped) validTemplate=validTemplate.flipX();
        this.ID =id;
    }
    public MultiBlockInstance(Class<?> type, World w, UUID id, ChunkPos cpos){
        super(MultiBlockHandler.getChunkDir(w,cpos).toAbsolutePath() + "/" + type.getCanonicalName() + "_" + id + ".json");
        this.type=type;
        this.ID = id;
        this.world=w;
    }

    @Override
    public void onInitialization() {
        super.onInitialization();
        if(validTemplate == null) validTemplate = templates.get(type).rotate(facing.value());
    }

    public MultiBlockInstance setBox(MultiBlockBoundingBox box) {
        this.box = box;
        box.mbi = this;
        return this;
    }
    public void revalidate(Block b, MultiBlockWorldData dat) {
        Location l = b.getLocation();
        SQLVector3i v = xyz.value();
        Vector sp = validTemplate.getOriginTranslation();
        //Translate to template corner.
        v = new SQLVector3i(v.x+sp.getBlockX(),v.y+sp.getBlockY(),v.z+sp.getBlockZ());
        //Translate to the block location in the template.
        v = new SQLVector3i(l.getBlockX()-v.x,l.getBlockY()-v.y,l.getBlockZ()-v.z);
        BlockInstance i = validTemplate.get(v.x,v.y,v.z);
        if(i == null) return;
        if(!i.matches(b.getWorld().getBlockAt(l))){
            invalidate(dat);
        }
    }
    public void revalidate(Block b, BlockInstance in, MultiBlockWorldData dat) {
        Location l = b.getLocation();
        SQLVector3i v = xyz.value();
        Vector sp = validTemplate.getOriginTranslation();
        //Translate to template corner.
        v = new SQLVector3i(v.x+sp.getBlockX(),v.y+sp.getBlockY(),v.z+sp.getBlockZ());
        //Translate to the block location in the template.
        v = new SQLVector3i(l.getBlockX()-v.x,l.getBlockY()-v.y,l.getBlockZ()-v.z);
        BlockInstance i = validTemplate.get(v.x,v.y,v.z);
        if(i == null) return;
        if(!i.equals(in)){
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

    /**
     * How this works:
     * Checks orientation in every facing orientation : NORTH,SOUTH,EAST,WEST
     * Returns the orientation with the most blocks correct.
     */
    public static MultiBlockState validate(World w, Location startPos, Class<?> type){
        BlockTemplate blocks = templates.get(type);
        //NORTH ORIENTATION
        BlockFace orientation = BlockFace.NORTH;
        MultiBlockState state = validate(w,startPos,blocks,orientation);
        if(state.isValid()) return state;
        //EAST ORIENTATION
        orientation = BlockFace.EAST;
        state = bestCase(state, validate(w,startPos,blocks,orientation));
        if(state.isValid()) return state;
        //SOUTH ORIENTATION
        orientation = BlockFace.SOUTH;
        state = bestCase(state, validate(w,startPos,blocks,orientation));
        if(state.isValid()) return state;
        //WEST ORIENTATION
        orientation = BlockFace.WEST;
        state = bestCase(state, validate(w,startPos,blocks,orientation));
        return state;
    }

    /**
     * How this works:
     * Rotates the template to the correct orientation.
     * If false, checks the x flipped version of the template.
     * Returns the state with the most blocks correct.
     */
    public static MultiBlockState validate(World w, Location startPos, BlockTemplate blocks, BlockFace orientation){
        blocks = blocks.rotate(orientation);
        MultiBlockState state = validate(blocks,w,startPos);
        if(state.facing(orientation).isValid()) return state;
        else return bestCase(state, validate(blocks.flipX(),w,startPos).flip(true));
    }
    private static MultiBlockState bestCase(MultiBlockState state1, MultiBlockState state2){
        if(state1.count > state2.count) return state1;
        return state2;
    }

    /**
     * Returns the state of the template in the world.
     */
    private static MultiBlockState validate(BlockTemplate blocks, World w, Location startPos){
        startPos = startPos.clone().add(blocks.getOriginTranslation());
        long count = 0;
        for (int x = 0; x <= blocks.maxX; x++) {
            for (int y = 0; y <= blocks.maxY; y++) {
                for (int z = 0; z <= blocks.maxZ; z++) {
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
        return validTemplate;
    }

    @Override
    public String toString() {
        return "MultiBlockInstance{" +
                "type=" + type +
                ", world=" + world.getUID() +
                ", xyz=" + xyz +
                ", facing=" + facing +
                ", ID=" + ID +
                '}';
    }
}
