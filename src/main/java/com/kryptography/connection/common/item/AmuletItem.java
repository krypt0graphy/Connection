package com.kryptography.connection.common.item;


import com.kryptography.connection.registry.ConnectionDataComponents;
import com.kryptography.connection.registry.ConnectionSounds;
import net.mehvahdjukaar.heartstone.HeartstoneData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.players.PlayerList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class AmuletItem extends Item {

	public AmuletItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return getAmuletId(stack) != null;
	}

	@Nullable
	public static Long getAmuletId(ItemStack stack) {
		return stack.get(ConnectionDataComponents.AMULET_ID.get());
	}

	public static AmuletContents getContents(ItemStack stack) {
		return stack.getOrDefault(ConnectionDataComponents.AMULET_CONTENTS.get(), AmuletContents.EMPTY);
	}

	public static int getCurrentAmount(ItemStack stack) {
		return getContents(stack).totalCount();
	}


	@Override
	public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
		ItemStack itemstack = player.getItemInHand(hand);
		if (level.isClientSide) {
			return InteractionResultHolder.success(itemstack);
		}

		player.awardStat(Stats.ITEM_USED.get(this));
		Player otherPlayer = getBoundPlayer(player, itemstack, true);

		if (otherPlayer == null) {
			player.displayClientMessage(Component.translatable("message.amulet.noplayer").withStyle(ChatFormatting.RED), true);
			level.playSound(null, player, ConnectionSounds.PLAYER_OR_PEARL_MISSING.get(), player.getSoundSource(), 0.6F, 0.5F);
			player.getCooldowns().addCooldown(this, 60);
			return InteractionResultHolder.consume(itemstack);
		}

		float distance = player.distanceTo(otherPlayer);
		int needed = pearlCost(distance);
		int current = getCurrentAmount(itemstack);

		if (current >= needed) {
			consumePearls(itemstack, needed);
			BlockPos pos = validPos(otherPlayer);
			level.playSound(null, player, SoundEvents.CHORUS_FRUIT_TELEPORT, player.getSoundSource(), 1F, 0.7F);
			player.teleportTo(pos.getX(), pos.getY(), pos.getZ());
			spawnParticles(level, level.random, player);
		} else {
			player.displayClientMessage(Component.translatable("message.amulet.nopearls").withStyle(ChatFormatting.RED), true);
			level.playSound(null, player, ConnectionSounds.PLAYER_OR_PEARL_MISSING.get(), player.getSoundSource(), 0.6F, 0.5F);
		}

		updateTexture(itemstack);
		player.getCooldowns().addCooldown(this, 60);
		return InteractionResultHolder.consume(itemstack);
	}

	private static int pearlCost(float distance) {
		if (distance < 1000) return 1;
		if (distance < 10000) return 2;
		return 3;
	}

	public static BlockPos validPos(Player player) {
		Level level = player.level();
		RandomSource random = level.random;

		double randomX = player.getX() + random.nextIntBetweenInclusive(-8, 8);
		double randomZ = player.getZ() + random.nextIntBetweenInclusive(-8, 8);

		BlockPos pos = new BlockPos((int) randomX, (int) player.getY(), (int) player.getZ());

		if (!level.getBlockState(pos).isSuffocating(level, pos)
				&& !level.getBlockState(pos.above(1)).isSuffocating(level, pos.above(1))
				&& (level.getBlockState(pos.below()).isSolid() || level.getBlockState(pos.below(2)).isSolid())) {
			return pos;
		} else {
			for (int i = -8; i < 8; i++) {
				BlockPos newPos = new BlockPos((int) randomX, (int) (player.getY() + i), (int) randomZ);
				if (!level.getBlockState(newPos).isSuffocating(level, newPos)
						&& !level.getBlockState(newPos.above(1)).isSuffocating(level, newPos.above(1))
						&& (level.getBlockState(newPos.below()).isSolid() || level.getBlockState(newPos.below(2)).isSolid())) {
					return newPos;
				}
			}
		}
		return new BlockPos((int) player.getX(), (int) player.getY(), (int) player.getZ());
	}

	@Nullable
	public static Player getBoundPlayer(Player player, ItemStack itemstack, boolean sameDim) {
		Long id = getAmuletId(itemstack);
		if (id != null && player.level() instanceof ServerLevel serverLevel) {
			PlayerList players = serverLevel.getServer().getPlayerList();
			for (Player targetPlayer : players.getPlayers()) {
				if (arePlayersBound(player, itemstack, targetPlayer, sameDim)) {
					return targetPlayer;
				}
			}
		}
		return null;
	}


	public static boolean arePlayersBound(Player player, ItemStack original, Player target, boolean sameDimension) {
		if (sameDimension && target.level().dimension() != player.level().dimension()) return false;
		if (target == player) return false;

		Long id = getAmuletId(original);
		if (id == null) return false;

		Inventory inv = target.getInventory();
		for (int i = 0; i < inv.getContainerSize(); ++i) {
			if (hasMatchingId(id, inv.getItem(i))) return true;
		}
		return false;
	}

	public static boolean hasMatchingId(Long id, ItemStack s) {
		if (!(s.getItem() instanceof AmuletItem)) return false;
		Long other = getAmuletId(s);
		return other != null && other.equals(id);
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag flag) {
		super.appendHoverText(stack, context, tooltipComponents, flag);
		Long id = getAmuletId(stack);
		if (id != null) {
			tooltipComponents.add(Component.translatable("tooltip.amulet.id", id).withStyle(ChatFormatting.GRAY));
		}
		tooltipComponents.add(Component.translatable("tooltip.amulet.itemcount", getCurrentAmount(stack), AmuletContents.MAX_PEARLS).withStyle(ChatFormatting.GRAY));
		tooltipComponents.add(Component.translatable("tooltip.amulet.guide").withStyle(ChatFormatting.BLUE));
	}

	@Override
	public void onCraftedBy(ItemStack stack, Level level, Player player) {
		super.onCraftedBy(stack, level, player);
		if (getAmuletId(stack) == null && level instanceof ServerLevel serverLevel) {
			stack.set(ConnectionDataComponents.AMULET_ID.get(), HeartstoneData.getNewId(serverLevel));
		}
	}

	@Override
	public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction action, Player player) {
		if (action != ClickAction.SECONDARY) return false;
		if (stack.getCount() != 1) return false;

		AmuletContents contents = stack.getOrDefault(ConnectionDataComponents.AMULET_CONTENTS.get(), AmuletContents.EMPTY);
		AmuletContents.Mutable mutable = new AmuletContents.Mutable(contents);
		ItemStack clickedItem = slot.getItem();

		if (clickedItem.isEmpty()) {
			ItemStack removed = mutable.removeOne();
			if (removed != null) {
				player.playSound(ConnectionSounds.PEARL_REMOVED.get());
				ItemStack remainder = slot.safeInsert(removed);
				mutable.tryInsert(remainder);
			}
		} else if (clickedItem.is(Items.ENDER_PEARL)) {
			int inserted = mutable.tryInsert(clickedItem);
			if (inserted > 0) {
				player.playSound(ConnectionSounds.PEARL_INSERTED.get());
			}
		}

		stack.set(ConnectionDataComponents.AMULET_CONTENTS.get(), mutable.toImmutable());
		updateTexture(stack);
		return true;
	}

	@Override
	public boolean overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access) {
		if (action != ClickAction.SECONDARY) return false;
		if (stack.getCount() != 1) return false;
		if (!slot.allowModification(player)) return false;

		AmuletContents contents = stack.getOrDefault(ConnectionDataComponents.AMULET_CONTENTS.get(), AmuletContents.EMPTY);
		AmuletContents.Mutable mutable = new AmuletContents.Mutable(contents);

		if (other.isEmpty()) {
			ItemStack removed = mutable.removeOne();
			if (removed != null) {
				player.playSound(ConnectionSounds.PEARL_REMOVED.get());
				access.set(removed);
			}
		} else if (other.is(Items.ENDER_PEARL)) {
			int inserted = mutable.tryInsert(other);
			if (inserted > 0) {
				player.playSound(ConnectionSounds.PEARL_INSERTED.get());
			}
		}

		stack.set(ConnectionDataComponents.AMULET_CONTENTS.get(), mutable.toImmutable());
		updateTexture(stack);
		return true;
	}

	@Override
	public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
		AmuletContents contents = itemEntity.getItem().getOrDefault(ConnectionDataComponents.AMULET_CONTENTS.get(), AmuletContents.EMPTY);
		if (!contents.isEmpty()) {
			itemEntity.getItem().set(ConnectionDataComponents.AMULET_CONTENTS.get(), AmuletContents.EMPTY);
			ItemUtils.onContainerDestroyed(itemEntity, contents.itemsCopy());
		}
	}

	public static void consumePearls(ItemStack stack, int amount) {
		AmuletContents contents = stack.getOrDefault(ConnectionDataComponents.AMULET_CONTENTS.get(), AmuletContents.EMPTY);
		if (contents.isEmpty()) return;
		AmuletContents.Mutable mutable = new AmuletContents.Mutable(contents);
		mutable.consume(amount);
		stack.set(ConnectionDataComponents.AMULET_CONTENTS.get(), mutable.toImmutable());
	}

	public static void updateTexture(ItemStack stack) {
		int value = getCurrentAmount(stack) > 0 ? 1 : 0;
		stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(value));
	}

	public static void spawnParticles(Level level, RandomSource random, Player player) {
		for (int j = 0; j < 128; ++j) {
			double d0 = (double) j / 127.0D;
			float f = (random.nextFloat() - 0.5F) * 0.2F;
			float f1 = (random.nextFloat() - 0.5F) * 0.2F;
			float f2 = (random.nextFloat() - 0.5F) * 0.2F;
			double d1 = Mth.lerp(d0, player.xo, player.getX()) + (random.nextDouble() - 0.5D) * (double) player.getBbWidth() * 2.0D;
			double d2 = Mth.lerp(d0, player.yo, player.getY()) + random.nextDouble() * (double) player.getBbHeight();
			double d3 = Mth.lerp(d0, player.zo, player.getZ()) + (random.nextDouble() - 0.5D) * (double) player.getBbWidth() * 2.0D;
			if (!level.isClientSide) {
				((ServerLevel) level).sendParticles(ParticleTypes.PORTAL, d1, d2, d3, 1, f, f1, f2, 0D);
			}
		}
	}
}
