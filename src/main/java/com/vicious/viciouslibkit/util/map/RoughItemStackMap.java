package com.vicious.viciouslibkit.util.map;

import com.vicious.viciouslibkit.item.types.metaless.MetalessItem;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * Acts as an ItemStackMap ignoring NBT.
 */
public class RoughItemStackMap extends HashMap<MetalessItem<?>,ItemStack> {
    public ItemStack get(ItemStack basis){
        return get(basis.getType());
    }
    /**
     * @param stack the stack to add.
     * @return if the Material was already present.
     */
    public boolean add(ItemStack stack){
        MetalessItem<?> mat = MetalessItem.fromItemStack(stack);
        if(putIfAbsent(mat,stack.clone()) != null){
            ItemStack mappedStack = get(mat);
            mappedStack.setAmount(mappedStack.getAmount()+stack.getAmount());
            replace(mat,mappedStack);
            return true;
        }
        return false;
    }

    /**
     * Reduces a stored item
     * @param stack
     * @return
     */
    public boolean reduceBy(ItemStack stack){
        MetalessItem<?> mat = MetalessItem.fromItemStack(stack);
        ItemStack mappedStack = get(mat);
        if(mappedStack == null) return false;
        mappedStack.setAmount(mappedStack.getAmount()-stack.getAmount());
        if(mappedStack.getAmount() == 0) remove(mat);
        return mappedStack.getAmount() < 0;
    }

    public RoughItemStackMap addAll(Collection<ItemStack> items){
        for (ItemStack item : items) {
            add(item);
        }
        return this;
    }
    public RoughItemStackMap addFromEntities(Item... itemEntities) {
        for (Item item : itemEntities) {
            ItemStack stack = item.getItemStack();
            add(stack);
        }
        return this;
    }
    public List<ItemStack> getStacks(){
        List<ItemStack> stacks = new ArrayList<>();
        for (ItemStack value : values()) {
            if(value.getAmount() <= 0) continue;
            convertToLegalStacks(stacks,value);
        }
        return stacks;
    }
    private RoughItemStackMap convertToLegalStacks(List<ItemStack> toAddTo, ItemStack value){
        int maxStackCount = value.getAmount()/value.getMaxStackSize();
        int modular = value.getAmount()%value.getMaxStackSize();
        if(maxStackCount > 0){
            for (int i = 0; i < maxStackCount; i++) {
                ItemStack toAdd = new ItemStack(value.getType(),value.getMaxStackSize());
                toAdd.setItemMeta(value.getItemMeta());
                toAddTo.add(toAdd);
            }
        }
        if(modular != 0){
            ItemStack toAdd = new ItemStack(value.getType(),modular);
            toAdd.setItemMeta(value.getItemMeta());
            toAddTo.add(toAdd);
        }
        return this;
    }

    public RoughItemStackMap combine(RoughItemStackMap stacks) {
        for (ItemStack value : stacks.values()) {
            add(value);
        }
        return this;
    }

    /**
     * Checks if the map contains AT LEAST all the elements in the list.
     */
    public boolean hasAll(List<ItemStack> stacks){
        //Map to unify stacks.
        RoughItemStackMap mapped = new RoughItemStackMap().addAll(stacks);
        for (MetalessItem<?> k : mapped.keySet()) {
            ItemStack mapStack = get(k);
            if (mapStack == null) return false;
            ItemStack expected = mapped.get(k);
            if (mapStack.getAmount() >= expected.getAmount()) mapped.remove(k);
            else mapped.reduceBy(mapStack);
        }
        //Mapped should be empty by the end of this.
        return mapped.size() == 0;
    }

    /**
     * WARNING THIS IS A LOSSY PROCESS!
     * Converting between rough and fine itemstackmaps eliminates NBT regardless and the resulting fine map will still act just like the rough map until new items are added.
     * @return
     */
    public ItemStackMap asFineMap(){
        return new ItemStackMap().addAll(values());
    }
}
