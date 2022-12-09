package com.vicious.viciouslibkit.data.worldstorage;

import com.vicious.viciouslib.util.ClassMap;
import com.vicious.viciouslibkit.data.DataTypeNotFoundException;
import com.vicious.viciouslibkit.interfaces.IEntityDataHandler;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

public class PluginEntityData implements PluginDataStorage<IEntityDataHandler>{
    private static final Map<Class<? extends IEntityDataHandler>, Supplier<IEntityDataHandler>> registeredDataClasses = new HashMap<>();
    private static final Map<UUID, PluginEntityData> dataMap = new HashMap<>();

    public static void registerDataType(Class<? extends IEntityDataHandler> cls, Supplier<IEntityDataHandler> dataHandlerSupplier){
        registeredDataClasses.put(cls,dataHandlerSupplier);
    }
    private final ClassMap<IEntityDataHandler> handlers = new ClassMap<>();
    private Entity entity;
    public PluginEntityData(){
        registeredDataClasses.forEach((c,d)-> handlers.put(c,d.get()));
    }

    public static void loadEntity(Entity entity) {
        getEntityData(entity).load(entity);
    }

    private static PluginEntityData getEntityData(Entity entity) {
        return dataMap.computeIfAbsent(entity.getUniqueId(),u->new PluginEntityData());
    }

    public static void unloadEntity(Entity entity) {
        getEntityData(entity).load(entity);
    }

    @SuppressWarnings("unchecked")
    public <T extends IEntityDataHandler> T getDataHandler(Class<T> cls) throws DataTypeNotFoundException {
        IEntityDataHandler handler = null;
        Class<?> cycle = cls;
        while (handler == null) {
            if (cycle == null) throw new DataTypeNotFoundException("No data handler registered for " + cls + ".");
            handler = handlers.get(cycle);
            cycle = cycle.getSuperclass();
        }
        return (T) handler;
    }


    public void save(Entity e) {
        handlers.forEach((cls,h)->{
            h.save(e);
        });
    }

    public void load(Entity e) {
        entity = e;
        handlers.forEach((cls,h)->{
            h.load(e);
        });
    }

    public void unload(Entity e) {
        handlers.forEach((cls, h) -> {
            h.unload(e);
        });
    }
    public Entity getEntity(){
        return entity;
    }

    @Override
    public ClassMap<IEntityDataHandler> getHandlerMap() {
        return handlers;
    }
}
