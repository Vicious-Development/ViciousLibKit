package com.vicious.viciouslibkit.util;

import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Stairs;

public class FacingUtil {
    public static BlockFace getPerpendicularLeft(BlockFace facing) {
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
}
