package com.vicious.viciouslibkit.block.blockinstance;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class BlockInstanceMaterialOnly extends BlockInstanceSpecial {

    public BlockInstanceMaterialOnly(Material material) {
        super(material);
    }

    @Override
    public boolean matches(Block in) {
        return in.getType() == material;
    }

    @Override
    public boolean matches(BlockInstance in) {
        return in.material == material;
    }

    @Override
    public String toString() {
        return "MO-" + super.toString();
    }

    @Override
    public String verboseInfo() {
        return "A " + material + " block";
    }
}
