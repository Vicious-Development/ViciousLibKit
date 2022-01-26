package com.vicious.viciouslibkit.multiblock;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.util.UUID;

@FunctionalInterface
public interface MBConstructor<T extends MultiBlockInstance> {
     T construct(Class<?> mbType, World w, Location l, BlockFace dir, boolean flipped, UUID id);
}
