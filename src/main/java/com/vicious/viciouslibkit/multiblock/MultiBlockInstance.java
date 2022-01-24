package com.vicious.viciouslibkit.multiblock;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import com.vicious.viciouslib.database.tracking.JSONTrackable;
import com.vicious.viciouslib.database.tracking.values.TrackableObject;
import com.vicious.viciouslib.database.tracking.values.TrackableValue;
import com.vicious.viciouslibkit.block.BlockInstance;
import com.vicious.viciouslibkit.block.BlockTemplate;
import com.vicious.viciouslibkit.util.ChunkPos;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

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
    public TrackableObject<Class<?>> type = add(new TrackableObject<>("t",()->null,this));
    public TrackableObject<World> world = add(new TrackableObject<>("w",()->null,this));
    //Same as a vector3i just with SQL support i guess. We're not using the sql part so whatever.
    public TrackableObject<SQLVector3i> xyz = add(new TrackableObject<>("p",()->new SQLVector3i(0,0,0),this));
    public MultiBlockBoundingBox box;
    private final UUID id;

    public MultiBlockInstance(Class<?> mbType, World w, Location l, UUID id) {
        super(MultiBlockHandler.getChunkDir(ChunkPos.fromBlockPos(l)).toAbsolutePath() + "/" + id + ".json");
        type.setWithoutUpdate(mbType);
        world.setWithoutUpdate(w);
        xyz.setWithoutUpdate(new SQLVector3i(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
        this.id=id;
    }
    public MultiBlockInstance(UUID id, ChunkPos cpos){
        super(MultiBlockHandler.getChunkDir(cpos).toAbsolutePath() + "/" + id + ".json");
        this.id = id;
    }
    public MultiBlockInstance setBox(MultiBlockBoundingBox box) {
        this.box = box;
        box.mbi = this;
        return this;
    }
    public void revalidate(Block b, MultiBlockWorldData dat) {
        if(!box.revalidateAtPos(b,templates.get(type.value()))){
            invalidate(dat);
        }
    }

    protected void invalidate(MultiBlockWorldData dat) {
        dat.removeMultiBlock(this);
    }

    public static MultiBlockState validate(World w, Location startPos, Class<?> type){
        BlockTemplate blocks = templates.get(type);
        for (int x = 0; x < blocks.maxX; x++) {
            for (int y = 0; y < blocks.maxY; y++) {
                for (int z = 0; z < blocks.maxZ; z++) {
                    Location l = startPos.clone().add(x,y,z);
                    BlockInstance instance = blocks.get(x,y,z);
                    if(instance == null) continue;
                    if(!instance.matches(w.getBlockAt(l))) return new MultiBlockState(MultiBlockState.State.INVALID,new BlockInstance(w.getBlockAt(l)).verboseInfo() + " at (" + l.getBlockX() + "," + l.getBlockY() + "," + l.getBlockZ() + ") is invalid, expected: " + instance.verboseInfo());
                }
            }
        }
        return new MultiBlockState(MultiBlockState.State.VALID,null);
    }

    public BlockTemplate getTemplate() {
        return templates.get(this.type.value());
    }
}
