package com.vicious.viciouslibkit.block;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import com.vicious.viciouslibkit.util.LibKitUtil;
import org.bukkit.block.BlockFace;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class BlockTemplate {
    private ArrayList<ArrayList<ArrayList<BlockInstance>>> instanceList = new ArrayList<>();
    public int maxX = 0;
    public int maxY = 0;
    public int maxZ = 0;
    private int x = 0;
    private int y = 0;
    private int z = 0;
    private SQLVector3i startPos;

    public BlockTemplate(){}
    /**
     * Inserts the block instance in the x loc
     * @param instances
     * @return
     */
    public BlockTemplate x(BlockInstance... instances) {
        while (y >= instanceList.size()){
            instanceList.add(new ArrayList<>());
        }
        while (z >= instanceList.get(y).size()) {
            instanceList.get(y).add(new ArrayList<>());
        }
        for (BlockInstance instance : instances) {
            instanceList.get(y).get(z).add(instance);
            maxX = Math.max(x,maxX);
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
        maxY = Math.max(y, maxY);
        y++;
        x=0;
        z=0;
        return this;
    }
    public BlockTemplate z(){
        maxZ = Math.max(z, maxZ);
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
        maxY = Math.max(y,maxY);
        return this;
    }

    public BlockInstance get(int x, int y, int z) {
        if(x < 0 || y < 0 || z < 0) return null;
        if(instanceList.size() <= y) return null;
        if(instanceList.get(y) == null) return null;
        if(instanceList.get(y).size() <= z) return null;
        if(instanceList.get(y).get(z) == null) return null;
        if(instanceList.get(y).get(z).size() <= x) return null;
        return instanceList.get(y).get(z).get(x);
    }
    public BlockTemplate rotateCounterClockwise90(){
        BlockTemplate template = BlockTemplate.start();
        for(int y = 0; y <= maxY; y++){
            for(int x = maxX; x >= 0; x--){
                for(int z = 0; z <= maxZ; z++){
                    BlockInstance i = get(x,y,z);
                    if(i != null) template.x(i.rotateCounterClockwise());
                    else template.x(i);
                }
                template.z();
            }
            template.y();
        }
        //We use templates in quad4, this is the quad1 equivalent math function.
        template.startPos = LibKitUtil.rotateQuad4TranslateQuad1(startPos,maxX);
        return template;
    }
    public BlockTemplate rotateClockwise90(){
        BlockTemplate template = BlockTemplate.start();
        for(int y = 0; y <= maxY; y++){
            for(int x = 0; x <= maxX; x++){
                for(int z = maxZ; z >= 0; z--){
                    BlockInstance i = get(x,y,z);
                    if(i != null) template.x(i.rotateClockwise());
                    else template.x(i);
                }
                template.z();
            }
            template.y();
        }
        //We use templates in quad4, this is the quad1 equivalent math function.
        template.startPos = LibKitUtil.rotateQuad2TranslateQuad1(startPos,maxZ);
        return template;
    }
    //TODO: optimize.
    public BlockTemplate rotate180(){
        BlockTemplate temp = rotateClockwise90().rotateClockwise90();
        temp.startPos = LibKitUtil.rotateQuad3TranslateQuad2(startPos,maxX,maxZ);
        return temp;
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
        this.startPos = new SQLVector3i(x,y,z);
        return this;
    }
    public String allOrientations(){
        String s = "";
        s += "\nNORTH";
        s += rotate(BlockFace.NORTH);
        s += "\nNORTH FLIPPED";
        s += rotate(BlockFace.NORTH).flipX();
        s += "\nSOUTH";
        s += rotate(BlockFace.SOUTH);
        s += "\nSOUTH FLIPPED";
        s += rotate(BlockFace.SOUTH).flipX();
        s += "\nEAST";
        s += rotate(BlockFace.EAST);
        s += "\nEAST FLIPPED";
        s += rotate(BlockFace.EAST).flipX();
        s += "\nWEST";
        s += rotate(BlockFace.WEST);
        s += "\nWEST FLIPPED";
        s += rotate(BlockFace.WEST).flipX();
        return s;
    }
    public String toString(){
        String s = "";
        int y = 0;
        for (ArrayList<ArrayList<BlockInstance>> ylist : instanceList) {
            s +=  "\nL" + y;
            int z = 0;
            for (ArrayList<BlockInstance> zlist : ylist) {
                s+= "\n|";
                int xx = 0;
                for (BlockInstance x : zlist) {
                    if(z == startPos.z && y == startPos.y && xx == startPos.x){
                        if(x == null) s+="-}";
                        else s += x.material.name().charAt(0) + "}";
                    }
                    else {
                        if (x == null) s += "-|";
                        else s += x.material.name().charAt(0) + "|";
                    }
                    xx++;
                }
                z++;
            }
            y++;
        }
        return s;
    }

    public Vector getOriginTranslation() {
        return new Vector(-startPos.x,-startPos.y,-startPos.z);
    }

    public BlockTemplate flipX() {
        BlockTemplate template = BlockTemplate.start();
        for(int y = 0; y <= maxY; y++){
            for(int z = 0; z <= maxZ; z++){
                for (int x = maxX; x >= 0; x--){
                    BlockInstance i = get(x,y,z);
                    if(i != null) template.x(i.rotateClockwise());
                    else template.x(i);
                }
                template.z();
            }
            template.y();
        }
        template.startPos = new SQLVector3i(-startPos.x+maxX,startPos.y,startPos.z);
        return template;
    }
}
