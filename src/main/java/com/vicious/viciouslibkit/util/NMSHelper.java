package com.vicious.viciouslibkit.util;

import com.google.common.collect.Lists;
import com.vicious.viciouslib.util.reflect.deep.DeepReflection;
import com.vicious.viciouslib.util.reflect.deep.MethodSearchContext;
import com.vicious.viciouslib.util.reflect.deep.TotalFailureException;
import com.vicious.viciouslib.util.reflect.wrapper.ReflectiveField;
import com.vicious.viciouslib.util.reflect.wrapper.ReflectiveMethod;
import com.vicious.viciouslibkit.ViciousLibKit;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;

import java.lang.reflect.Modifier;
import java.util.Map;

public class NMSHelper {
    //NMS
    public static Class<?> TileEntityFurnace = DeepReflection.get("TileEntityFurnace","net.minecraft");
    public static Class<?> ItemStack = DeepReflection.get("ItemStack","net.minecraft");
    public static Class<?> Item = DeepReflection.get("Item","net.minecraft");
    public static Class<?> World = DeepReflection.get("World","net.minecraft");
    public static Class<?> IRegistry = DeepReflection.get("IRegistry","net.minecraft");
    public static Class<?> MinecraftKey = DeepReflection.get("MinecraftKey","net.minecraft");
    public static Class<?> BlockPosition = DeepReflection.get("BlockPosition","net.minecraft");
    public static Class<?> Block = DeepReflection.get("Block","net.minecraft");
    public static Class<?> BlockPiston = DeepReflection.get("BlockPiston","net.minecraft");
    public static Class<?> IBlockData = DeepReflection.get("IBlockData","net.minecraft");
    public static Class EnumDirection = DeepReflection.get("EnumDirection","net.minecraft");
    //BUKKIT
    public static Class<?> CraftBlock = DeepReflection.get("CraftBlock","org.bukkit");
    public static Class<?> CraftItemStack = DeepReflection.get("CraftItemStack","org.bukkit");
    //Methods
    public static ReflectiveMethod CraftBlock$getNMS;
    public static ReflectiveMethod CraftBlock$getPosition;
    public static ReflectiveMethod IBlockData$getBlock;
    public static ReflectiveMethod BlockPiston$push;
    public static ReflectiveMethod IRegistry$get;
    public static ReflectiveMethod TileEntityFurnace$canUseAsFuel;
    public static ReflectiveMethod TileEntityFurnace$createFuelTimeMap;
    public static ReflectiveMethod ItemStack$getItem;
    //Fields
    public static ReflectiveField CraftWorld$world = new ReflectiveField("world");
    public static ReflectiveField CraftItemStack$handle = new ReflectiveField("handle");


    static {
        try {
            MethodSearchContext ctx = new MethodSearchContext().accepts().returns(Block);
            IBlockData$getBlock = DeepReflection.getMethod(IBlockData,ctx);
            ctx = new MethodSearchContext().accepts(World, BlockPosition, EnumDirection, boolean.class).returns(boolean.class);
            BlockPiston$push = DeepReflection.getMethod(BlockPiston,ctx);
            CraftBlock$getNMS = DeepReflection.getMethod(CraftBlock,new MethodSearchContext().name("getNMS"));
            CraftBlock$getPosition = DeepReflection.getMethod(CraftBlock,new MethodSearchContext().name("getPosition"));
            IRegistry$get = DeepReflection.getMethod(IRegistry,new MethodSearchContext().accepts(MinecraftKey).returns(Object.class).exceptions());
            TileEntityFurnace$canUseAsFuel = DeepReflection.getMethod(TileEntityFurnace, new MethodSearchContext().returns(boolean.class).accepts(ItemStack).exceptions().withAccess(Lists.newArrayList(Modifier::isStatic, Modifier::isPublic)));
            TileEntityFurnace$createFuelTimeMap = DeepReflection.getMethod(TileEntityFurnace, new MethodSearchContext().returns(Map.class).accepts().exceptions().withAccess(Lists.newArrayList(Modifier::isStatic, Modifier::isPublic)));
            ItemStack$getItem = DeepReflection.getMethod(ItemStack, new MethodSearchContext().returns(Item).accepts().exceptions().withAccess(Lists.newArrayList(Modifier::isPublic)));
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
