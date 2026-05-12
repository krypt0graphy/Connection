package com.kryptography.connection.registry;

import com.kryptography.connection.Connection;
import com.kryptography.connection.common.item.AmuletItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ConnectionItems {

	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Connection.MOD_ID);

	public static final DeferredItem<AmuletItem> AMULET_OF_CONNECTION = ITEMS.register("amulet_of_connection",
			() -> new AmuletItem(new Item.Properties()
					.stacksTo(2)
					.rarity(Rarity.EPIC)));

	public static void addCreative(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			addBefore(event, Items.COMPASS, AMULET_OF_CONNECTION.get());
		}
	}

	private static void addBefore(BuildCreativeModeTabContentsEvent event, ItemLike anchor, ItemLike toInsert) {
		event.insertBefore(new ItemStack(anchor), new ItemStack(toInsert), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
	}
}
