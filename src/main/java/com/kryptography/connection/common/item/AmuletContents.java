package com.kryptography.connection.common.item;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class AmuletContents {

	public static final int MAX_PEARLS = 16;

	public static final AmuletContents EMPTY = new AmuletContents(List.of());

	public static final Codec<AmuletContents> CODEC = ItemStack.CODEC.listOf()
			.xmap(AmuletContents::new, c -> c.items);

	public static final StreamCodec<RegistryFriendlyByteBuf, AmuletContents> STREAM_CODEC =
			ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list())
					.map(AmuletContents::new, c -> c.items);

	private final List<ItemStack> items;
	private final int totalCount;

	public AmuletContents(List<ItemStack> items) {
		this.items = items;
		this.totalCount = computeTotal(items);
	}

	private static int computeTotal(List<ItemStack> items) {
		int n = 0;
		for (ItemStack s : items) n += s.getCount();
		return n;
	}

	public int totalCount() {
		return this.totalCount;
	}

	public boolean isEmpty() {
		return this.items.isEmpty();
	}

	public Stream<ItemStack> stream() {
		return this.items.stream();
	}

	public Iterable<ItemStack> items() {
		return this.items;
	}

	public Iterable<ItemStack> itemsCopy() {
		List<ItemStack> copies = new ArrayList<>(this.items.size());
		for (ItemStack s : this.items) copies.add(s.copy());
		return copies;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof AmuletContents other)) return false;
		return this.totalCount == other.totalCount && ItemStack.listMatches(this.items, other.items);
	}

	@Override
	public int hashCode() {
		return ItemStack.hashStackList(this.items);
	}

	public static class Mutable {
		private final List<ItemStack> items;
		private int totalCount;

		public Mutable(AmuletContents contents) {
			this.items = new ArrayList<>(contents.items);
			this.totalCount = contents.totalCount;
		}

		public int totalCount() {
			return this.totalCount;
		}

		public boolean isEmpty() {
			return this.items.isEmpty();
		}

		public int spaceLeft() {
			return Math.max(0, MAX_PEARLS - this.totalCount);
		}

		private int findStackIndex(ItemStack stack) {
			if (!stack.isStackable()) return -1;
			for (int i = 0; i < this.items.size(); i++) {
				if (ItemStack.isSameItemSameComponents(this.items.get(i), stack)) return i;
			}
			return -1;
		}

		public int tryInsert(ItemStack stack) {
			if (stack.isEmpty() || !stack.is(Items.ENDER_PEARL)) return 0;
			int space = spaceLeft();
			if (space <= 0) return 0;

			int toInsert = Math.min(stack.getCount(), space);
			int idx = findStackIndex(stack);
			if (idx != -1) {
				ItemStack existing = this.items.remove(idx);
				ItemStack merged = existing.copyWithCount(existing.getCount() + toInsert);
				stack.shrink(toInsert);
				this.items.add(0, merged);
			} else {
				this.items.add(0, stack.split(toInsert));
			}
			this.totalCount += toInsert;
			return toInsert;
		}

		@Nullable
		public ItemStack removeOne() {
			if (this.items.isEmpty()) return null;
			ItemStack first = this.items.remove(0).copy();
			this.totalCount -= first.getCount();
			return first;
		}

		public int consume(int amount) {
			int remaining = amount;
			while (remaining > 0 && !this.items.isEmpty()) {
				ItemStack first = this.items.get(0);
				int take = Math.min(first.getCount(), remaining);
				first.shrink(take);
				remaining -= take;
				this.totalCount -= take;
				if (first.isEmpty()) this.items.remove(0);
			}
			return amount - remaining;
		}

		public AmuletContents toImmutable() {
			return new AmuletContents(List.copyOf(this.items));
		}
	}
}
