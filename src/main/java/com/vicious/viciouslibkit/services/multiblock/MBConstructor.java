package com.vicious.viciouslibkit.services.multiblock;

import com.vicious.viciouslibkit.data.provided.multiblock.MultiBlockInstance;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.util.UUID;

@FunctionalInterface
public interface MBConstructor<T extends MultiBlockInstance> {
     T construct(Class<? extends MultiBlockInstance> mbType, World w, Location l, BlockFace dir, boolean flipped, boolean flippedVertical, UUID id);
}
