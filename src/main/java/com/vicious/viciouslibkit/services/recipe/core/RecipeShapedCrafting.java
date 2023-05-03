package com.vicious.viciouslibkit.services.recipe.core;


import com.vicious.viciouslib.persistence.json.JSONArray;
import com.vicious.viciouslib.persistence.json.JSONMap;
import com.vicious.viciouslib.persistence.json.value.JSONValue;
import com.vicious.viciouslib.persistence.storage.aunotamations.PersistentPath;
import com.vicious.viciouslib.persistence.storage.aunotamations.Save;
import com.vicious.viciouslib.util.FileUtil;
import com.vicious.viciouslibkit.ViciousLibKit;
import com.vicious.viciouslibkit.item.CustomItemRegistry;
import com.vicious.viciouslibkit.item.ICustomItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;

import java.util.*;

public class RecipeShapedCrafting implements IRecipe{
    private final NamespacedKey key;
    private ItemStack output;
    @PersistentPath
    public String path;

    @Save
    public JSONMap keys = new JSONMap();

    @Save
    public JSONMap result = new JSONMap();

    @Save
    public JSONArray matrix = new JSONArray();

    public RecipeShapedCrafting(NamespacedKey key, ItemStack output, Object... inputs){
        this.key = key;
        this.output = output;
        String folder = FileUtil.createDirectoryIfDNE(ViciousLibKit.defaultRecipesPath + "/" + key.getNamespace()).toAbsolutePath().toString();
        path = folder + "/" + key.toString() + ".txt";
        Map<Object, String> keys = new LinkedHashMap<>();
        int k = 65;
        for (Object input : inputs) {
            if(!keys.containsKey(input)){
                keys.put(input,""+((char)k));
            }
            matrix.addObject(keys.get(input));
            k++;
        }
        keys.forEach((ingredient,identifier)->{
            if(ingredient instanceof ItemStack is){
                Map<String,Object> m = is.serialize();
                JSONMap out = new JSONMap();
                m.forEach(out::put);
                this.keys.put(identifier,out);
            }
            else if(ingredient instanceof Material m){
                this.keys.put(identifier,m.name());
            }
            else if(ingredient instanceof ICustomItem ici){
                this.keys.put(identifier,ici.key());
            }
            else if(ingredient instanceof RecipeChoice.ExactChoice choice){
                JSONArray arr = new JSONArray();
                for (ItemStack c : choice.getChoices()) {
                    arr.addObject(c);
                }
                this.keys.put(identifier,arr);
            }
            else if(ingredient instanceof RecipeChoice.MaterialChoice choice){
                JSONArray arr = new JSONArray();
                for (Material c : choice.getChoices()) {
                    arr.addObject(c);
                }
                this.keys.put(identifier,arr);
            }
            else{
                this.keys.put(identifier,ingredient);
            }
        });
    }

    @Override
    public void load() {
        IRecipe.super.load();
        output = parseStack(result);
        ShapedRecipe recipe = new ShapedRecipe(key,output);
        //Create the Matrix.
        String[] matrix = new String[3];
        for (int i = 0; i < this.matrix.size(); i++) {
            matrix[i] = this.matrix.get(i).softAs(String.class);
        }
        recipe.shape(matrix);
        //Create the ingredients.
        keys.forEach((key,choice)->{
            if(choice.get() == null){
                return;
            }
            if(key.length() > 1){
                ViciousLibKit.LOGGER.warning("Shaped Recipe " + this.key + " has illegal key " + key + " with more than 1 character. Only the first character will be used.");
            }
            recipe.setIngredient(key.charAt(0),parseChoice(choice));
        });
        Bukkit.getServer().addRecipe(recipe);
    }

    private ItemStack parseStack(JSONMap m) {
        Map<String,Object> objs = new HashMap<>();
        m.forEach((k,v)->{
            objs.put(k,v.get());
        });
        return ItemStack.deserialize(objs);
    }

    public RecipeChoice parseChoice(JSONValue value){
        Object obj = value.get();
        if(obj instanceof String s) {
            if (CustomItemRegistry.isRegistered(s)) {
                return new RecipeChoice.ExactChoice(CustomItemRegistry.getCustomItem(s).createStack(1));
            } else {
                Material m = Material.getMaterial(s);
                if (m != null) {
                    return new RecipeChoice.MaterialChoice(m);
                } else {
                    ViciousLibKit.LOGGER.warning("Shaped Recipe " + this.key + " has unknown item " + s + " as an ingredient. Will not include ingredient in recipe.");
                    return null;
                }
            }
        }
        else if(obj instanceof JSONArray arr){
            boolean exact = false;
            List<RecipeChoice> choices = new ArrayList<>();
            for (JSONValue jsonValue : arr) {
                RecipeChoice internal = parseChoice(jsonValue);
                if(internal != null) {
                    if (internal instanceof RecipeChoice.ExactChoice) {
                        exact = true;
                    }
                    choices.add(internal);
                }
            }
            if(exact){
                List<ItemStack> out = new ArrayList<>();
                for (RecipeChoice choice : choices) {
                    if(choice instanceof RecipeChoice.MaterialChoice m){
                        for (Material mChoice : m.getChoices()) {
                            out.add(new ItemStack(mChoice));
                        }
                    }
                    if(choice instanceof RecipeChoice.ExactChoice e){
                        out.addAll(e.getChoices());
                    }
                }
                return new RecipeChoice.ExactChoice(out);
            }
            else {
                List<Material> out = new ArrayList<>();
                for (RecipeChoice choice : choices) {
                    if(choice instanceof RecipeChoice.MaterialChoice m){
                        out.addAll(m.getChoices());
                    }
                }
                return new RecipeChoice.MaterialChoice(out);
            }
        }
        else if(obj instanceof JSONMap m){
            return new RecipeChoice.ExactChoice(parseStack(m));
        }
        else{
            ViciousLibKit.LOGGER.warning("Shaped Recipe " + this.key + " has unknown item " + obj + " as an ingredient. Will not include ingredient in recipe.");
            return null;
        }
    }
}
