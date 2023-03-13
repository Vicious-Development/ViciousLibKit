package com.vicious.viciouslibkit.item;

import java.util.HashMap;
import java.util.Map;

public class CustomItemRegistry {
    private static final Map<String,ICustomItem> customItems = new HashMap<>();
    public static void register(ICustomItem item){
        if(!isRegistered(item.key())){
            customItems.put(item.key(),item);
        }
    }
    public static void unregister(String key){
        if(getCustomItem(key) != null){
            customItems.remove(key);
        }
    }
    public static void unregister(ICustomItem item){
        customItems.remove(item.key());
    }
    public static boolean isRegistered(String key){
        return customItems.containsKey(key);
    }
    public static ICustomItem getCustomItem(String key){
        return customItems.get(key);
    }

}
