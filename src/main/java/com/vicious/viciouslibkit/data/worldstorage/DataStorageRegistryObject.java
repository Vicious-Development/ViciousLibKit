package com.vicious.viciouslibkit.data.worldstorage;

import com.vicious.viciouslib.util.ClassMap;
import com.vicious.viciouslibkit.interfaces.IDataHandler;

import java.util.function.Supplier;

public class DataStorageRegistryObject<STORAGE extends PluginDataStorage<V>,V extends IDataHandler> {
    public final ClassMap<Supplier<V>> dataHandlerFactories = new ClassMap<>();
    public final Supplier<STORAGE> storageFactory;
    public DataStorageRegistryObject(Supplier<STORAGE> factory){
        this.storageFactory=factory;
    }
    @SuppressWarnings("unchecked")
    public <T extends V> void registerDataType(Class<T> cls, Supplier<T> factory){
        dataHandlerFactories.put(cls,(Supplier<V>) factory);
    }
}
