package com.vicious.viciouslibkit;

import com.vicious.viciouslibkit.event.BlockEvents;
import com.vicious.viciouslibkit.event.ChunkEvents;
import com.vicious.viciouslibkit.util.LibKitDirectories;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class ViciousLibKit extends JavaPlugin {
    private static Logger LOGGER;
    @Override
    public void onEnable() {
        LOGGER=getLogger();
        LibKitDirectories.initializePluginDependents();
        getServer().getPluginManager().registerEvents(new ChunkEvents(), this);
        getServer().getPluginManager().registerEvents(new BlockEvents(), this);
        // Plugin startup logic

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public static Logger logger(){
        return LOGGER;
    }
}
