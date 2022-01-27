package com.vicious.viciouslibkit.event;

import com.vicious.viciouslibkit.block.BlockInstance;
import com.vicious.viciouslibkit.multiblock.MultiBlockHandler;
import com.vicious.viciouslibkit.util.WorldUtil;
import org.bukkit.Particle;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockEvents implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent ev){
        if(ev.isCancelled()) return;
        MultiBlockHandler.handleBlockChange(BlockInstance.AIR,ev.getBlock());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent ev){
        if(ev.isCancelled()) return;
        MultiBlockHandler.handleBlockChange(ev.getBlock());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockForm(BlockFormEvent ev){
        if(ev.isCancelled()) return;
        MultiBlockHandler.handleBlockChange(ev.getBlock());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockSpread(BlockSpreadEvent ev){
        if(ev.isCancelled()) return;
        MultiBlockHandler.handleBlockChange(ev.getBlock());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBurn(BlockBurnEvent ev) {
        if (ev.isCancelled()) return;
        MultiBlockHandler.handleBlockChange(ev.getBlock());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockExplode(BlockExplodeEvent ev) {
        if (ev.isCancelled()) return;
        MultiBlockHandler.handleBlockChange(ev.getBlock());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockFromTo(BlockFromToEvent ev) {
        if (ev.isCancelled()) return;
        MultiBlockHandler.handleBlockChange(ev.getBlock());
    }
}
