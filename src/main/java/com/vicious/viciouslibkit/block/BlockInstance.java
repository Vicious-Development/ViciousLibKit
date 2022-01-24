package com.vicious.viciouslibkit.block;

import com.vicious.viciouslibkit.util.FacingUtil;
import org.bukkit.Axis;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.*;
import org.bukkit.block.data.type.Slab;

public class BlockInstance {
    public static final BlockInstance ENDSTONE = new BlockInstance(Material.END_STONE);
    public static final BlockInstance ENDSTONEBRICK = new BlockInstance(Material.END_STONE_BRICKS);
    public static final BlockInstance AIR = new BlockInstance(Material.AIR);
    private BlockFace facing;
    private Material material;
    private Bisected.Half verticalOrientation;
    private boolean waterLogged;
    private Axis axis;
    private Slab.Type slabType;

    public BlockInstance(Material material){
        this.material=material;
    }
    public BlockInstance(Block b){
        this.material=b.getType();
        BlockData dat = b.getBlockData();
        if(dat instanceof Directional){
            facing=((Directional) dat).getFacing();
        }
        if(dat instanceof Bisected){
            verticalOrientation=((Bisected) dat).getHalf();
        }
        if(dat instanceof Waterlogged){
            waterLogged =((Waterlogged) dat).isWaterlogged();
        }
        if(dat instanceof Orientable){
            axis=((Orientable) dat).getAxis();
        }
        if(dat instanceof Slab){
            slabType=((Slab) dat).getType();
        }
    }
    public BlockInstance facing(BlockFace face){
        facing=face;
        return this;
    }
    public BlockInstance vOrientation(Bisected.Half half){
        verticalOrientation=half;
        return this;
    }
    public BlockInstance waterLogged(boolean isWaterLogged){
        waterLogged =isWaterLogged;
        return this;
    }
    public BlockInstance slabType(Slab.Type type){
        this.slabType = type;
        return this;
    }
    public boolean matches(Block in){
        if(material != in.getType()) return false;
        BlockData dat = in.getBlockData();
        if(dat instanceof Directional){
            if(facing != null && facing != ((Directional) dat).getFacing()) return false;
        }
        if(dat instanceof Bisected){
            if(verticalOrientation != null && verticalOrientation != ((Bisected) dat).getHalf()) return false;
        }
        if(dat instanceof Waterlogged){
            if(waterLogged != ((Waterlogged) dat).isWaterlogged()) return false;
        }
        if(dat instanceof Orientable){
            if(axis != ((Orientable) dat).getAxis()) return false;
        }
        if(dat instanceof Slab){
            if(slabType != ((Slab) dat).getType()) return false;
        }
        return true;
    }
    public BlockInstance rotateLeft(){
        return new BlockInstance(material).facing(FacingUtil.getPerpendicularLeft(facing));
    }

    public BlockInstance orientation(Axis axis) {
        this.axis=axis;
        return this;
    }
    public String toString(){
        return "BInstance: " + material + "\nblockface=" + facing + "\naxis=" + axis + "\nwaterlogged=" + waterLogged + "\nslabtype=" + slabType + "\nvorientation=" + verticalOrientation;
    }

    public String verboseInfo() {
        String ret = material.toString();
        if(facing != null) ret += " facing " + facing.name();
        if(axis != null) ret += " on the " + axis.name() + " axis";
        if(slabType != null){
            if(slabType == Slab.Type.BOTTOM) ret += " downside up";
            else if(slabType == Slab.Type.TOP) ret += " upside down";
            else ret += " double slab";
        }
        if(verticalOrientation != null){
            if(verticalOrientation == Bisected.Half.TOP) ret += " upside down";
            else ret += " downside up";
        }
        if(waterLogged) ret += " underwater";
        ret += ".";
        return ret;
    }
}