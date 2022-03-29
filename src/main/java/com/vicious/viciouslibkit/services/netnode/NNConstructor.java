package com.vicious.viciouslibkit.services.netnode;

import com.vicious.viciouslibkit.data.provided.netnode.NetNode;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

@FunctionalInterface
public interface NNConstructor<T extends NetNode> {
     T construct(Class<? extends NetNode> nnType, World w, Location l, UUID id);
}
