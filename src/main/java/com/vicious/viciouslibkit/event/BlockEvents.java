package com.vicious.viciouslibkit.event;

import com.vicious.viciouslibkit.block.blockinstance.BlockInstance;
import com.vicious.viciouslibkit.data.worldstorage.PluginWorldData;
import com.vicious.viciouslibkit.services.multiblock.MultiBlockService;
import com.vicious.viciouslibkit.services.multiblock.MultiBlockState;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockEvents implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent ev){
        if(ev.isCancelled()) return;
        MultiBlockService.handleBlockChange(BlockInstance.AIR,ev.getBlock());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent ev){
        if(ev.isCancelled()) return;
        MultiBlockService.handleBlockChange(ev.getBlock());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockForm(BlockFormEvent ev){
        if(ev.isCancelled()) return;
        MultiBlockService.handleBlockChange(ev.getBlock());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockSpread(BlockSpreadEvent ev){
        if(ev.isCancelled()) return;
        MultiBlockService.handleBlockChange(ev.getBlock());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBurn(BlockBurnEvent ev) {
        if (ev.isCancelled()) return;
        MultiBlockService.handleBlockChange(ev.getBlock());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockExplode(BlockExplodeEvent ev) {
        if (ev.isCancelled()) return;
        MultiBlockService.handleBlockChange(ev.getBlock());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockFromTo(BlockFromToEvent ev) {
        if (ev.isCancelled()) return;
        MultiBlockService.handleBlockChange(ev.getBlock());
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent ev){
        if(ev.getItem() == null) return;
        if(ev.getAction() == Action.RIGHT_CLICK_BLOCK && ev.getItem().getType() == Material.STICK){
            Block b = ev.getClickedBlock();
            if(b == null) return;
            MultiBlockState state = PluginWorldData.createMultiblockOn(b);
            if(state.count != -1) ev.getPlayer().sendMessage(state.toString());
        }
    }
}
