package com.vicious.viciouslibkit.event;


import com.vicious.viciouslibkit.Viciouslibkit;
import com.vicious.viciouslibkit.multiblock.MultiBlockHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkEvents implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent ev){
        try {
            MultiBlockHandler.load(ev.getChunk());
        } catch (ClassNotFoundException ex){
            Viciouslibkit.logger().warning("Multiblock previously registered no longer exists.");
        }
    }
}
