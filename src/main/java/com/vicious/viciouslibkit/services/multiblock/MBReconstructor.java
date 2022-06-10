package com.vicious.viciouslibkit.services.multiblock;

import com.vicious.viciouslibkit.data.provided.multiblock.MultiBlockInstance;
import com.vicious.viciouslibkit.util.vector.ChunkPos;
import org.bukkit.World;

import java.util.UUID;

@FunctionalInterface
public interface MBReconstructor<T extends MultiBlockInstance> {
    T construct(Class<? extends MultiBlockInstance> mbType, World w, UUID id, ChunkPos pos);
}
