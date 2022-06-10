package com.vicious.viciouslibkit.util.nms;

import com.vicious.viciouslib.util.reflect.deep.TotalFailureException;

import java.util.ArrayList;
import java.util.List;

/**
 * Allows us to access vanilla registries.
 * You need to use a MinecraftKey for this to work.
 */
public class NMSRegistryUtility {
    private static List<Object> registries = new ArrayList<>();
    public static int getRegistryContaining(Object minecraftKey) {
        if(!NMSHelper.MinecraftKey.isInstance(minecraftKey)) throw new IllegalArgumentException("This is a reflective method! Please obtain a MinecraftKey Object from somewhere! You provided a: " + minecraftKey.getClass());
        try {
            return NMSHelper.forAllFields(NMSHelper.IRegistry,(f)->{
                if(NMSHelper.IRegistry == f.getType()){
                    try {
                        f.setAccessible(true);
                        Object reg = f.get(NMSHelper.IRegistry);
                        try{
                            //There is another method "getOrThrow" that does the same thing as this but throws an exception so I put this in here to be safe.
                            if(NMSHelper.IRegistry$get.invoke(reg,minecraftKey) != null){
                                registries.add(reg);
                                return registries.size()-1;
                            }
                        } catch (Exception e){
                            return -1;
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                return -1;
            });
        } catch (TotalFailureException e) {
            return -1;
        }
    }

    /**
     * You need to get a registryID by using the method getRegistryContaining. Please cache this value so you have quick access to it.
     */
    public static Object get(int registryID, Object key){
        return NMSHelper.IRegistry$get.invoke(registries.get(registryID),key);
    }

    public static Object getRegistryObjectForKey(Object key){
        return registries.get(getRegistryContaining(key));
    }

    public static Object get(Object registry, Object key){
        return NMSHelper.IRegistry$get.invoke(registry,key);
    }
}
