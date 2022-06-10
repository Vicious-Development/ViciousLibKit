package com.vicious.viciouslibkit.data.provided.netnode;

import com.vicious.viciouslibkit.ViciousLibKit;
import com.vicious.viciouslibkit.block.blockinstance.BlockInstance;
import com.vicious.viciouslibkit.interfaces.IChunkDataHandler;
import com.vicious.viciouslibkit.services.netnode.NNReconstructor;
import com.vicious.viciouslibkit.services.netnode.NetNodeService;
import com.vicious.viciouslibkit.util.vector.ChunkPos;
import com.vicious.viciouslibkit.util.LibKitUtil;
import com.vicious.viciouslibkit.util.map.PositionMap;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class NetChunkDataHandler implements IChunkDataHandler {
    public PositionMap<NetNode> nodes = new PositionMap<>();
    public static final String DATAID = "netnodes";

    @Override
    public void load(Chunk c) {
        ChunkPos cpos = new ChunkPos(c.getX(),c.getZ());
        for (File file : getFiles(c)) {
            int underscoreidx = file.getName().indexOf("_");
            String mbID = file.getName().substring(0, underscoreidx);
            Class<? extends NetNode> cls = NetNodeService.idMap.getV(mbID);
            if(cls == null) {
                ViciousLibKit.logger().warning("NetNode type previously registered no longer exists. Multiblock ID:" + mbID);
                return;
            }
            NNReconstructor<?> constructor = NetNodeService.reconstructors.get(cls);
            NetNode node = constructor.construct(cls, c.getWorld(), UUID.fromString(file.getName().substring(underscoreidx + 1).replace(".json", "")), cpos);
            node.executeIfInitialized((x) -> addNetNode(node));
        }
    }

    @Override
    public void unload(Chunk c) {
        nodes.forEach((k,n)->{
            n.invalidate();
        });
        nodes.clear();
    }

    @Override
    public void save(Chunk c) {
        nodes.forEach((k,n)->{
            n.save();
        });
    }

    @Override
    public void handleBlockChange(Block b) {
        NetNode node = nodes.get(LibKitUtil.fromLocation(b.getLocation()));
        if(node == null) return;
        if(!node.revalidate(b)){
            node.invalidate();
            removeNetNode(node);
        }
    }

    @Override
    public void handleBlockChange(BlockInstance bi, Block b) {
        NetNode node = nodes.get(LibKitUtil.fromLocation(b.getLocation()));
        if(node == null) return;
        if(!node.revalidate(bi)){
            node.invalidate();
            removeNetNode(node);
        }
    }
    public void removeNetNode(NetNode node)  {
        nodes.remove(node.xyz.value());
        try {
            Files.deleteIfExists(node.PATH);
        } catch(IOException e){
            ViciousLibKit.logger().severe(e.getMessage());
            e.printStackTrace();
        }
    }
    @Override
    public String getDataID() {
        return DATAID;
    }

    private void addNetNode(NetNode node) {
        nodes.put(node.xyz.value(),node);
        node.validate();
    }
}
