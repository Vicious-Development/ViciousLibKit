package com.vicious.viciouslibkit.event.piston;

import com.vicious.viciouslibkit.util.vector.WorldPosI;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
    private static final Map<WorldPosI, List<Consumer<BlockPistonRetractEvent>>> handlers = new HashMap<>();
    @EventHandler
    public void onRetract(BlockPistonRetractEvent ev){
        Location l = ev.getBlock().getLocation();
        List<Consumer<BlockPistonRetractEvent>> lst = handlers.get(new WorldPosI(l.getWorld(), l.getBlockX(), l.getBlockY(), l.getBlockZ()));
        if(lst != null) {
            for (Consumer<BlockPistonRetractEvent> cons : lst) {
                if (!ev.isCancelled()) cons.accept(ev);
                else return;
            }
        }
    }
    public static void listenRetract(World w, int x, int y, int z, Consumer<BlockPistonRetractEvent> cons){
        listenRetract(new WorldPosI(w,x,y,z),cons);
    }
    public static void listenRetract(WorldPosI wpi, Consumer<BlockPistonRetractEvent> cons){
        List<Consumer<BlockPistonRetractEvent>> lst = handlers.computeIfAbsent(wpi,(k)->new ArrayList<>());
        lst.add(cons);
    }
    public static void stopListeningRetract(World w, int x, int y, int z, Consumer<BlockPistonRetractEvent> cons){
        stopListeningRetract(new WorldPosI(w,x,y,z),cons);
    }
    public static void stopListeningRetract(WorldPosI wpi, Consumer<BlockPistonRetractEvent> cons){
        if(!handlers.containsKey(wpi)) return;
        List<Consumer<BlockPistonRetractEvent>> lst = handlers.get(wpi);
        lst.remove(cons);
    }
}
