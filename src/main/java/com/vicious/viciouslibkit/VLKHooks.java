package com.vicious.viciouslibkit;

import com.vicious.viciouslib.util.reflect.deep.DeepReflection;
import com.vicious.viciouslib.util.reflect.deep.MethodSearchContext;
import com.vicious.viciouslib.util.reflect.deep.TotalFailureException;
import com.vicious.viciouslib.util.reflect.wrapper.ReflectiveMethod;
import com.vicious.viciouslibkit.util.NMSHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.EnumMap;
import java.util.Map;

/**
 * This provides support for shit bukkit doesn't provide. Fuck you Bukkit.
 * God I hate this API ;)
 */
public class VLKHooks {
    /**
     * Burntime Hooks. Vanilla doesn't cache burntime information so we will.
     * Trust me, whoever coded the vanilla mc furnace tile is an absolute idiot.
     */
    public static Map<Material, Integer> fuelStats = new EnumMap<>(Material.class);
    public static int getBurnTime(ItemStack stack){
        Material m = stack.getType();
        Integer ret = fuelStats.get(m);
        if(ret == null){
            addFuelFor(stack);
            ret = fuelStats.get(m);
        }
        return ret;
    }
    private static void addFuelFor(ItemStack stack) {
        Object nmsstack = NMSHelper.CraftItemStack$handle.get(stack);
        if((boolean)NMSHelper.TileEntityFurnace$canUseAsFuel.invoke(NMSHelper.TileEntityFurnace,nmsstack)){
            fuelStats.put(stack.getType(), (Integer) NMSHelper.TileEntityFurnace$getFuelTime.invoke(NMSHelper.TileEntityFurnace,nmsstack));
        }
        else fuelStats.put(stack.getType(),0);
    }
}

