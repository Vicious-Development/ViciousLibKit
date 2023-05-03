package com.vicious.viciouslibkit.util.nms;

import com.google.common.collect.Lists;
import com.vicious.viciouslib.util.reflect.deep.DeepReflection;
import com.vicious.viciouslib.util.reflect.deep.FieldSearchContext;
import com.vicious.viciouslib.util.reflect.deep.MethodSearchContext;
import com.vicious.viciouslib.util.reflect.deep.TotalFailureException;
import com.vicious.viciouslib.util.reflect.wrapper.ReflectiveField;
import com.vicious.viciouslib.util.reflect.wrapper.ReflectiveMethod;
import com.vicious.viciouslibkit.ViciousLibKit;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.function.Function;

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
    public static Class<?> TileEntityComparator = DeepReflection.get("TileEntityComparator","net.minecraft");
    //BUKKIT
    public static Class<?> CraftBlock = DeepReflection.get("CraftBlock","org.bukkit");
    public static Class<?> CraftItemStack = DeepReflection.get("CraftItemStack","org.bukkit");
    public static Class<?> CraftBlockEntityState = DeepReflection.get("CraftBlockEntityState","org.bukkit");
    public static ReflectiveMethod TileEntityComparator$setOutputSignal;
    //Methods
    public static ReflectiveMethod CraftBlock$getNMS;
    public static ReflectiveMethod CraftBlock$getPosition;
    public static ReflectiveMethod IBlockData$getBlock;
    public static ReflectiveMethod BlockPiston$push;
    public static ReflectiveMethod IRegistry$get;
    public static ReflectiveMethod TileEntityFurnace$canUseAsFuel;
    public static ReflectiveMethod TileEntityFurnace$createFuelTimeMap;
    public static ReflectiveMethod ItemStack$getItem;
    public static ReflectiveMethod CraftBlockEntity$refreshSnapshot;
    public static ReflectiveMethod BlockPiston$checkIfExtend;

    //Fields
    public static ReflectiveField CraftWorld$world = new ReflectiveField("world");
    public static ReflectiveField CraftItemStack$handle = new ReflectiveField("handle");
    public static ReflectiveField CraftBlockEntity$tileEntity = new ReflectiveField("tileEntity");
    public static ReflectiveField BlockPiston$isSticky;

    public static void init() {
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
            CraftBlockEntity$refreshSnapshot = DeepReflection.getMethod(CraftBlockEntityState,new MethodSearchContext().name("refreshSnapshot"));
            TileEntityComparator$setOutputSignal = DeepReflection.getMethod(TileEntityComparator,new MethodSearchContext().accepts(int.class).returns(void.class).exceptions());
            BlockPiston$checkIfExtend = DeepReflection.getMethod(NMSHelper.BlockPiston, new MethodSearchContext().accepts(NMSHelper.World,NMSHelper.BlockPosition,NMSHelper.IBlockData));
            BlockPiston$isSticky = DeepReflection.getField(BlockPiston,new FieldSearchContext().type(boolean.class).withAccess(Lists.newArrayList(Modifier::isFinal, Modifier::isPrivate)));
        } catch (TotalFailureException e) {
            ViciousLibKit.LOGGER.severe(e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setExtended(Block piston, boolean extended) {
        Object nmsblockdata = CraftBlock$getNMS.invoke(piston);
        Object nmsblock = IBlockData$getBlock.invoke(nmsblockdata);
        Enum<?> direction = Enum.valueOf(EnumDirection,((Directional) piston.getBlockData()).getFacing().name());
        BlockPiston$push.invoke(nmsblock,CraftWorld$world.get(piston.getWorld()),CraftBlock$getPosition.invoke(piston),direction,extended);
    }
    public static void updatePiston(Block pistonBase, org.bukkit.World world) {
        Object nmsblockdata = NMSHelper.CraftBlock$getNMS.invoke(pistonBase);
        Object nmsblock = NMSHelper.IBlockData$getBlock.invoke(nmsblockdata);
        Object nmsworld = NMSHelper.CraftWorld$world.get(world);
        Object nmsblockposition = NMSHelper.CraftBlock$getPosition.invoke(pistonBase);
        BlockPiston$checkIfExtend.invoke(nmsblock,nmsworld, nmsblockposition, nmsblockdata);
    }
    public static void convertSticky(Block pistonBase){
        Object nmsblockdata = NMSHelper.CraftBlock$getNMS.invoke(pistonBase);
        BlockPiston$isSticky.set(nmsblockdata,true);
    }

    public static <T> T forAllFields(Class<?> target, Function<Field,T> func) throws TotalFailureException {
        for (Field declaredField : target.getDeclaredFields()) {
            T ret = func.apply(declaredField);
            if(ret != null) return ret;
        }
        throw new TotalFailureException("No object found to return using a custom search function.");
    }
}
