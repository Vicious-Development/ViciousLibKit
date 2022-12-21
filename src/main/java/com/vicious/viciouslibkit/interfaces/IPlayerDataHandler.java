package com.vicious.viciouslibkit.interfaces;

import org.bukkit.entity.Player;

public interface IPlayerDataHandler extends IDataHandler{
    void load(Player p);
    void unload(Player p);
    void save(Player p);
}
