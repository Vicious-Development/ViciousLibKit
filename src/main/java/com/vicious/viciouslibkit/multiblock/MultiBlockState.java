package com.vicious.viciouslibkit.multiblock;

public class MultiBlockState {
    private State state;
    private String message;
    public MultiBlockState(State state, String message){
        this.state=state;
        this.message=message;
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
