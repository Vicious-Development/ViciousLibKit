package com.vicious.viciouslibkit.data;

public class DataTypeNotFoundException extends RuntimeException {
    public DataTypeNotFoundException(){
        super();
    }
    public DataTypeNotFoundException(String msg){
        super(msg);
    }
}
