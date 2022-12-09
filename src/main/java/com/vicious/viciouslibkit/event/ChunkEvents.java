package com.vicious.viciouslibkit.event;


import com.vicious.viciouslibkit.data.worldstorage.PluginEntityData;
import com.vicious.viciouslibkit.data.worldstorage.PluginWorldData;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;

public class ChunkEvents implements Listener {
    @EventHandler
    public void onChunkLoad(ChunkLoadEvent ev){
        PluginWorldData.loadChunk(ev.getChunk());
    }
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent ev){
        PluginWorldData.unloadChunk(ev.getChunk());
    }

    @EventHandler
    public void onEntityLoad(EntitiesLoadEvent ev){
        for (Entity entity : ev.getEntities()) {
            PluginEntityData.loadEntity(entity);
        }
    }
    @EventHandler
    public void onEntityUnload(EntitiesUnloadEvent ev){
        for (Entity entity : ev.getEntities()) {
            PluginEntityData.unloadEntity(entity);
        }
    }
}
