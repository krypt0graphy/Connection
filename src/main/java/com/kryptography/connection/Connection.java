package com.kryptography.connection;

import com.kryptography.connection.init.ModItems;
import com.kryptography.connection.init.ModSounds;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Connection.MOD_ID)
public class Connection {
    public static final String MOD_ID = "connection";

    public Connection() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(bus);
        ModSounds.SOUND_EVENTS.register(bus);

        bus.addListener(ModItems::addCreative);
    }


}
