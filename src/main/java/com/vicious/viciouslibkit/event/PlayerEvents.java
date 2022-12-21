package com.vicious.viciouslibkit.event;

import com.vicious.viciouslibkit.data.worldstorage.PluginPlayerData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent ev){
        PluginPlayerData.loadPlayer(ev.getPlayer());
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent ev){
        PluginPlayerData.savePlayer(ev.getPlayer());
    }
    @EventHandler
    public void onDeath(PlayerDeathEvent ev){
        PluginPlayerData.savePlayer(ev.getEntity());
    }
}
