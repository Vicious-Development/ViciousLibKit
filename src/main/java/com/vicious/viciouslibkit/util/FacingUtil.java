package com.vicious.viciouslibkit.util;

import org.bukkit.Axis;
import org.bukkit.Particle;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Slab;

public class FacingUtil {
    public static BlockFace getClockwise90(BlockFace facing) {
        if(facing == BlockFace.EAST){
            return BlockFace.SOUTH;
        }
        else if(facing == BlockFace.SOUTH){
            return BlockFace.WEST;
        }
        else if(facing == BlockFace.WEST){
            return BlockFace.NORTH;
        }
        else if(facing == BlockFace.NORTH){
            return BlockFace.EAST;
        }
        return facing;
    }

    public static BlockFace getCounterClockWise90(BlockFace facing) {
        if(facing == BlockFace.EAST){
            return BlockFace.NORTH;
        }
        else if(facing == BlockFace.NORTH){
            return BlockFace.WEST;
        }
        else if(facing == BlockFace.WEST){
            return BlockFace.SOUTH;
        }
        else if(facing == BlockFace.SOUTH){
            return BlockFace.EAST;
        }
        return facing;
    }

    public static Axis get90Horizontal(Axis axis) {
        if(axis == null) return axis;
        if(axis == Axis.Y) return axis;
        if(axis == Axis.X) return Axis.Y;
        return Axis.X;
    }

    public static BlockFace flipY(BlockFace facing) {
        if(facing == BlockFace.UP) return BlockFace.DOWN;
        else if(facing == BlockFace.DOWN) return BlockFace.UP;
        return facing;
    }

    public static Bisected.Half flipY(Bisected.Half verticalOrientation) {
        if(verticalOrientation == Bisected.Half.BOTTOM) return Bisected.Half.TOP;
        else if(verticalOrientation == Bisected.Half.TOP) return Bisected.Half.BOTTOM;
        return verticalOrientation;
    }

    public static Slab.Type flipSlab(Slab.Type slabType) {
        if(slabType == Slab.Type.TOP) return Slab.Type.BOTTOM;
        else if(slabType == Slab.Type.BOTTOM) return Slab.Type.TOP;
        return slabType;
    }
}
