package com.vicious.viciouslibkit.event;

import com.vicious.viciouslibkit.ViciousLibKit;
import com.vicious.viciouslibkit.interfaces.ITickable;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class Ticker {
    private static final Map<ITickable,ITickable> tickables = new IdentityHashMap<>();
    private static final List<ITickable> toAdd = new ArrayList<>();
    private static final List<ITickable> toRemove = new ArrayList<>();
    static{
        Bukkit.getScheduler().scheduleSyncRepeatingTask(ViciousLibKit.INSTANCE,()->{
            try {
                while (!toAdd.isEmpty()) {
                    ITickable tickable = toAdd.remove(0);
                    tickables.putIfAbsent(tickable, tickable);
                }
                while (!toRemove.isEmpty()) {
                    tickables.remove(toRemove.remove(0));
                }
                tickables.forEach((k, v) -> {
                    v.tick();
                });
            } catch (Exception e){
                ViciousLibKit.logger().warning("Exception in ticker: " +  e.getMessage());
                e.printStackTrace();
            }
        },1,1);
    }
    public static void onNextTick(Runnable exec){
        Bukkit.getScheduler().scheduleSyncDelayedTask(ViciousLibKit.INSTANCE,exec,1);
    }
    public static void add(ITickable tickable){
        toAdd.add(tickable);
    }
    public static void remove(ITickable tickable){
        toRemove.add(tickable);
    }
}
