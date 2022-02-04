package com.vicious.viciouslibkit.services.netnode;

import com.vicious.viciouslibkit.data.provided.multiblock.MultiBlockInstance;
import com.vicious.viciouslibkit.data.provided.netnode.NetNode;
import com.vicious.viciouslibkit.util.ChunkPos;
import org.bukkit.World;

import java.util.UUID;

@FunctionalInterface
public interface NNReconstructor<T extends NetNode> {
    T construct(Class<? extends NetNode> type, World w, UUID id, ChunkPos cpos);
}
