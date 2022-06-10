package com.vicious.viciouslibkit.data.provided.netnode;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import com.vicious.viciouslib.database.tracking.JSONTrackable;
import com.vicious.viciouslib.database.tracking.values.TrackableObject;
import com.vicious.viciouslibkit.block.blockinstance.BlockInstance;
import com.vicious.viciouslibkit.services.netnode.NetNodeService;
import com.vicious.viciouslibkit.util.vector.ChunkPos;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.UUID;

public abstract class NetNode extends JSONTrackable<NetNode> {
    public Class<? extends NetNode> type;
    public World world;
    public final UUID ID;

    public TrackableObject<SQLVector3i> xyz = add(new TrackableObject<>("p",()->new SQLVector3i(0,0,0),this));
    public TrackableObject<UUID> netID = add(new TrackableObject<>("n",()->null,this,UUID.class));

    public NetNode(Class<? extends NetNode> nnType, World w, Location l, UUID id) {
        super(NetNodeService.getNNPath(w,l,nnType,id));
        this.ID =id;
        type = nnType;
        world = w;
        xyz.setWithoutUpdate(new SQLVector3i(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
    }
    public NetNode(Class<? extends NetNode> type, World w, UUID id, ChunkPos cpos){
        super(NetNodeService.getNNPath(w,cpos,type,id));
        this.type=type;
        this.ID = id;
        this.world=w;
    }

    public boolean revalidate(BlockInstance bi) {
        return getExpectedBlock().matches(bi);
    }
    public boolean revalidate(Block b) {
        return getExpectedBlock().matches(b);
    }
    public abstract BlockInstance getExpectedBlock();

    public void invalidate(){

    }
    public void validate(){

    }
}
