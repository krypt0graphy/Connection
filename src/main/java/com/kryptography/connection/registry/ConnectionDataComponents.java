package com.kryptography.connection.registry;

import com.kryptography.connection.Connection;
import com.kryptography.connection.common.item.AmuletContents;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public class ConnectionDataComponents {

	public static final DeferredRegister.DataComponents COMPONENTS =
			DeferredRegister.createDataComponents(Connection.MOD_ID);

	public static final Supplier<DataComponentType<Long>> AMULET_ID = register("amulet_id",
			builder -> builder
					.persistent(Codec.LONG)
					.networkSynchronized(ByteBufCodecs.VAR_LONG));

	public static final Supplier<DataComponentType<AmuletContents>> AMULET_CONTENTS = register("amulet_contents",
			builder -> builder
					.persistent(AmuletContents.CODEC)
					.networkSynchronized(AmuletContents.STREAM_CODEC));

	private static <T> Supplier<DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> op) {
		return COMPONENTS.registerComponentType(name, builder -> op.apply(builder).cacheEncoding());
	}
}
