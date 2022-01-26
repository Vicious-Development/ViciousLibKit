package com.vicious.viciouslibkit.multiblock;

import com.vicious.viciouslibkit.util.ChunkPos;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.BlockFace;

import java.util.UUID;

@FunctionalInterface
public interface MBReconstructor<T extends MultiBlockInstance> {
    T construct(Class<?> mbType, World w, UUID id, ChunkPos pos);
}
