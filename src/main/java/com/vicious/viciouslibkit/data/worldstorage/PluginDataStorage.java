package com.vicious.viciouslibkit.data.worldstorage;

import com.vicious.viciouslib.util.ClassMap;
import com.vicious.viciouslibkit.interfaces.IDataHandler;

import java.util.function.Supplier;

public interface PluginDataStorage<V> {
    ClassMap<DataStorageRegistryObject<?,?>> registry = new ClassMap<>();
    static <T extends PluginDataStorage<V>,V extends IDataHandler> DataStorageRegistryObject<T,V> registerPluginDataType(Class<T> dataType, Supplier<T> storageFactory){
        DataStorageRegistryObject<T,V> rog = new DataStorageRegistryObject<>(storageFactory);
        registry.put(dataType, rog);
        return rog;
    }
    @SuppressWarnings("unchecked")
    static <STORAGE extends PluginDataStorage<V>,V extends IDataHandler> DataStorageRegistryObject<STORAGE,V> getPluginDataRegistry(Class<STORAGE> cls){
        return (DataStorageRegistryObject<STORAGE, V>) registry.get(cls);
    }
    @SuppressWarnings("unchecked")
    default void initialize(){
        ClassMap<V> map = getHandlerMap();
        PluginDataStorage.getPluginDataRegistry(PluginChunkData.class).dataHandlerFactories.forEach((c,d)->map.put(c,(V)d.get()));
    }

    ClassMap<V> getHandlerMap();

}
