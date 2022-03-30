package com.vicious.viciouslibkit.data.provided.multiblock;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import com.vicious.viciouslib.database.tracking.JSONTrackable;
import com.vicious.viciouslib.database.tracking.values.TrackableEnum;
import com.vicious.viciouslib.database.tracking.values.TrackableObject;
import com.vicious.viciouslibkit.block.BlockTemplate;
import com.vicious.viciouslibkit.block.blockinstance.BlockInstance;
import com.vicious.viciouslibkit.services.multiblock.MultiBlockBoundingBox;
import com.vicious.viciouslibkit.services.multiblock.MultiBlockService;
import com.vicious.viciouslibkit.services.multiblock.MultiBlockState;
import com.vicious.viciouslibkit.util.ChunkPos;
import com.vicious.viciouslibkit.util.LibKitUtil;
import com.vicious.viciouslibkit.util.WorldUtil;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class MultiBlockInstance extends JSONTrackable<MultiBlockInstance> {
    public static Map<Class<?>, BlockTemplate> templates = new HashMap<>();
    public Class<? extends MultiBlockInstance> type;
    public World world;
    //Same as a vector3i just with SQL support i guess. We're not using the sql part so whatever.
    public TrackableObject<SQLVector3i> xyz = add(new TrackableObject<>("p",()->new SQLVector3i(0,0,0),this));
    public TrackableEnum<BlockFace> facing = add(new TrackableEnum<>("f",()->BlockFace.NORTH,this));
    public TrackableObject<Boolean> flipped = add(new TrackableObject<>("d",()->false,this));
    public TrackableObject<Boolean> upsideDown = add(new TrackableObject<>("o",()->false,this));
    public MultiBlockBoundingBox box;
    protected BlockTemplate validTemplate;
    public final UUID ID;

    public MultiBlockInstance(Class<? extends MultiBlockInstance> mbType, World w, Location l, BlockFace dir, boolean flipped, boolean flippedVertical, UUID id) {
        super(MultiBlockService.getMBPath(w,l,mbType,id));
        type = mbType;
        world = w;
        xyz.setWithoutUpdate(new SQLVector3i(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
        facing.setWithoutUpdate(dir);
        this.flipped.setWithoutUpdate(flipped);
        this.upsideDown.setWithoutUpdate(flippedVertical);
        validTemplate=templates.get(type).rotate(dir);
        if(flipped) validTemplate=validTemplate.flipX();
        if(flippedVertical) validTemplate=validTemplate.flipY();
        this.ID =id;
    }
    public MultiBlockInstance(Class<? extends MultiBlockInstance> type, World w, UUID id, ChunkPos cpos){
        super(MultiBlockService.getMBPath(w,cpos,type,id));
        this.type=type;
        this.ID = id;
        this.world=w;
    }
    public void initValidTemplate(){
        validTemplate=templates.get(type).rotate(this.facing.value());
        if(this.flipped.value()) validTemplate=validTemplate.flipX();
        if(this.upsideDown.value()) validTemplate=validTemplate.flipY();
    }

    @Override
    public void onInitialization() {
        super.onInitialization();
        if(validTemplate == null) {
            validTemplate = templates.get(type).rotate(facing.value());
            if(flipped.value()) validTemplate=validTemplate.flipX();
            if(upsideDown.value()) validTemplate=validTemplate.flipY();
        }
    }

    public MultiBlockInstance setBox(MultiBlockBoundingBox box) {
        this.box = box;
        box.mbi = this;
        return this;
    }
    public void revalidate(Block b, MultiBlockChunkDataHandler dat) {
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
    public void revalidate(Block b, BlockInstance in, MultiBlockChunkDataHandler dat) {
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

    protected void invalidate(MultiBlockChunkDataHandler dat) {
        dat.removeMultiBlock(this);
    }

    /**  N      E      S      W
     * p      b a p  interfaces j b      interfaces
     * a c    j c      c a    c j
     * b j interfaces  interfaces          p  p a b
     *
     */

    public static MultiBlockState checkValid(World w, Location startPos, Class<?> type){
        BlockTemplate blocks = templates.get(type);
        MultiBlockState state = checkValid(w,startPos,blocks);
        if(!state.isValid() && !blocks.canBeUpsideDown) return state;
        else return bestCase(state, checkValid(w,startPos,blocks.flipY()).isUpsideDown(true));
    }
    /**
     * How this works:
     * Checks orientation in every facing orientation : NORTH,SOUTH,EAST,WEST
     * Returns the orientation with the most blocks correct.
     */
    public static MultiBlockState checkValid(World w, Location startPos, BlockTemplate blocks){
        //NORTH ORIENTATION
        BlockFace orientation = BlockFace.NORTH;
        MultiBlockState state = checkValid(w,startPos,blocks,orientation);
        if(state.isValid()) return state;
        //EAST ORIENTATION
        orientation = BlockFace.EAST;
        state = bestCase(state, checkValid(w,startPos,blocks,orientation));
        if(state.isValid()) return state;
        //SOUTH ORIENTATION
        orientation = BlockFace.SOUTH;
        state = bestCase(state, checkValid(w,startPos,blocks,orientation));
        if(state.isValid()) return state;
        //WEST ORIENTATION
        orientation = BlockFace.WEST;
        state = bestCase(state, checkValid(w,startPos,blocks,orientation));
        return state;
    }

    /**
     * How this works:
     * Rotates the template to the correct orientation.
     * If false, checks the x flipped version of the template.
     * Returns the state with the most blocks correct.
     */
    public static MultiBlockState checkValid(World w, Location startPos, BlockTemplate blocks, BlockFace orientation){
        blocks = blocks.rotate(orientation);
        MultiBlockState state = checkValid(blocks,w,startPos).facing(orientation);
        if(state.isValid()) return state;
        else return bestCase(state, checkValid(blocks.flipX(),w,startPos).facing(orientation).flip(true));
    }
    public static MultiBlockState bestCase(MultiBlockState state1, MultiBlockState state2){
        if(state1.count > state2.count) return state1;
        return state2;
    }
    public void validate(){
        Location loc = new Location(world,xyz.value().x,xyz.value().y,xyz.value().z);
        forEachSolid(loc, world,(w, l)->{
            WorldUtil.spawnHoveringParticlesAround(l,w, Particle.FIREWORKS_SPARK,1,0.15,0.15,0.15,0.16,3);
        });
    }
    /**
     * Returns the state of the template in the worldstorage.
     */
    private static MultiBlockState checkValid(BlockTemplate blocks, World w, Location startPos){
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
                ", worldstorage=" + world.getUID() +
                ", xyz=" + xyz +
                ", facing=" + facing +
                ", ID=" + ID +
                '}';
    }
    public void forEachSolid(Location startPos, World w, BiConsumer<World,Location> consumer){
        startPos = startPos.clone().add(validTemplate.getOriginTranslation());
        for (int x = 0; x <= validTemplate.maxX; x++) {
            for (int y = 0; y <= validTemplate.maxY; y++) {
                for (int z = 0; z <= validTemplate.maxZ; z++) {
                    Location l = startPos.clone().add(x,y,z);
                    BlockInstance instance = validTemplate.get(x,y,z);
                    if(instance == null || instance.isAir()) continue;
                    consumer.accept(w,l);
                }
            }
        }
    }

    /**
     * All multiblock structures can be rotated, flipped, etc, This relativizes a vector to those parameters.
     * The default orientation is NORTH-NOTFLIPPED-DOWNSIDEUP.
     * Note the vector should be the DISTANCE from the multiblock position to be usable.
     */
    public SQLVector3i relativize(SQLVector3i vec){
        return LibKitUtil.orientate(vec,facing.value(),flipped.value(), upsideDown.value());
    }

    /**
     * Returns the final in game position for the location distancevector away from the multiblock position.
     */
    public SQLVector3i getRelativePosition(SQLVector3i distanceVector){
        return xyz.value().add(LibKitUtil.orientate(distanceVector,facing.value(),flipped.value(), upsideDown.value()));
    }
}
