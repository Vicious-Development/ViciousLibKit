package com.vicious.viciouslibkit.data.worldstorage;

import com.vicious.viciouslibkit.ViciousLibKit;
import com.vicious.viciouslibkit.block.BlockInstance;
import com.vicious.viciouslibkit.data.DataTypeNotFoundException;
import com.vicious.viciouslibkit.data.provided.multiblock.MultiBlockInstance;
import com.vicious.viciouslibkit.interfaces.IChunkDataHandler;
import org.bukkit.Chunk;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class PluginChunkData {
    private static Map<Class<? extends IChunkDataHandler>, Supplier<IChunkDataHandler>> registeredDataClasses = new HashMap<>();
    private Map<Class<? extends IChunkDataHandler>,IChunkDataHandler> handlers = new HashMap<>();
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
    public static void registerDataType(Class<? extends IChunkDataHandler> cls, Supplier<IChunkDataHandler> dataHandlerSupplier){
        registeredDataClasses.put(cls,dataHandlerSupplier);
    }

    public void save(Chunk c) {
        handlers.forEach((cls,h)->{
            h.save(c);
        });
    }

    public void load(Chunk c) {
        handlers.forEach((cls,h)->{
            h.load(c);
        });
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
    public void removeMultiBlock(MultiBlockInstance mbi) {

    }
}
