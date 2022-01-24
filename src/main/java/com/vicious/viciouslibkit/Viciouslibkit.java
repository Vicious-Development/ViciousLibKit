package com.vicious.viciouslibkit;

import com.vicious.viciouslibkit.util.LibKitDirectories;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class Viciouslibkit extends JavaPlugin {
    private static Logger LOGGER;
    @Override
    public void onEnable() {
        LOGGER=getLogger();
        LibKitDirectories.initializePluginDependents();
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
