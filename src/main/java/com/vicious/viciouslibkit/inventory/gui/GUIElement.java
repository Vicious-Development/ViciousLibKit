package com.vicious.viciouslibkit.inventory.gui;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class GUIElement<T extends GUIElement<?>> {
    public static GUIElement EMPTY = new GUIElement(null);
    public int slot;
    private ItemStack stack;
    public boolean cancel = true;
    private Consumer<InventoryClickEvent> onLeftClick;
    private Consumer<InventoryClickEvent> onRightClick;
    public GUIElement(ItemStack stack){
        this.stack = stack;
    }
    public GUIElement(ItemStack stack, boolean cancelEvent){
        this.stack = stack;
        cancel=cancelEvent;
    }
    public ItemStack getStack(){
        return stack;
    }
    public GUIElement<?> onLeftClick(Consumer<InventoryClickEvent> onLeftClick){
        this.onLeftClick=onLeftClick;
        return this;
    }
    public GUIElement<?> onRightClick(Consumer<InventoryClickEvent> onRightClick){
        this.onRightClick=onRightClick;
        return this;
    }
    public void leftClick(InventoryClickEvent ev) {
        if(onLeftClick != null) onLeftClick.accept(ev);
        ev.setCancelled(cancel);
    }
    public void rightClick(InventoryClickEvent ev) {
        if(onRightClick != null) onRightClick.accept(ev);
        ev.setCancelled(cancel);
    }
    public T lore(String... lore){
        ItemMeta metaverse = stack.getItemMeta();
        if(metaverse == null) return (T) this;
        metaverse.setLore(Arrays.asList(lore));
        stack.setItemMeta(metaverse);
        return (T) this;
    }

    public void setStack(ItemStack stack) {
        this.stack=stack;
    }

    public static GUIElement loredNamelessElement(ItemStack stack, String... lore){
        ItemMeta metaverse = stack.getItemMeta();
        if(metaverse == null) return GUIElement.of(stack);
        metaverse.setLore(Arrays.asList(lore));
        stack.setItemMeta(metaverse);
        return GUIElement.of(stack);
    }
    public static GUIElement loredElement(ItemStack stack, String name, String... lore){
        return loredElement(stack,name,Arrays.asList(lore));
    }
    public static GUIElement loredElement(ItemStack stack, String name, List<String> lore){
        ItemMeta metaverse = stack.getItemMeta();
        if(metaverse == null) return GUIElement.of(stack);
        metaverse.setDisplayName(name);
        metaverse.setLore(lore);
        stack.setItemMeta(metaverse);
        return GUIElement.of(stack);
    }

    public GUIElement toggleEnchant(){
        if(stack != null){
            ItemMeta metaverse = stack.getItemMeta();
            if(metaverse == null) return this;
            if(stack.containsEnchantment(Enchantment.WATER_WORKER)){
                stack.removeEnchantment(Enchantment.WATER_WORKER);
            }
            else {
                stack.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
                metaverse.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                stack.setItemMeta(metaverse);
            }
        }
        return this;
    }
    public static GUIElement of(ItemStack stack) {
        return new GUIElement(stack);
    }
}
