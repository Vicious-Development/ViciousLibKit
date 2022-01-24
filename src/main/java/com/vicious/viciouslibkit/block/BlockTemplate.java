package com.vicious.viciouslibkit.block;

import java.util.ArrayList;

public class BlockTemplate {
    private ArrayList<ArrayList<ArrayList<BlockInstance>>> instanceList = new ArrayList<>();
    public int maxX;
    public int maxY;
    public int maxZ;
    private int x = 0;
    private int y = 0;
    private int z = 0;
    public BlockTemplate(){}
    /**
     * Inserts the block instance in the x loc
     * @param instances
     * @return
     */
    public BlockTemplate x(BlockInstance... instances) {
        if (y >= instanceList.size()) instanceList.add(new ArrayList<>());
        if (z >= instanceList.get(y).size()) instanceList.get(y).add(new ArrayList<>());
        for (BlockInstance instance : instances) {
            instanceList.get(y).get(z).add(instance);
            maxX = Math.max(x, maxX);
            maxY = Math.max(y, maxY);
            maxZ = Math.max(z, maxZ);
            x++;
        }
        return this;
    }
    public BlockTemplate x(int i, BlockInstance instance){
        for (int j = 0; j < i; j++) {
            x(instance);
        }
        return this;
    }

    /**
     * Increases the y index by 1.
     * @return
     */
    public BlockTemplate y(){
        y++;
        x=0;
        z=0;
        return this;
    }
    public BlockTemplate z(){
        z++;
        x=0;
        return this;
    }
    public static BlockTemplate start(){
        return new BlockTemplate();
    }

    public BlockTemplate air(int i) {
        x(i,BlockInstance.AIR);
        return this;
    }

    public BlockTemplate any(int i) {
        x(i,null);
        return this;
    }
    public BlockTemplate copyY(int i){
        for (int j = 0; j < i; j++) {
            instanceList.add(instanceList.get(y));
        }
        y+=i;
        return this;
    }

    public BlockInstance get(int x, int y, int z) {
        if(instanceList.size() <= y) return null;
        if(instanceList.get(y) == null) return null;
        if(instanceList.get(y).size() <= z) return null;
        if(instanceList.get(y).get(z) == null) return null;
        if(instanceList.get(y).get(z).size() <= x) return null;
        return instanceList.get(y).get(z).get(x);
    }
}
