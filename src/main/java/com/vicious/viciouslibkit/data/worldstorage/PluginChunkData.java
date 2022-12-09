package com.vicious.viciouslibkit.data.worldstorage;

import com.vicious.viciouslib.util.ClassMap;
import com.vicious.viciouslibkit.block.blockinstance.BlockInstance;
import com.vicious.viciouslibkit.data.DataTypeNotFoundException;
import com.vicious.viciouslibkit.interfaces.IChunkDataHandler;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PluginChunkData implements PluginDataStorage<IChunkDataHandler>{
    private final ClassMap<IChunkDataHandler> handlers = new ClassMap<>();
    private final List<Consumer<Chunk>> scheduled = new ArrayList<>();
    private Chunk chunk;
    public PluginChunkData(){
        initialize();
    }
    @SuppressWarnings("unchecked")
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

    @Override
    public ClassMap<IChunkDataHandler> getHandlerMap() {
        return handlers;
    }
}
