package com.kryptography.connection;

import com.kryptography.connection.init.ModItems;
import com.kryptography.connection.init.ModSounds;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(Connection.MOD_ID)
public class Connection {
    public static final String MOD_ID = "connection";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID.toUpperCase());

    public Connection() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModItems.ITEMS.register(bus);
        ModSounds.SOUND_EVENTS.register(bus);

        bus.addListener(ModItems::addCreative);
    }


}
