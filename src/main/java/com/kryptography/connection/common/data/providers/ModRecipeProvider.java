package com.kryptography.connection.common.data.providers;

import com.kryptography.connection.init.ModItems;
import net.mehvahdjukaar.heartstone.Heartstone;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;

import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.AMULET_OF_CONNECTION.get(), 2)
                .requires(Items.TOTEM_OF_UNDYING)
                .requires(Heartstone.HEARTSTONE_ITEM.get(), 2)
                .unlockedBy("has_heartstone", has(Heartstone.HEARTSTONE_ITEM.get()))
                .save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.AMULET_OF_CONNECTION.get(), 2)
                .requires(ModItems.AMULET_OF_CONNECTION.get(), 2)
                .unlockedBy("has_heartstone", has(Heartstone.HEARTSTONE_ITEM.get()))
                .save(consumer, "amulet_of_connection_duplicate");
    }
}
