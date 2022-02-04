package com.vicious.viciouslibkit.util;

import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslibkit.ViciousLibKit;
import org.bukkit.Bukkit;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class LibKitDirectories {
    public static Path pluginsDirectory;
    public static Path pluginConfigDirectory;
    public static Path libKitConfigPath;
    public static Path overworldPath;
    public static String overworldName;
    //This path is wiped on a server reset.
    public static Path worldPersistentDataDir;
    public static Path multiblockDataDir;
    public static Properties serverProperties = new Properties();
    //Anything stored here will ultimately be deleted.
    static{
        try {
            serverProperties.load(new FileReader(rootDir() + "/server.properties"));
        } catch (IOException e) {
            ViciousLibKit.logger().severe("Tried to read from server.properties, but the properties file did not exist???" +
                    "\nIf you have some sort of cursed minecraft server without server.properties, please create the file and set 'level-name' to your worldstorage name.");
            e.printStackTrace();
            System.exit(-1);
        }
    }
    public static void initializePluginDependents(){
        pluginsDirectory = FileUtil.createDirectoryIfDNE(rootDir() + "/plugins");
        pluginConfigDirectory = FileUtil.createDirectoryIfDNE(pluginsDirectory.toAbsolutePath() + "/config");
        libKitConfigPath = Paths.get(pluginConfigDirectory.toAbsolutePath() + "/dubiousConfigPath.json");
        overworldName = serverProperties.getProperty("level-name");
        overworldPath = FileUtil.createDirectoryIfDNE(rootDir() + "/" + overworldName);
        worldPersistentDataDir = FileUtil.createDirectoryIfDNE(overworldPath + "/" + "vicious");
    }

    public static String rootDir() {
        return Bukkit.getServer().getWorldContainer().getAbsolutePath();
   }
}
