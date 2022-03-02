package com.vicious.viciouslibkit.inventory.wrapper;

import com.vicious.viciouslibkit.util.interfaces.INotifiable;
import com.vicious.viciouslibkit.util.interfaces.INotifier;
import com.vicious.viciouslibkit.util.map.ItemStackMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Holds extra information about an inventory.
 * Used primarily for optimization purposes. (Unifies all stacks into one fake stack).
 */
public class InventoryWrapper implements INotifier<EInventoryUpdateStatus> {
    private List<INotifiable<EInventoryUpdateStatus>> notifiables = new ArrayList<>();
    public Inventory INVENTORY;
    private ItemStackMap MAP = new ItemStackMap();
    public InventoryWrapper(Inventory inventory){
        map(inventory);
    }
    public void removeFromMap(ItemStack stack){
        MAP.reduceBy(stack);
        sendNotification(EInventoryUpdateStatus.REMOVED);

    }
    public ItemStackMap getMap(){
        return MAP;
    }

    public void addToMap(ItemStack stack) {
        MAP.add(stack);
        sendNotification(EInventoryUpdateStatus.ADDED);
    }

    @Override
    public void sendNotification(EInventoryUpdateStatus status) {
        for (INotifiable<EInventoryUpdateStatus> notifiable : notifiables) {
            notifiable.notify(this,status);
        }
    }

    @Override
    public void listen(INotifiable<EInventoryUpdateStatus> notifiable) {
        notifiables.add(notifiable);
    }

    @Override
    public void stopListening(INotifiable<EInventoryUpdateStatus> notifiable) {
        notifiables.remove(notifiable);
    }

    public void unwrap() {
        notifiables.clear();
        INVENTORY = null;
        MAP = null;
    }

    public void remap(Inventory inv) {
        MAP.clear();
        map(inv);
        sendNotification(EInventoryUpdateStatus.REMAPPED);
    }
    private void map(Inventory inv){
        INVENTORY=inv;
        for (ItemStack item : INVENTORY.getStorageContents()) {
            if(item == null) continue;
            MAP.add(item);
        }
    }

    public void remove(ItemStack input) {
        MAP.reduceBy(input);
        INVENTORY.removeItem(input);
    }

    public Map<Integer,ItemStack> addItem(ItemStack output) {
        MAP.add(output);
        return INVENTORY.addItem(output);
    }
}
