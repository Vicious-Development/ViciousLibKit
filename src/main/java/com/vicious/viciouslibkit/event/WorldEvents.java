package com.vicious.viciouslibkit.event;

import com.vicious.viciouslibkit.data.worldstorage.PluginWorldData;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldSaveEvent;

public class WorldEvents implements Listener {
    @EventHandler
    public void onWorldSave(WorldSaveEvent wse){
        World w = wse.getWorld();
        PluginWorldData.getWorldData(w).save(w);
    }
    @EventHandler
    public void onWorldLoad(WorldLoadEvent wle){
        World w = wle.getWorld();
        PluginWorldData.getWorldData(w).load(w);
    }
}
