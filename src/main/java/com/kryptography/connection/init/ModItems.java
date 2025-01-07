package com.kryptography.connection.init;

import com.kryptography.connection.Connection;
import com.kryptography.connection.common.item.AmuletItem;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Connection.MOD_ID);

    public static final RegistryObject<Item> AMULET_OF_CONNECTION = ITEMS.register("amulet_of_connection", () -> new AmuletItem(new Item.Properties().stacksTo(2).rarity(Rarity.EPIC)));

    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            addBefore(event, Items.COMPASS, ModItems.AMULET_OF_CONNECTION.get());
        }
    }
    public static void addBefore(BuildCreativeModeTabContentsEvent event, ItemLike first, ItemLike second) {
        event.getEntries().putBefore(new ItemStack(first), new ItemStack(second), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
    }

}
