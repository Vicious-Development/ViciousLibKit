package com.vicious.viciouslibkit;

import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslib.util.reflect.deep.DeepReflection;
import com.vicious.viciouslibkit.data.provided.multiblock.MultiBlockChunkDataHandler;
import com.vicious.viciouslibkit.data.worldstorage.*;
import com.vicious.viciouslibkit.event.BlockEvents;
import com.vicious.viciouslibkit.event.ChunkEvents;
import com.vicious.viciouslibkit.event.PlayerEvents;
import com.vicious.viciouslibkit.event.WorldEvents;
import com.vicious.viciouslibkit.event.piston.PistonWatcher;
import com.vicious.viciouslibkit.interfaces.IChunkDataHandler;
import com.vicious.viciouslibkit.inventory.wrapper.InventoryEvents;
import com.vicious.viciouslibkit.inventory.wrapper.InventoryWrapperChunkHandler;
import com.vicious.viciouslibkit.util.LibKitDirectories;
import com.vicious.viciouslibkit.util.nms.NMSHelper;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URL;
import java.util.Enumeration;
import java.util.logging.Logger;

public final class ViciousLibKit extends JavaPlugin implements Listener {
    public static Logger LOGGER;
    public static ViciousLibKit INSTANCE;
    public static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    //The location of paper.jar Used to scan all classes included within.
    public static Enumeration<URL> jarURL;

    public static final String pluginsPath = FileUtil.createDirectoryIfDNE("plugins").toAbsolutePath().toString();
    public static final String pluginsConfigPath = FileUtil.createDirectoryIfDNE(pluginsPath + "/config").toAbsolutePath().toString();
    public static final String viciousConfigPath = FileUtil.createDirectoryIfDNE(pluginsConfigPath + "/vicious").toAbsolutePath().toString();
    public static final String defaultRecipesPath = FileUtil.createDirectoryIfDNE(viciousConfigPath + "/recipes").toAbsolutePath().toString();

    @Override
    public void onEnable() {
        LibKitDirectories.initializePluginDependents();
        INSTANCE=this;
        LOGGER=getLogger();
        try {
            jarURL = classLoader.getResources("net/minecraft");
            DeepReflection.mapClasses(jarURL, classLoader,true);
            NMSHelper.init();
        } catch (Exception e) {
            LOGGER.severe("Failed to initialize the NMSHelper.");
            //e.printStackTrace();
            //return;
        }

        PluginDataStorage.registerPluginDataType(PluginWorldData.class,PluginWorldData::new);
        DataStorageRegistryObject<PluginChunkData, IChunkDataHandler> chunkDataStorage = PluginDataStorage.registerPluginDataType(PluginChunkData.class,PluginChunkData::new);
        PluginDataStorage.registerPluginDataType(PluginPlayerData.class,PluginPlayerData::new);

        chunkDataStorage.registerDataType(InventoryWrapperChunkHandler.class, InventoryWrapperChunkHandler::new);
        chunkDataStorage.registerDataType(MultiBlockChunkDataHandler.class,MultiBlockChunkDataHandler::new);

        getServer().getPluginManager().registerEvents(new ChunkEvents(), this);
        getServer().getPluginManager().registerEvents(new BlockEvents(), this);
        getServer().getPluginManager().registerEvents(new InventoryEvents(), this);
        getServer().getPluginManager().registerEvents(new PistonWatcher(), this);
        getServer().getPluginManager().registerEvents(new WorldEvents(), this);
        getServer().getPluginManager().registerEvents(new PlayerEvents(), this);
        Bukkit.getPluginManager().registerEvents(this,this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    public static Logger logger(){
        return LOGGER;
    }
}
