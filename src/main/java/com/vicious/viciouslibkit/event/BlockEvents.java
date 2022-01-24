package com.vicious.viciouslibkit.event;

import com.vicious.viciouslibkit.multiblock.MultiBlockHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BlockEvents implements Listener {
    @EventHandler
    public void onBlockEvent(org.bukkit.event.block.BlockEvent ev){
        MultiBlockHandler.handleBlockEvent(ev);
    }
}
