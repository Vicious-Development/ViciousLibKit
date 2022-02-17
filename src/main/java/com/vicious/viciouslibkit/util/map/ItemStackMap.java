package com.vicious.viciouslibkit.util.map;

import com.vicious.viciouslibkit.item.StackType;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.checkerframework.checker.units.qual.A;
import org.checkerframework.checker.units.qual.K;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * I found myself using a Map Material,ItemStack so much that I decided to create a version of a hashmap specifically for ItemStacks.
 * Items are mapped by their metadata and material, any item of the same mapping will be combined into one stack regardless if the stack exceeds maxstacksize.
 * Use getStacks() to get all stacks following stack size requirements.
 */
public class ItemStackMap extends HashMap<StackType,ItemStack> {
    private Map<Material, List<StackType>> metaMap = new HashMap<>();
    private StackType getStackType(ItemStack stack){
        List<StackType> stl = metaMap.get(stack.getType());
        if(stl != null) {
            if (stl.size() == 1) {
                return stl.get(0);
            } else if (stl.size() > 1) {
                for (StackType st : stl) {
                    if (st.isType(stack)) {
                        return st;
                    }
                }
            }
        }
        List<StackType> lst = new ArrayList<>();
        StackType st = new StackType(stack);
        lst.add(st);
        metaMap.put(stack.getType(),lst);
        return st;
    }
    public ItemStack get(ItemStack basis){
        return get(getStackType(basis));
    }
    /**
     * @param stack the stack to add.
     * @return if the Material was already present.
     */
    public boolean add(ItemStack stack){
        StackType mat = getStackType(stack);
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
        StackType mat = getStackType(stack);
        ItemStack mappedStack = get(mat);
        if(mappedStack == null) return false;
        mappedStack.setAmount(mappedStack.getAmount()-stack.getAmount());
        if(mappedStack.getAmount() == 0) remove(mat);
        return mappedStack.getAmount() < 0;
    }

    public ItemStackMap addAll(Collection<ItemStack> items){
        for (ItemStack item : items) {
            add(item);
        }
        return this;
    }
    public ItemStackMap addFromEntities(Item... itemEntities) {
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
    private ItemStackMap convertToLegalStacks(List<ItemStack> toAddTo, ItemStack value){
        int maxStackCount = value.getAmount()/value.getMaxStackSize()-1;
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

    public ItemStackMap combine(ItemStackMap stacks) {
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
        ItemStackMap mapped = new ItemStackMap().addAll(stacks);
        System.out.println(mapped);
        for (StackType k : mapped.keySet()) {
            ItemStack mapStack = get(k);
            if (mapStack == null) return false;
            ItemStack expected = mapped.get(k);
            if (mapStack.getAmount() >= expected.getAmount()) mapped.remove(k);
            else mapped.reduceBy(mapStack);
        }
        //Mapped should be empty by the end of this.
        return mapped.size() == 0;
    }
}
