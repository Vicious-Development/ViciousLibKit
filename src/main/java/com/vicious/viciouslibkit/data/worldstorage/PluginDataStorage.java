package com.vicious.viciouslibkit.data.worldstorage;

import com.vicious.viciouslib.util.ClassMap;
import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslibkit.interfaces.IDataHandler;
import com.vicious.viciouslibkit.util.LibKitDirectories;

import java.nio.file.Path;
import java.util.Locale;
import java.util.function.Supplier;

public interface PluginDataStorage<V extends IDataHandler> {
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
    default <T extends PluginDataStorage<V>> void initialize(Class<T> cls){
        ClassMap<V> map = getHandlerMap();
        PluginDataStorage.getPluginDataRegistry(cls).dataHandlerFactories.forEach((c,d)->map.put(c,d.get()));
    }

    default Path getDataDirectory(){
        return FileUtil.createDirectoryIfDNE(LibKitDirectories.worldPersistentDataDir.toAbsolutePath() + "/" + getClass().getSimpleName().toLowerCase(Locale.ROOT));
    }

    Path getSpecificDirectory();

    ClassMap<V> getHandlerMap();

}
