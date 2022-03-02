package com.vicious.viciouslibkit.block;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

public class MultipleBlockInstances extends BlockInstance{
    private List<BlockInstance> instances = new ArrayList<>();
    public MultipleBlockInstances() {
        super(Material.OBSIDIAN);
    }
    public MultipleBlockInstances add(BlockInstance i){
        instances.add(i);
        return this;
    }
    public boolean matches(Block in){
        for (BlockInstance instance : instances) {
            if(instance.matches(in)) return true;
        }
        return false;
    }
    public MultipleBlockInstances rotateCounterClockwise(){
        List<BlockInstance> instancesnew = new ArrayList<>();
        for (BlockInstance instance : instances) {
            instancesnew.add(instance.rotateCounterClockwise());
        }
        MultipleBlockInstances newi = new MultipleBlockInstances();
        newi.instances=instancesnew;
        return newi;
    }
    public MultipleBlockInstances rotateClockwise(){
        List<BlockInstance> instancesnew = new ArrayList<>();
        for (BlockInstance instance : instances) {
            instancesnew.add(instance.rotateClockwise());
        }
        MultipleBlockInstances newi = new MultipleBlockInstances();
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
