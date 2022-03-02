package com.vicious.viciouslibkit.block.blockinstance;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class BlockInstanceMultiple extends BlockInstance{
    private List<BlockInstance> instances = new ArrayList<>();
    public BlockInstanceMultiple() {
        super(Material.OBSIDIAN);
    }
    public BlockInstanceMultiple add(BlockInstance i){
        instances.add(i);
        return this;
    }
    public boolean matches(Block in){
        for (BlockInstance instance : instances) {
            if(instance.matches(in)) return true;
        }
        return false;
    }
    public BlockInstanceMultiple rotateCounterClockwise(){
        List<BlockInstance> instancesnew = new ArrayList<>();
        for (BlockInstance instance : instances) {
            instancesnew.add(instance.rotateCounterClockwise());
        }
        BlockInstanceMultiple newi = new BlockInstanceMultiple();
        newi.instances=instancesnew;
        return newi;
    }
    public BlockInstanceMultiple rotateClockwise(){
        List<BlockInstance> instancesnew = new ArrayList<>();
        for (BlockInstance instance : instances) {
            instancesnew.add(instance.rotateClockwise());
        }
        BlockInstanceMultiple newi = new BlockInstanceMultiple();
        newi.instances=instancesnew;
        return newi;
    }
    public String toString(){
        return "MultipleBInstances: " + instances.toString();
    }

    public String verboseInfo() {
        String ret = "Any of: ";
        for (int i = 0; i < instances.size(); i++) {
            BlockInstance instance = instances.get(i);
            ret += instance.material;
            if(i != instances.size()-1){
                ret += ",";
            }
        }
        return ret;
    }
}
