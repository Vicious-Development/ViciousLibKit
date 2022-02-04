package com.vicious.viciouslibkit.services.multiblock;

import org.bukkit.block.BlockFace;

public class MultiBlockState {
    public long count;
    private State state;
    private String message;
    public BlockFace orientation;
    public boolean flipped = false;
    public MultiBlockState(State state, String message, long count){
        this.state=state;
        this.message=message;
        this.count=count;
    }
    public MultiBlockState facing(BlockFace face){
        orientation = face;
        return this;
    }
    public MultiBlockState flip(boolean b){
        flipped=b;
        return this;
    }
    public String toString(){
        return "The multiblock is: " + state + " " + (message != null ? message : "");
    }

    public boolean isValid() {
        return state == State.VALID;
    }

    public enum State{
        INVALID,
        VALID
    }
}
