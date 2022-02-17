package com.vicious.viciouslibkit.event;

import com.vicious.viciouslibkit.ViciousLibKit;
import com.vicious.viciouslibkit.interfaces.ITickable;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class Ticker {
    private static final List<ITickable> tickables = new ArrayList<>();
    private static final List<ITickable> toAdd = new ArrayList<>();
    private static final List<ITickable> toRemove = new ArrayList<>();
    static{
        Bukkit.getScheduler().scheduleSyncRepeatingTask(ViciousLibKit.INSTANCE,()->{
            while(!toRemove.isEmpty()){
                tickables.remove(toRemove.remove(0));
            }
            while(!toAdd.isEmpty()){
                tickables.add(toAdd.remove(0));
            }
            for (ITickable tickable : tickables) {
                tickable.tick();
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
        toAdd.remove(tickable);
    }
}
