package com.vicious.viciouslibkit.block;

import org.bukkit.Material;

import java.util.Objects;

/**
 * Differentiates between a material that is a block.
 * This was mostly important for InventorySearcher's tool finding function.
 *
 * TODO MOVE TO VLK
 */
public class BlockMaterial {
    private Material material;
    public BlockMaterial(Material material){
        this.material = material;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockMaterial that = (BlockMaterial) o;
        return material == that.material;
    }

    @Override
    public int hashCode() {
        return Objects.hash(material);
    }
}
