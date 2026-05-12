package com.kryptography.connection;

import com.kryptography.connection.common.data.providers.ConnectionItemModelProvider;
import com.kryptography.connection.common.data.providers.ConnectionRecipeProvider;
import com.kryptography.connection.registry.ConnectionDataComponents;
import com.kryptography.connection.registry.ConnectionItems;
import com.kryptography.connection.registry.ConnectionSounds;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@Mod(Connection.MOD_ID)
public class Connection {
	public static final String MOD_ID = "connection";

	public Connection(IEventBus bus) {
		ConnectionItems.ITEMS.register(bus);
		ConnectionSounds.SOUND_EVENTS.register(bus);
		ConnectionDataComponents.COMPONENTS.register(bus);

		bus.addListener(ConnectionItems::addCreative);
		bus.addListener(this::dataSetup);
	}

	private void dataSetup(GatherDataEvent event) {
		DataGenerator gen = event.getGenerator();
		PackOutput packOutput = gen.getPackOutput();
		ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
		CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();

		gen.addProvider(event.includeClient(), new ConnectionItemModelProvider(packOutput, existingFileHelper));
		gen.addProvider(event.includeServer(), new ConnectionRecipeProvider(packOutput, lookup));
	}
}
