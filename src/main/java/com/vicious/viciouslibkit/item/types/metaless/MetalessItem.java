package com.vicious.viciouslibkit.item.types.metaless;

import com.vicious.viciouslibkit.item.types.ItemType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * Uses only the material for the hashcode method.
 */
public class MetalessItem<T> extends ItemType<Material,T> {
    public MetalessItem(Material type){
        super(type);
    }
    public MetalessItem(Material type, T meta){
        super(type,meta);
    }
    public MetalessItem(ItemStack stack){
        super(stack.getType());
    }

    @Override
    public boolean isType(ItemType<?, ?> type, boolean ignoreMeta) {
        return Objects.equals(type.getType(),getType());
    }

    @Override
    public int hashCode() {
        return type.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetalessItem<?> itemType = (MetalessItem<?>) o;
        return Objects.equals(type, itemType.type);
    }
    /**
     * Currently only supports vanilla items.
     */
    public static MetalessItem<?> fromItemStack(ItemStack stack){
        if(stack.hasItemMeta()){
            //Most custom items have a custom display name.
            if(stack.getItemMeta().hasDisplayName()){
                return new MetalessNamedItem(stack);
            }
            else return new MetalessItem<Object>(stack);
        }
        else return new MetalessItem<Object>(stack);
    }
}
