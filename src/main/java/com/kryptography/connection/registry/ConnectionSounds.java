package com.kryptography.connection.registry;

import com.kryptography.connection.Connection;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConnectionSounds {

	public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
			DeferredRegister.create(Registries.SOUND_EVENT, Connection.MOD_ID);

	public static final DeferredHolder<SoundEvent, SoundEvent> PEARL_INSERTED = register("pearl_inserted");
	public static final DeferredHolder<SoundEvent, SoundEvent> PEARL_REMOVED = register("pearl_removed");
	public static final DeferredHolder<SoundEvent, SoundEvent> PLAYER_OR_PEARL_MISSING = register("player_or_pearl_missing");

	private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
		return SOUND_EVENTS.register(name,
				() -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Connection.MOD_ID, name)));
	}
}
