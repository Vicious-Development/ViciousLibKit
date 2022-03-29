package com.vicious.viciouslibkit.util.map;

import com.vicious.viciouslibkit.item.types.ItemType;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;

import java.util.*;

/**
 * I found myself using a Map Material,ItemStack so much that I decided to create a version of a hashmap specifically for ItemStacks.
 * Items are mapped by their metadata and material, any item of the same mapping will be combined into one stack regardless if the stack exceeds maxstacksize.
 * Use getStacks() to get all stacks following stack size requirements.
 */
public class ItemStackMap extends HashMap<ItemType<?,?>,ItemStack> {
    private final Map<Material, List<ItemType<?,?>>> metaMap = new HashMap<>();
    private ItemType<?,?> getItemType(ItemStack stack){
        List<ItemType<?,?>> stl = metaMap.get(stack.getType());
        if(stl != null) {
            if (stl.size() == 1) {
                return stl.get(0);
            } else if (stl.size() > 1) {
                for (ItemType<?,?> st : stl) {
                    if (st.isType(ItemType.fromItemStack(stack),false)) {
                        return st;
                    }
                }
            }
        }
        List<ItemType<?,?>> lst = new ArrayList<>();
        ItemType<?,?> st = ItemType.fromItemStack(stack);
        lst.add(st);
        metaMap.put(stack.getType(),lst);
        return st;
    }
    public ItemStack get(ItemStack basis){
        return get(getItemType(basis));
    }
    /**
     * @param stack the stack to add.
     * @return if the Material was already present.
     */
    public boolean add(ItemStack stack){
        ItemType<?,?> mat = getItemType(stack);
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
        ItemType<?,?> mat = getItemType(stack);
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
        int maxStackCount = value.getAmount()/value.getMaxStackSize();
        int modular = value.getAmount()%value.getMaxStackSize();
        if(maxStackCount > 0){
            for (int i = 0; i < maxStackCount; i++) {
                ItemStack toAdd = new ItemStack(value.getType(),value.getMaxStackSize());
                toAdd.setItemMeta(value.getItemMeta());
                toAddTo.add(toAdd);
            }
        }
        if(modular != 0) {
            ItemStack toAdd = new ItemStack(value.getType(), modular);
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
        for (ItemType<?,?> k : mapped.keySet()) {
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
     * Warning this is a lossy process, the rough stack map stores items without NBT data. Do not do this unless you intend to use it as a data source rather than an item container.
     * @return
     */
    public RoughItemStackMap asRoughMap(){
        return new RoughItemStackMap().addAll(values());
    }
}
