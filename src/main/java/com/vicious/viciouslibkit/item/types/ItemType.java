package com.vicious.viciouslibkit.item.types;

import com.vicious.viciouslibkit.item.NotCustomItemException;
import com.vicious.viciouslibkit.item.types.meta.NamedItem;
import com.vicious.viciouslibkit.item.types.meta.NamespacedItem;
import com.vicious.viciouslibkit.item.types.meta.VanillaItem;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * I've realized that people like making custom items, so I'm providing support for them.
 */
public class ItemType<T,M> {
    protected T type;
    protected M meta;
    public ItemType(){}
    public ItemType(T type){
        this.type=type;
    }
    public ItemType(T type,M meta){
        this.type=type;
        this.meta=meta;
    }
    public T getType(){
        return type;
    }
    public M getMeta(){
        return meta;
    }
    public boolean isType(ItemType<?,?> type, boolean ignoreMeta){
        if(ignoreMeta) return type.type.equals(type);
        else{
            return type.type.equals(type) && type.meta.equals(meta);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemType<?, ?> itemType = (ItemType<?, ?>) o;
        return Objects.equals(type, itemType.type) && Objects.equals(meta, itemType.meta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, meta);
    }

    /**
     * Currently only supports vanilla items.
     */
    public static ItemType<?,?> fromItemStack(ItemStack stack){
        if(stack.hasItemMeta()){
            //Most custom items have a custom display name.
            if(stack.getItemMeta().hasDisplayName()){
                try {
                    return new NamespacedItem(stack);
                } catch (NotCustomItemException e) {
                    return new NamedItem(stack);
                }
            }
            else return new VanillaItem(stack);
        }
        else return new VanillaItem(stack);
    }
}
