package com.vicious.viciouslibkit.block;

import org.bukkit.block.BlockFace;

import java.util.ArrayList;

public class BlockTemplate {
    private ArrayList<ArrayList<ArrayList<BlockInstance>>> instanceList = new ArrayList<>();
    public int maxX;
    public int maxY;
    public int maxZ;
    private int x = 0;
    private int y = 0;
    private int z = 0;
    public int startX = 0;
    public int startY = 0;
    public int startZ = 0;
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
    public BlockTemplate rotateCounterClockwise90(){
        BlockTemplate template = BlockTemplate.start();
        for (int i = 0; i < maxY && i < instanceList.size(); i++) {
            ArrayList<ArrayList<BlockInstance>> zlist = instanceList.get(i);
            for(int x = 0; x < maxX; x++){
                for (ArrayList<BlockInstance> xlist : zlist) {
                    if (x >= xlist.size()) template.any(1);
                    else template.x(xlist.get(x).rotateCounterClockwise());
                }
                template.z();
            }
            template.y();
        }
        return template;
    }
    public BlockTemplate rotateClockwise90(){
        BlockTemplate template = BlockTemplate.start();
        for (int i = 0; i < maxY && i < instanceList.size(); i++) {
            ArrayList<ArrayList<BlockInstance>> zlist = instanceList.get(i);
            for(int x = maxX; x >= 0; x--){
                for (ArrayList<BlockInstance> xlist : zlist) {
                    if (x >= xlist.size()) template.any(1);
                    else template.x(xlist.get(x).rotateClockwise());
                }
                template.z();
            }
            template.y();
        }
        return template;
    }
    //TODO: optimize.
    public BlockTemplate rotate180(){
        return rotateClockwise90().rotateClockwise90();
    }
    public BlockTemplate rotate(BlockFace dir) {
        if (dir == BlockFace.NORTH) {
            return this;
        } else if (dir == BlockFace.WEST) {
            return rotateCounterClockwise90();
        } else if (dir == BlockFace.EAST) {
            return rotateClockwise90();
        } else {
            return rotate180();
        }
    }
    public BlockTemplate finish(int x, int y, int z){
        this.startX=x;
        this.startY=y;
        this.startZ=z;
        return this;
    }
}
