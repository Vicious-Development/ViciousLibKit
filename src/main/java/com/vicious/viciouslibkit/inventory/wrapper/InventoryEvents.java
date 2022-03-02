package com.vicious.viciouslibkit.inventory.wrapper;

import com.vicious.viciouslibkit.ViciousLibKit;
import com.vicious.viciouslibkit.data.DataTypeNotFoundException;
import com.vicious.viciouslibkit.data.worldstorage.PluginChunkData;
import com.vicious.viciouslibkit.data.worldstorage.PluginWorldData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class InventoryEvents implements Listener {
    private static Map<InventoryAction,List<Consumer<InventoryClickEvent>>> clickEventHandlers = new EnumMap<>(InventoryAction.class);
    @EventHandler(priority = EventPriority.LOWEST)
    public void onMove(InventoryMoveItemEvent ev){
        if(ev.isCancelled()) return;
        Inventory source = ev.getSource();
        Inventory destination = ev.getDestination();
        removeFromInventory(source,ev.getItem());
        addToInventory(destination,ev.getItem());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPickup(InventoryPickupItemEvent ev){
        if(ev.isCancelled()) return;
        addToInventory(ev.getInventory(),ev.getItem().getItemStack());
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void onClick(InventoryClickEvent ev){
        if(ev.isCancelled()) return;
        List<Consumer<InventoryClickEvent>> handlers = clickEventHandlers.get(ev.getAction());
        if(handlers == null) return;
        for (Consumer<InventoryClickEvent> handler : handlers) {
            handler.accept(ev);
        }
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public static void onInventoryDrag(InventoryDragEvent ev){
        if(ev.isCancelled()) return;
        Bukkit.getScheduler().scheduleSyncDelayedTask(ViciousLibKit.INSTANCE,()->remapWrapper(ev.getInventory()),1);
    }
    public static void removeFromInventory(Inventory inv, ItemStack... stacks){
        try{
            updateInventory(inv,InventoryWrapper::removeFromMap,stacks);
        } catch (Exception e){
            ViciousLibKit.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }
    public static void addToInventory(Inventory inv, ItemStack... stacks){
        try {
            updateInventory(inv, InventoryWrapper::addToMap, stacks);
        } catch (Exception e){
            ViciousLibKit.LOGGER.warning(e.getMessage());
            e.printStackTrace();
        }
    }
    public static void remapWrapper(Inventory inv){
        InventoryWrapper wrapper = getWrapper(inv);
        if(wrapper == null){
            return;
        }
        wrapper.remap(inv);
    }
    public static InventoryWrapper getWrapper(Inventory inv){
        Location l = inv.getLocation();
        if(l == null) return null;
        if(l.getWorld() == null) return null;
        PluginChunkData cdat = PluginWorldData.getChunkData(l.getWorld(),l);
        InventoryWrapperChunkHandler iwch;
        try {
            iwch = cdat.getDataHandler(InventoryWrapperChunkHandler.class);
        } catch (DataTypeNotFoundException e) {
            ViciousLibKit.LOGGER.severe(e.getMessage());
            e.printStackTrace();
            return null;
        }
        return iwch.getWrapper(l);
    }
    public static void updateInventory(Inventory inv, BiConsumer<InventoryWrapper,ItemStack> consumer, ItemStack... stacks) {
        InventoryWrapper wrapper = getWrapper(inv);
        if(wrapper == null) return;
        for (ItemStack stack : stacks) {
            consumer.accept(wrapper,stack);
        }
        wrapper.INVENTORY=inv;
    }
    public static void registerInventoryClickListener(InventoryAction act, Consumer<InventoryClickEvent> consumer){
        clickEventHandlers.putIfAbsent(act, new ArrayList<>());
        clickEventHandlers.get(act).add(consumer);
    }

    /**
     * Used for inventory optimization.
     * See InventoryWrapper for more details.
     */
    static{
        //TODO: Optimize all player functions.
        //This is disabled due to the difficulty of not duping stuff lmao.
        /*registerInventoryClickListener(InventoryAction.PLACE_ALL,(ev)-> {
            addToInventory(ev.getClickedInventory(),ev.getCursor());
        });
        registerInventoryClickListener(InventoryAction.PLACE_ONE,(ev)-> {
            ItemStack stack = ev.getCursor();
            if(stack == null) return;
            stack=stack.clone();
            stack.setAmount(1);
            addToInventory(ev.getClickedInventory(),stack);
        });
        registerInventoryClickListener(InventoryAction.PLACE_SOME,(ev)-> {
            ItemStack cur = ev.getCurrentItem();
            if(cur == null || ev.getCursor() == null) return;
            int toFill = cur.getMaxStackSize()-cur.getAmount();
            ItemStack stack = ev.getCursor().clone();
            stack.setAmount(toFill);
            addToInventory(ev.getClickedInventory(),stack);
        });
        registerInventoryClickListener(InventoryAction.PICKUP_HALF,(ev)->{
            ItemStack cursorItem = ev.getCurrentItem();
            if(cursorItem == null) return;
            cursorItem=cursorItem.clone();
            cursorItem.setAmount((int) Math.ceil(ev.getCurrentItem().getAmount()/2.0));
            removeFromInventory(ev.getClickedInventory(),cursorItem);
        });
        registerInventoryClickListener(InventoryAction.PICKUP_ALL,(ev)->{
            removeFromInventory(ev.getClickedInventory(),ev.getCurrentItem());
        });
        registerInventoryClickListener(InventoryAction.DROP_ALL_SLOT,(ev)->{
            removeFromInventory(ev.getClickedInventory(),ev.getCurrentItem());
        });
        registerInventoryClickListener(InventoryAction.DROP_ONE_SLOT,(ev)->{
            ItemStack stack = ev.getCurrentItem();
            if(stack == null) return;
            stack=stack.clone();
            stack.setAmount(1);
            removeFromInventory(ev.getClickedInventory(),stack);
        });*/
        InventoryAction[] test = InventoryAction.values();
        for (InventoryAction inventoryAction : test) {
            registerInventoryClickListener(inventoryAction,(ev)->{
                try {
                    if (ev.isCancelled()) return;
                    if (ev.getClickedInventory() != null && ev.getClickedInventory().getHolder() instanceof Container) {
                        Bukkit.getScheduler().scheduleSyncDelayedTask(ViciousLibKit.INSTANCE, () -> remapWrapper(ev.getClickedInventory()), 1);
                    }
                    else {
                        Inventory inv2 = ev.getInventory();
                        if (inv2.getHolder() instanceof Container) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(ViciousLibKit.INSTANCE, () -> remapWrapper(inv2), 1);
                        }
                    }
                } catch (Exception e){
                    ViciousLibKit.LOGGER.warning("Error in inventory action handler: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    }
}
