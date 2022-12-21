package com.vicious.viciouslibkit.data.worldstorage;

import com.vicious.viciouslib.util.ClassMap;
import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslibkit.data.DataTypeNotFoundException;
import com.vicious.viciouslibkit.interfaces.IPlayerDataHandler;
import org.bukkit.entity.Player;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PluginPlayerData implements PluginDataStorage<IPlayerDataHandler> {
    private static final Map<UUID, PluginPlayerData> dataMap = new HashMap<>();

    private final ClassMap<IPlayerDataHandler> handlers = new ClassMap<>();
    private Player player;

    public PluginPlayerData() {
        initialize(getClass());
    }

    public static void loadPlayer(Player entity) {
        getPlayerData(entity).load(entity);
    }

    public static PluginPlayerData getPlayerData(Player p) {
        return dataMap.computeIfAbsent(p.getUniqueId(), u -> new PluginPlayerData());
    }

    public static void unloadEntity(Player p) {
        getPlayerData(p).load(p);
    }

    @SuppressWarnings("unchecked")
    public <T extends IPlayerDataHandler> T getDataHandler(Class<T> cls) throws DataTypeNotFoundException {
        IPlayerDataHandler handler = null;
        Class<?> cycle = cls;
        while (handler == null) {
            if (cycle == null) throw new DataTypeNotFoundException("No data handler registered for " + cls + ".");
            handler = handlers.get(cycle);
            cycle = cycle.getSuperclass();
        }
        return (T) handler;
    }


    public static void savePlayer(Player p){
        getPlayerData(p).save(p);
    }

    public void save(Player p) {
        handlers.forEach((cls, h) -> {
            h.save(p);
        });
    }

    public void load(Player p) {
        player = p;
        handlers.forEach((cls, h) -> {
            h.load(p);
        });
    }


    public void unload(Player e) {
        handlers.forEach((cls, h) -> {
            h.unload(e);
        });
    }

    @Override
    public Path getSpecificDirectory() {
        return FileUtil.createDirectoryIfDNE(getDataDirectory() + "/" + player.getUniqueId());
    }

    @Override
    public ClassMap<IPlayerDataHandler> getHandlerMap() {
        return handlers;
    }
}
