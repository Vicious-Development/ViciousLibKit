package com.vicious.viciouslibkit.util;

import com.vicious.viciouslib.database.objectTypes.SQLVector3i;
import com.vicious.viciouslibkit.block.BlockInstance;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class LibKitUtil {
    /**
     * Also known as 90 counter clockwise around 0,0.
     */
    public static SQLVector3i rotateQuad2(SQLVector3i v){
        return new SQLVector3i(v.z,v.y,-v.x);
    }

    /**
     * Also known as 90 clockwise around 0,0.
     */
    public static SQLVector3i rotateQuad4(SQLVector3i v){
        return new SQLVector3i(-v.z,v.y,v.x);
    }


    /**
     * Vector math functions tha are mainly used to rotate a vector's position while keeping it in quad 1.
     */

    /**
     * Rotates the vector by 90 degrees into quadrant 2 and then translates it into quad1.
     * @return
     */
    public static SQLVector3i rotateQuad2TranslateQuad1(SQLVector3i vec, int q1Height){
        return new SQLVector3i(-vec.z+q1Height,vec.y,vec.x);
    }
    /**
     * Rotates the vector by 180 degrees into quadrant 3 and then translates it into quad1.
     * @return
     */
    public static SQLVector3i rotateQuad3TranslateQuad2(SQLVector3i vec, int q1Width, int q1Height){
        return new SQLVector3i(-vec.x+q1Width,vec.y,-vec.z+q1Height);
    }
    /**
     * Rotates the vector by 90 degrees into quadrant 4 and then translates it into quad1.
     * @return
     */
    public static SQLVector3i rotateQuad4TranslateQuad1(SQLVector3i vec, int q1Width){
        return new SQLVector3i(vec.z,vec.y,-vec.x+q1Width);
    }

    public static Vector zeroVelocity(){
        return new Vector(0,0,0);
    }
}
