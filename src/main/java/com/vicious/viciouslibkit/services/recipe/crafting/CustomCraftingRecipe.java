package com.vicious.viciouslibkit.services.recipe.crafting;

import com.vicious.viciouslibkit.item.ICustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Objects;

public class CustomCraftingRecipe {
    private final NamespacedKey key;
    private final ItemStack[] inputs;
    private final ItemStack output;

    public CustomCraftingRecipe(NamespacedKey key, ItemStack output, ItemStack... inputs){
        this.inputs = new ItemStack[9];
        for (int i = 0; i < this.inputs.length; i++) {
            if(i < inputs.length){
                this.inputs[i]=inputs[i];
            }
            else{
                this.inputs[i]=new ItemStack(Material.AIR);
            }
        }
        this.output=output;
        this.key=key;
    }
    public CustomCraftingRecipe(NamespacedKey key, ItemStack output, Material... inputs){
        this.inputs = new ItemStack[9];
        for (int i = 0; i < this.inputs.length; i++) {
            if(i < inputs.length){
                this.inputs[i]=new ItemStack(inputs[i]);
            }
            else{
                this.inputs[i]=new ItemStack(Material.AIR);
            }
        }
        this.output=output;
        this.key=key;
    }
    public CustomCraftingRecipe(NamespacedKey key, ItemStack output, Object... inputs){
        this.inputs = new ItemStack[9];
        for (int i = 0; i < this.inputs.length; i++) {
            if(i < inputs.length){
                if(inputs[i] instanceof Material m) {
                    this.inputs[i] = new ItemStack(m);
                }
                else if(inputs[i] instanceof ICustomItem e){
                    this.inputs[i] = e.createStack(1);
                }
            }
            else{
                this.inputs[i]=new ItemStack(Material.AIR);
            }
        }
        this.output=output;
        this.key=key;
    }

    public boolean matches(CraftingInventory inventory){
        ItemStack[] matrix = inventory.getMatrix();
        for (int i = 0; i < matrix.length; i++) {
            if(!inputs[i].isSimilar(matrix[i])){
                return false;
            }
        }
        return true;
    }
    public NamespacedKey getKey(){
        return key;
    }
    public ItemStack getOutput(){
        return output;
    }

    public void write(StringBuilder builder){
        builder.append(key.toString()).append('\n');
        builder.append(getKey(output)).append('[').append('\n');
        for (ItemStack input : inputs) {
            builder.append(getKey(input)).append('\n');
        }
        builder.append(']');
    }

    @SuppressWarnings("ConstantConditions")
    private String getKey(ItemStack output) {
        ItemMeta meta = output.getItemMeta();
        if(meta != null){
            PersistentDataContainer cont = meta.getPersistentDataContainer();
            if(cont.has(ICustomItem.customItemKey, PersistentDataType.STRING)){
                return cont.get(ICustomItem.customItemKey, PersistentDataType.STRING);
            }
        }
        return output.getType().name();
    }


    public void register(){
        ShapedRecipe recipe = new ShapedRecipe(key,output);
        //Lazy but whatever.
        recipe.shape("abc","def","ghi");
        recipe.setIngredient('a', new RecipeChoice.ExactChoice(inputs[0]));
        recipe.setIngredient('b',new RecipeChoice.ExactChoice(inputs[1]));
        recipe.setIngredient('c',new RecipeChoice.ExactChoice(inputs[2]));
        recipe.setIngredient('d',new RecipeChoice.ExactChoice(inputs[3]));
        recipe.setIngredient('e',new RecipeChoice.ExactChoice(inputs[4]));
        recipe.setIngredient('f',new RecipeChoice.ExactChoice(inputs[5]));
        recipe.setIngredient('g',new RecipeChoice.ExactChoice(inputs[6]));
        recipe.setIngredient('h',new RecipeChoice.ExactChoice(inputs[7]));
        recipe.setIngredient('i',new RecipeChoice.ExactChoice(inputs[8]));
        if(Bukkit.getServer().getRecipe(key) == null) {
            Bukkit.getServer().addRecipe(recipe);
        }
    }

    public void unregister() {
        Bukkit.removeRecipe(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomCraftingRecipe that = (CustomCraftingRecipe) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
