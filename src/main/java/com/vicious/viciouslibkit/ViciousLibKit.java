package com.vicious.viciouslibkit;

import com.vicious.viciouslib.util.reflect.deep.DeepReflection;
import com.vicious.viciouslibkit.data.worldstorage.PluginChunkData;
import com.vicious.viciouslibkit.event.BlockEvents;
import com.vicious.viciouslibkit.event.ChunkEvents;
import com.vicious.viciouslibkit.inventory.wrapper.InventoryEvents;
import com.vicious.viciouslibkit.inventory.wrapper.InventoryWrapperChunkHandler;
import com.vicious.viciouslibkit.util.LibKitDirectories;
import com.vicious.viciouslibkit.util.NMSHelper;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Logger;

public final class ViciousLibKit extends JavaPlugin {
    public static Logger LOGGER;
    public static ViciousLibKit INSTANCE;
    public static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    //The location of paper.jar Used to scan all classes included within.
    public static Enumeration<URL> jarURL;
    @Override
    public void onEnable() {

        LibKitDirectories.initializePluginDependents();
        INSTANCE=this;
        LOGGER=getLogger();
        try {
            jarURL = classLoader.getResources("net/minecraft");
            DeepReflection.mapClasses(jarURL, classLoader);
            new NMSHelper();
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize the NMSHelper.");
            e.printStackTrace();
            return;
        }
        getServer().getPluginManager().registerEvents(new ChunkEvents(), this);
        getServer().getPluginManager().registerEvents(new BlockEvents(), this);
        getServer().getPluginManager().registerEvents(new InventoryEvents(), this);
        PluginChunkData.registerDataType(InventoryWrapperChunkHandler.class, InventoryWrapperChunkHandler::new);

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
