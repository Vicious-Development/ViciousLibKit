package com.vicious.viciouslibkit.event.piston;

import com.vicious.viciouslibkit.util.vector.WorldPosI;

/**
 * Forces a piston to be sticky. Useful for when server restarts occur.
 */
public class StickyPistonController extends PistonController{
    public StickyPistonController(WorldPosI blockPosition) {
        super(blockPosition);
        isSticky=true;
    }

    @Override
    public boolean isSticky() {
        return true;
    }
}
