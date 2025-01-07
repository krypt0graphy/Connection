package com.kryptography.connection.init;

import com.kryptography.connection.Connection;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Connection.MOD_ID);


    public static final RegistryObject<SoundEvent> PEARL_INSERTED = SOUND_EVENTS.register("pearl_inserted", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Connection.MOD_ID, "pearl_inserted")));
    public static final RegistryObject<SoundEvent> PEARL_REMOVED = SOUND_EVENTS.register("pearl_removed", () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Connection.MOD_ID, "pearl_removed")));
}
