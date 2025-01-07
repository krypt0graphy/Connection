package com.kryptography.connection.common.data.providers;

import com.kryptography.connection.Connection;
import com.kryptography.connection.init.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, Connection.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.basicItem(ModItems.AMULET_OF_CONNECTION.get()).override().predicate(new ResourceLocation("custom_model_data"), 0).model(
                singleTexture("amulet_of_connection_0", mcLoc("item/generated"), "layer0", modLoc("item/" + "amulet_of_connection_0"))
        ).end().override().predicate(new ResourceLocation("custom_model_data"), 1).model(
               singleTexture("amulet_of_connection", mcLoc("item/generated"), "layer0", modLoc("item/" + "amulet_of_connection"))
        ).end();


    }
}
