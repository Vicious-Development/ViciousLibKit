package com.vicious.viciouslibkit.event;


import com.vicious.viciouslibkit.data.worldstorage.PluginWorldData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

public class ChunkEvents implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent ev){
        PluginWorldData.loadChunk(ev.getChunk());
    }
    public void onChunkUnload(ChunkUnloadEvent ev){
        PluginWorldData.unloadChunk(ev.getChunk());
    }
}
