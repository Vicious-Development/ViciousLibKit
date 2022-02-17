package com.vicious.viciouslibkit.services.multiblock;

import com.vicious.viciouslibkit.data.provided.multiblock.MultiBlockChunkDataHandler;
import com.vicious.viciouslibkit.data.provided.multiblock.MultiBlockInstance;
import com.vicious.viciouslibkit.event.Ticker;
import com.vicious.viciouslibkit.interfaces.ITickable;
import com.vicious.viciouslibkit.util.ChunkPos;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.util.UUID;

/**
 * Unoptimized ticking multiblock. To optimize your multiblock please ensure it is only added to the ticker
 * when necessary. You can prevent the multiblock from being added to the ticker by overriding addToTicker.
 * Some multiblocks will always require ticking, others won't, I highly recommend you put as much effort in as possible to make sure the tick() method is optimal.
 * If you use recipes, try not to check recipes every tick. Store the current recipe to memory and only validate on an inventory change.
 * Thank you for reading and considering the stability of servers.
 */
public abstract class TickableMultiBlock extends MultiBlockInstance implements ITickable {
    private boolean isTicking = false;
    public TickableMultiBlock(Class<? extends MultiBlockInstance> mbType, World w, Location l, BlockFace dir, boolean flipped, UUID id) {
        super(mbType, w, l, dir, flipped, id);
    }

    public TickableMultiBlock(Class<? extends MultiBlockInstance> type, World w, UUID id, ChunkPos cpos) {
        super(type, w, id, cpos);
    }
    @Override
    public void validate() {
        super.validate();
        if(tickOnInit()) addToTicker();
    }

    public void addToTicker(){
        if(!isTicking) Ticker.add(this);
        isTicking=true;
    }
    public void removeFromTicker(){
        if(isTicking) Ticker.remove(this);
        isTicking=false;
    }

    @Override
    protected void invalidate(MultiBlockChunkDataHandler dat) {
        super.invalidate(dat);
        removeFromTicker();
    }

    @Override
    public void tick() {

    }
    public abstract boolean tickOnInit();
}
