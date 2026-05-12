package com.kryptography.connection.common.data.providers;

import com.kryptography.connection.Connection;
import com.kryptography.connection.registry.ConnectionItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ConnectionItemModelProvider extends ItemModelProvider {
	public ConnectionItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
		super(output, Connection.MOD_ID, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		this.basicItem(ConnectionItems.AMULET_OF_CONNECTION.get())
				.override().predicate(ResourceLocation.withDefaultNamespace("custom_model_data"), 0)
				.model(singleTexture("amulet_of_connection_0", mcLoc("item/generated"), "layer0", modLoc("item/amulet_of_connection_0")))
				.end()
				.override().predicate(ResourceLocation.withDefaultNamespace("custom_model_data"), 1)
				.model(singleTexture("amulet_of_connection", mcLoc("item/generated"), "layer0", modLoc("item/amulet_of_connection")))
				.end();
	}
}
