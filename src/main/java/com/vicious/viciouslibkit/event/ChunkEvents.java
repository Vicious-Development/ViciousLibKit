package com.vicious.viciouslibkit.event;


import com.vicious.viciouslibkit.multiblock.MultiBlockHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;

public class ChunkEvents implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent ev){
        MultiBlockHandler.load(ev.getChunk());
    }
}
