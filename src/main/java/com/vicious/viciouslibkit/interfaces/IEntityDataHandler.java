package com.vicious.viciouslibkit.interfaces;

import org.bukkit.entity.Entity;

public interface IEntityDataHandler extends IDataHandler{
    void load(Entity p);
    void unload(Entity p);
    void save(Entity p);
}
