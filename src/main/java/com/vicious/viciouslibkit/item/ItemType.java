package com.vicious.viciouslibkit.item;

import java.util.Objects;

/**
 * I've realized that people like making custom items, so I'm providing support for them.
 */
public class ItemType<T,M> {
    private T type;
    private M meta;
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
}
