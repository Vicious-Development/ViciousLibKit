package com.vicious.viciouslibkit.services.recipe.deprecated.crafting;

import com.vicious.viciouslibkit.ViciousLibKit;
import org.bukkit.plugin.Plugin;

import java.util.Set;

public interface IRecipeProvider {
    Plugin plugin();
    default String getPath(){
        return plugin().getName() + "_" + getClass().getSimpleName() + ".txt";
    }
    default String defaultRecipesPath(){
        return ViciousLibKit.defaultRecipesPath;
    }
    Set<CustomCraftingRecipe> recipes();

    default void addRecipe(CustomCraftingRecipe customRecipe) {
        if(recipes().contains(customRecipe)){
            customRecipe.unregister();
            recipes().remove(customRecipe);
        }
        recipes().add(customRecipe);
    }
}
