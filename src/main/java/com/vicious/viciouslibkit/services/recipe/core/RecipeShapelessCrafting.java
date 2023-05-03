package com.vicious.viciouslibkit.services.recipe.core;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class RecipeShapelessCrafting extends RecipeShapedCrafting {
    public RecipeShapelessCrafting(NamespacedKey key, ItemStack output, Object... inputs) {
        super(key, output, inputs);
    }

    @Override
    public void load() {
        super.load();
    }
}
