package com.vicious.viciouslibkit.data.worldstorage;

import com.vicious.viciouslibkit.ViciousLibKit;
import com.vicious.viciouslibkit.block.blockinstance.BlockInstance;
import com.vicious.viciouslibkit.data.DataTypeNotFoundException;
import com.vicious.viciouslibkit.data.provided.multiblock.MultiBlockChunkDataHandler;
import com.vicious.viciouslibkit.data.provided.netnode.NetChunkDataHandler;
import com.vicious.viciouslibkit.interfaces.IChunkDataHandler;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PluginChunkData {
    private static Map<Class<? extends IChunkDataHandler>, Supplier<IChunkDataHandler>> registeredDataClasses = new HashMap<>();
    static {
        registerDataType(MultiBlockChunkDataHandler.class,MultiBlockChunkDataHandler::new);
        registerDataType(NetChunkDataHandler.class,NetChunkDataHandler::new);
    }
    public static void registerDataType(Class<? extends IChunkDataHandler> cls, Supplier<IChunkDataHandler> dataHandlerSupplier){
        registeredDataClasses.put(cls,dataHandlerSupplier);
    }
    private Map<Class<? extends IChunkDataHandler>,IChunkDataHandler> handlers = new HashMap<>();
    private List<Consumer<Chunk>> scheduled = new ArrayList<>();
    private Chunk chunk;
    public PluginChunkData(){
        registeredDataClasses.forEach((c,d)-> handlers.put(c,d.get()));
    }
    public <T extends IChunkDataHandler> T getDataHandler(Class<T> cls) throws DataTypeNotFoundException {
        IChunkDataHandler handler =  null;
        Class<?> cycle = cls;
        while(handler == null){
            if(cycle == null) throw new DataTypeNotFoundException("No data handler registered for " + cls + ".");
            handler = handlers.get(cycle);
            cycle = cycle.getSuperclass();
        }
        return (T)handler;
    }
    public <T extends IChunkDataHandler> T getDataHandlerExceptionless(Class<T> cls) {
        try {
            return getDataHandler(cls);
        } catch (DataTypeNotFoundException ex){
            ViciousLibKit.logger().severe(ex.getMessage());
            ex.printStackTrace();
        }
        return null;
    }


    public void save(Chunk c) {
        handlers.forEach((cls,h)->{
            h.save(c);
        });
    }

    public void load(Chunk c) {
        chunk=c;
        handlers.forEach((cls,h)->{
            h.load(c);
        });
        executeScheduledProcesses(c);
    }

    public void handleBlockChange(Block b) {
        handlers.forEach((cls,h)->{
            h.handleBlockChange(b);
        });
    }
    public void handleBlockChange(BlockInstance bi, Block b) {
        handlers.forEach((cls,h)->{
            h.handleBlockChange(bi,b);
        });
    }
    public void unload(Chunk c) {
        handlers.forEach((cls, h) -> {
            h.unload(c);
        });
    }
    public void scheduleProcessForLoad(Consumer<Chunk> chunkConsumer){
        if(chunk != null) {
            if(!chunk.isLoaded()) scheduled.add(chunkConsumer);
            else chunkConsumer.accept(chunk);
        }
        else scheduled.add(chunkConsumer);
    }
    private void executeScheduledProcesses(Chunk c){
        while(!scheduled.isEmpty()){
            scheduled.remove(scheduled.size()-1).accept(c);
        }
    }
    public Chunk getChunk(){
        return chunk;
    }
}
