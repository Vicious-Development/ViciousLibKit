package com.vicious.viciouslibkit.util;

import com.vicious.viciouslib.util.reflect.deep.DeepReflection;
import com.vicious.viciouslib.util.reflect.deep.MethodSearchContext;
import com.vicious.viciouslib.util.reflect.deep.TotalFailureException;
import com.vicious.viciouslib.util.reflect.wrapper.ReflectiveField;
import com.vicious.viciouslib.util.reflect.wrapper.ReflectiveMethod;
import com.vicious.viciouslibkit.ViciousLibKit;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;

public class NMSHelper {
    //NMS
    public static Class<?> World = DeepReflection.get("World","net.minecraft");
    public static Class<?> GeneratorAccess = DeepReflection.get("GeneratorAccess","net.minecraft");
    public static Class<?> BlockPosition = DeepReflection.get("BlockPosition","net.minecraft");
    public static Class<?> Block = DeepReflection.get("Block","net.minecraft");
    public static Class<?> BlockPiston = DeepReflection.get("BlockPiston","net.minecraft");
    public static Class<?> IBlockData = DeepReflection.get("IBlockData","net.minecraft");
    public static Class EnumDirection = DeepReflection.get("EnumDirection","net.minecraft");
    //BUKKIT
    public static Class<?> CraftBlock = DeepReflection.get("CraftBlock","org.bukkit");
    //Methods
    public static ReflectiveMethod CraftBlock$getNMS;
    public static ReflectiveMethod CraftBlock$getPosition;
    public static ReflectiveMethod IBlockData$getBlock;
    public static ReflectiveMethod BlockPiston$push;
    //Fields
    public static ReflectiveField CraftWorld$world = new ReflectiveField("world");


    static {
        try {
            MethodSearchContext ctx = new MethodSearchContext().accepts().returns(Block)
                    .after(new MethodSearchContext().returns(void.class).accepts(World,BlockPosition,IBlockData,boolean.class))
                    .before(new MethodSearchContext().returns(void.class).accepts(GeneratorAccess,BlockPosition,int.class,int.class));
            IBlockData$getBlock = DeepReflection.getMethod(IBlockData,ctx);
            ctx = new MethodSearchContext().accepts(World, BlockPosition, EnumDirection, boolean.class).returns(boolean.class);
            BlockPiston$push = DeepReflection.getMethod(BlockPiston,ctx);
            CraftBlock$getNMS = DeepReflection.getMethod(CraftBlock,new MethodSearchContext().name("getNMS"));
            CraftBlock$getPosition = DeepReflection.getMethod(CraftBlock,new MethodSearchContext().name("getPosition"));
        } catch (TotalFailureException e) {
            ViciousLibKit.LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setExtended(Block piston, boolean extended) throws Exception {
        Object nmsblockdata = CraftBlock$getNMS.invoke(piston);
        Object nmsblock = IBlockData$getBlock.invoke(nmsblockdata);
        Enum<?> direction = Enum.valueOf(EnumDirection,((Directional) piston.getBlockData()).getFacing().name());
        BlockPiston$push.invoke(nmsblock,CraftWorld$world.get(piston.getWorld()),CraftBlock$getPosition.invoke(piston),direction,extended);
    }
}
