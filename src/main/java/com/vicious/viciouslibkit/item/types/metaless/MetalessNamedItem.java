package com.vicious.viciouslibkit.item.types.metaless;

import com.vicious.viciouslibkit.item.types.ItemType;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

/**
 * Ignores the full metadata, but keeps the name data.
 */
public class MetalessNamedItem extends MetalessItem<String>{
    public MetalessNamedItem(ItemStack stack){
        super(stack.getType(),stack.hasItemMeta() ? stack.getItemMeta().getDisplayName() : "");
    }

    @Override
    public boolean isType(ItemType<?, ?> type, boolean ignoreMeta) {
        return Objects.equals(type.getType(),this.type) && Objects.equals(type.getType(),this.meta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type,meta);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetalessNamedItem itemType = (MetalessNamedItem) o;
        return Objects.equals(type, itemType.type) && Objects.equals(meta,itemType.meta);
    }
}
