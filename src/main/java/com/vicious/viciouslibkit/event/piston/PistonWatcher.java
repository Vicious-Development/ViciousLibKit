package com.vicious.viciouslibkit.event.piston;

import com.vicious.viciouslibkit.util.vector.WorldPosI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


/**
 * Allows the control of pistons using PistonControllers
 */
public class PistonWatcher implements Listener {
    private static final Map<WorldPosI, List<Consumer<BlockPistonEvent>>> handlers = new HashMap<>();
    @EventHandler(priority = EventPriority.LOWEST)
    public void onRetract(BlockPistonRetractEvent ev){
        pistonEvent(ev);
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onExtend(BlockPistonExtendEvent ev){
        pistonEvent(ev);
    }
    public void pistonEvent(BlockPistonEvent ev){
        Location l = ev.getBlock().getLocation();
        List<Consumer<BlockPistonEvent>> lst = handlers.get(new WorldPosI(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ()));
        if(lst != null) {
            for (Consumer<BlockPistonEvent> cons : lst) {
                if (!ev.isCancelled()) cons.accept(ev);
                else return;
            }
        }
    }
    public static void listenRetract(World w, int x, int y, int z, Consumer<BlockPistonEvent> cons){
        listenRetract(new WorldPosI(w,x,y,z),cons);
    }
    public static void listenRetract(WorldPosI wpi, Consumer<BlockPistonEvent> cons){
        List<Consumer<BlockPistonEvent>> lst = handlers.get(wpi);
        //Avoids CMods. Don't change.
        if(lst == null) {
            lst = new ArrayList<>();
            handlers.put(wpi,lst);
        }
        lst.add(cons);
    }
    public static void stopListeningRetract(World w, int x, int y, int z, Consumer<BlockPistonEvent> cons){
        stopListeningRetract(new WorldPosI(w,x,y,z),cons);
    }
    public static void stopListeningRetract(WorldPosI wpi, Consumer<BlockPistonEvent> cons){
        if(!handlers.containsKey(wpi)) return;
        List<Consumer<BlockPistonEvent>> lst = handlers.get(wpi);
        lst.remove(cons);
    }
}
