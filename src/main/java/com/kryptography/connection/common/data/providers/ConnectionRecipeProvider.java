package com.kryptography.connection.common.data.providers;

import com.kryptography.connection.registry.ConnectionItems;
import net.mehvahdjukaar.heartstone.Heartstone;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class ConnectionRecipeProvider extends RecipeProvider {
	public ConnectionRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup) {
		super(output, lookup);
	}

	@Override
	protected void buildRecipes(RecipeOutput output) {
		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConnectionItems.AMULET_OF_CONNECTION.get(), 2)
				.requires(Items.TOTEM_OF_UNDYING)
				.requires(Heartstone.HEARTSTONE_ITEM.get(), 2)
				.unlockedBy("has_heartstone", has(Heartstone.HEARTSTONE_ITEM.get()))
				.save(output);

		ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ConnectionItems.AMULET_OF_CONNECTION.get(), 2)
				.requires(ConnectionItems.AMULET_OF_CONNECTION.get(), 2)
				.unlockedBy("has_heartstone", has(Heartstone.HEARTSTONE_ITEM.get()))
				.save(output, ResourceLocation.fromNamespaceAndPath("minecraft", "amulet_of_connection_duplicate"));
	}
}
