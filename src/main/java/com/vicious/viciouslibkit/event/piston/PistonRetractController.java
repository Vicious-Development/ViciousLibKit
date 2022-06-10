package com.vicious.viciouslibkit.event.piston;

import com.vicious.viciouslibkit.util.vector.WorldPosI;
import org.bukkit.event.block.BlockPistonRetractEvent;

/**
 * Cancels or allows piston retract events.
 */
public class PistonRetractController extends PistonController<BlockPistonRetractEvent>{
    private boolean doRetract = true;
    public PistonRetractController(WorldPosI blockPosition) {
        super(blockPosition);
    }
    public void haltRetract(){
        this.doRetract=false;
    }
    public void allowRetract(){
        this.doRetract=true;
    }

    @Override
    public void attemptRetract() {
        allowRetract();
        super.attemptRetract();
    }

    /**
     * Disables retraction and extends the piston if it is not already.
     */
    public void keepExtended(){
        haltRetract();
        attemptPush();
    }

    @Override
    public void accept(BlockPistonRetractEvent blockPistonRetractEvent) {
        this.direction=blockPistonRetractEvent.getDirection();
        blockPistonRetractEvent.setCancelled(!doRetract);
    }
}
