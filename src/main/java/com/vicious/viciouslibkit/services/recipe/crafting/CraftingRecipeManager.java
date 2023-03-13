package com.vicious.viciouslibkit.services.recipe.crafting;

import com.vicious.viciouslibkit.item.CustomItemRegistry;
import com.vicious.viciouslibkit.item.ICustomItem;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CraftingRecipeManager {
    private static final Set<IRecipeProvider> providers = new HashSet<>();
    private static final Set<IRecipeProvider> failedLoads = new HashSet<>();

    public static void addRecipeProvider(IRecipeProvider provider){
        providers.add(provider);
        attemptLoad(provider);
    }

    private static void attemptLoad(IRecipeProvider provider){
        Set<IRecipeProvider> success = new HashSet<>();
        for (IRecipeProvider failedLoad : failedLoads) {
            if(load(failedLoad)){
                success.add(failedLoad);
            }
        }
        failedLoads.removeAll(success);
        if(load(provider)) {
            save(provider);
        }
        else{
            failedLoads.add(provider);
        }
    }

    public static void reload(){
        load();
        save();
    }

    public static void load(){
        for (IRecipeProvider provider : providers) {
            load(provider);
        }
    }

    public static boolean load(IRecipeProvider provider) {
        try {
            Scanner scan = new Scanner(new File(provider.getPath()));
            String name = null;
            ItemStack out = null;
            List<ItemStack> in = new ArrayList<>();
            while(scan.hasNextLine()){
                String line = scan.nextLine();
                if(name == null){
                    name = line;
                }
                else if(out == null){
                    out = stackOf(line.substring(0,line.length()-1));
                    if(out == null){
                        return false;
                    }
                }
                else if(in.size() < 9){
                    in.add(stackOf(line));
                }
                else{
                    provider.addRecipe(new CustomCraftingRecipe(NamespacedKey.fromString(name), out, in.toArray(new ItemStack[0])));
                    name=null;
                    out=null;
                    in.clear();
                }
            }
            scan.close();
        } catch (FileNotFoundException ignored) {}
        for (CustomCraftingRecipe recipe : provider.recipes()) {
            recipe.register();
        }
        return true;
    }

    private static ItemStack stackOf(String line) {
        ICustomItem item = CustomItemRegistry.getCustomItem(line);
        if(item != null) {
            CustomItemRegistry.getCustomItem(line);
            return item.createStack(1);
        }
        else {
            return new ItemStack(Material.valueOf(line));
        }
    }

    public static void save(IRecipeProvider provider){
        StringBuilder out = new StringBuilder();
        for (CustomCraftingRecipe recipe : provider.recipes()) {
            recipe.write(out);
            out.append('\n');
        }
        try {
            File f = new File(provider.getPath());
            if(!f.exists()){
                f.createNewFile();
            }
            FileWriter writer = new FileWriter(f);
            writer.write(out.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void save() {
        for (IRecipeProvider provider : providers) {
            save(provider);
        }
    }
}
