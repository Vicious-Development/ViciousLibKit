package com.vicious.viciouslibkit.inventory.gui;

import com.vicious.viciouslibkit.event.Ticker;
import com.vicious.viciouslibkit.interfaces.ITickable;
import com.vicious.viciouslibkit.inventory.wrapper.InventoryEvents;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

public class CustomGUIInventory {
    private static IdentityHashMap<Object,CustomGUIInventory> active = new IdentityHashMap<>();
    static {
        for (InventoryAction value : InventoryAction.values()) {
            InventoryEvents.registerInventoryClickListener(value,(e)->{
                CustomGUIInventory gui = active.get(e.getClickedInventory());
                if(gui != null) {
                    if (e.isLeftClick()) {
                        gui.onLeftClick(e);
                    } else if (e.isRightClick()) {
                        gui.onRightClick(e);
                    }
                    else e.setCancelled(true);
                }
            });
        }
    }
    public static CustomGUIInventory newGUI(String title, int size){
        Inventory gui = Bukkit.createInventory(null,size,title);
        CustomGUIInventory ret = new CustomGUIInventory(gui);
        return ret;
    }

    public final Inventory GUI;
    public GUIElement[] elements;
    public Object parent;
    public Runnable onClose;
    public Map<UUID,Player> accessors = new HashMap<>();
    public CustomGUIInventory(Inventory gui) {
        GUI=gui;
        elements = new GUIElement[gui.getSize()];
        for (int i = 0; i < elements.length; i++) {
            elements[i] = new GUIElement(null);
            elements[i].slot = i;
        }
    }
    public void updateInventory(){
        for (int i = 0; i < elements.length; i++) {
            GUI.setItem(i,elements[i].getStack());
        }
    }
    public void updateElement(GUIElement element){
        GUI.setItem(element.slot,element.getStack());
    }
    public void onLeftClick(InventoryClickEvent ev){
        elements[ev.getRawSlot()].leftClick(ev);
    }
    public void onRightClick(InventoryClickEvent ev){
        elements[ev.getRawSlot()].rightClick(ev);
    }
    public CustomGUIInventory setElement(GUIElement element, int slot){
        elements[slot]=element;
        element.slot = slot;
        GUI.setItem(slot,element.getStack());
        return this;
    }
    public CustomGUIInventory setElement(GUIElement element, int row, int column){
        int slot = row*9+column;
        elements[slot]=element;
        element.slot = slot;
        GUI.setItem(slot,element.getStack());
        return this;
    }
    public void open(Player plr){
        if(!active.containsKey(GUI)) active.put(GUI,this);
        accessors.put(plr.getUniqueId(),plr);
        plr.openInventory(this.GUI);
    }
    public void close(Player plr){
        plr.closeInventory();
        accessors.remove(plr.getUniqueId());
        if(accessors.isEmpty()){
            close();
        }
    }
    public void forceClose(){
        accessors.forEach((k,v)->{
            v.closeInventory();
        });
        accessors.clear();
        close();
    }
    public void softClose(){
        removeTickables();
        active.remove(GUI);
    }
    protected void removeTickables(){
        for (GUIElement<?> element : elements) {
            if(element instanceof ITickable){
                Ticker.remove((ITickable) element);
            }
        }
    }
    public void close(){
        if(active.containsKey(this.GUI)){
            removeTickables();
           active.remove(GUI);
           if(onClose != null){
               onClose.run();
           }
        }
    }
}
