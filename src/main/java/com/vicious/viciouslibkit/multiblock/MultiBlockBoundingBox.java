package com.vicious.viciouslibkit.multiblock;

import com.vicious.viciouslibkit.block.BlockTemplate;
import org.bukkit.Location;
import org.bukkit.block.Block;

public class MultiBlockBoundingBox {
    public int x1,y1,z1;
    public int x2,y2,z2;
    public MultiBlockInstance mbi;

    public MultiBlockBoundingBox(int x1,int y1,int z1, int x2, int y2, int z2){
        this.x1=x1;
        this.x2=x2;
        this.y1=y1;
        this.y2=y2;
        this.z1=z1;
        this.z2=z2;
    }

    /**
     * Checks if l is within the box.
     * @param l
     * @return
     */
    public boolean isWithin(Location l){
        if(l.getBlockX() < x1 && l.getBlockX() > x2) return false;
        if(l.getBlockY() < y1 && l.getBlockY() > y2) return false;
        if(l.getBlockZ() < z1 && l.getBlockZ() > z2) return false;
        return true;
    }
    /**
     * Revalidates the block.
     * @param b
     * @return
     */
    public boolean revalidateAtPos(Block b, BlockTemplate template){
        Location l = b.getLocation();
        return template.get(l.getBlockX()-x1, l.getBlockY()-y1, l.getBlockZ()-z1).matches(b);
    }
    public String toString(){
        return "MBI(" + x1 + "," + y1 + "," + z1 + ")-(" + x2 + "," + y2 + "," + z2 + ")";
    }
}
