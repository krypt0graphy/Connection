package com.kryptography.connection.common.item;



import com.kryptography.connection.init.ModItems;
import com.kryptography.connection.init.ModSounds;
import net.mehvahdjukaar.heartstone.HeartstoneData;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;


public class AmuletItem extends Item {

    public AmuletItem(Properties pProperties) {
        super(pProperties);
    }

    public boolean isFoil(ItemStack pStack) {
        return getTotemId(pStack) != null;
    }

    public static int pearlAmountTag(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag.getInt("PearlAmount");
    }

    public static @Nullable Long getTotemId(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null && tag.contains("Id") ? tag.getLong("Id") : null;
    }


    //Teleport Functionality
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player player, InteractionHand pHand) {
        ItemStack itemstack = player.getItemInHand(pHand);
            if (pLevel.isClientSide) {
                return InteractionResultHolder.success(itemstack);
            } else {
                player.awardStat(Stats.ITEM_USED.get(this));
                boolean teleported = false;
                Player otherPlayer = getBoundPlayer(player, itemstack, true);
                RandomSource random = pLevel.random;
                if (otherPlayer != null) {

                    float distance = player.distanceTo(otherPlayer);
                    BlockPos pos = validPos(otherPlayer);

                    if (distance < 1000 && getCurrentAmount(itemstack) >= 1) {
                        usePearl(itemstack, 1);
                        pLevel.playSound(null, player, SoundEvents.CHORUS_FRUIT_TELEPORT, player.getSoundSource(), 1F, 0.7F);
                        player.teleportTo(pos.getX(), pos.getY(), pos.getZ());
                        spawnParticles(pLevel, random, player);
                        teleported = true;
                    }
                    if (distance > 1000 && distance < 9999 && getCurrentAmount(itemstack) >= 2) {
                        usePearl(itemstack, 2);
                        pLevel.playSound(null, player, SoundEvents.CHORUS_FRUIT_TELEPORT, player.getSoundSource(), 1F, 0.7F);
                        player.teleportTo(pos.getX(), pos.getY(), pos.getZ());
                        spawnParticles(pLevel, random, player);
                        teleported = true;
                    }
                    if (distance > 10000 && getCurrentAmount(itemstack) >= 3) {
                        usePearl(itemstack, 3);
                        pLevel.playSound(null, player, SoundEvents.CHORUS_FRUIT_TELEPORT, player.getSoundSource(), 1F, 0.7F);
                        player.teleportTo(pos.getX(), pos.getY(), pos.getZ());
                        spawnParticles(pLevel, random, player);
                        teleported = true;
                    } else if (getCurrentAmount(itemstack) < 1 && !teleported) {
                        player.displayClientMessage(Component.translatable("message.amulet.nopearls").withStyle(ChatFormatting.RED), true);
                        pLevel.playSound(null, player, SoundEvents.ENDER_EYE_DEATH, player.getSoundSource(), 0.6F, 0.5F);
                    }
                } else {
                    player.displayClientMessage(Component.translatable("message.amulet.noplayer").withStyle(ChatFormatting.RED), true);
                    pLevel.playSound((Player) null, player, SoundEvents.ENDER_EYE_DEATH, player.getSoundSource(), 0.6F, 0.5F);
                }
                updateTexture(itemstack);
                player.getCooldowns().addCooldown(this, 60);
                return InteractionResultHolder.consume(itemstack);
            }
    }

    public static BlockPos validPos(Player player) {
        Level level = player.level();
        RandomSource random = level.random;

        double randomX = player.getX() + random.nextIntBetweenInclusive(0, 3);
        double randomZ = player.getZ() + random.nextIntBetweenInclusive(0, 3);

        BlockPos pos = new BlockPos((int)randomX, (int)player.getY(), (int)player.getZ());

        if(level.getBlockState(pos).isAir() && (!level.getBlockState(pos.below()).isAir() || !level.getBlockState(pos.below(2)).isAir())) {
            return pos;
        } else {
            boolean found = false;
            if(!found) {
                for(int i = -30; i < 30; i++) {
                BlockPos newPos = new BlockPos((int) randomX, (int) (player.getY() + i), (int) randomZ);
                    if(level.getBlockState(newPos).isAir() && (!level.getBlockState(newPos.below()).isAir() || !level.getBlockState(newPos.below(2)).isAir())) {
                        found = true;
                        return newPos;
                    }
                }
            }

        }
        return new BlockPos((int) player.getX(), (int) player.getY(), (int) player.getZ());
    }

    public static @Nullable Player getBoundPlayer(Player player, ItemStack itemstack, boolean sameDim) {
        Long id = getTotemId(itemstack);
        if (id != null) {
            Level var5 = player.level();
            if (var5 instanceof ServerLevel) {
                ServerLevel serverLevel = (ServerLevel)var5;
                PlayerList players = serverLevel.getServer().getPlayerList();
                Iterator var6 = players.getPlayers().iterator();

                while(var6.hasNext()) {
                    Player targetPlayer = (Player)var6.next();
                    if (arePlayersBound(player, itemstack, targetPlayer, sameDim)) {
                        return targetPlayer;
                    }
                }
            }
        }

        return null;
    }
    

    public static boolean arePlayersBound(Player pPlayer, ItemStack original, Player target, boolean sameDimension) {
        if (sameDimension && target.level().dimension() != pPlayer.level().dimension()) {
            return false;
        } else {
            if (target != pPlayer) {
                Long id = getTotemId(original);
                if (id == null) {
                    return false;
                }

                Inventory inv = target.getInventory();

                for(int i = 0; i < inv.getContainerSize(); ++i) {
                    ItemStack s = inv.getItem(i);
                    if (hasMatchingId(id, s)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public static boolean hasMatchingId(Long id, ItemStack s) {
        if (!(s.getItem() instanceof AmuletItem)) {
            return false;
        } else {
            Long other = getTotemId(s);
            return other != null && other.equals(id);
        }
    }

    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        Long id = getTotemId(pStack);
        if (id != null) {
            pTooltipComponents.add(Component.translatable("tooltip.amulet.id", new Object[]{id}).withStyle(ChatFormatting.GRAY));
        }
        pTooltipComponents.add(Component.translatable("tooltip.amulet.itemcount", getCurrentAmount(pStack), 16).withStyle(ChatFormatting.GRAY));
        pTooltipComponents.add(Component.translatable("tooltip.amulet.guide").withStyle(ChatFormatting.BLUE));
    }

    public void onCraftedBy(ItemStack pStack, Level pLevel, Player pPlayer) {
        super.onCraftedBy(pStack, pLevel, pPlayer);
        CompoundTag tag = pStack.getOrCreateTag();
        if (!tag.contains("Id") && pLevel instanceof ServerLevel serverLevel) {
            tag.putLong("Id", HeartstoneData.getNewId(serverLevel));
        }
    }


    //Ender pearl storing

    public static final int MAX_PEARLS = 16;

    public static void add(ItemStack totemStack, ItemStack insert) {
        if (!insert.isEmpty()) {
            CompoundTag tag = totemStack.getOrCreateTag();

            if(!tag.contains("Pearls")) {
                tag.put("Pearls", new ListTag());
            }

            ListTag listTag = tag.getList("Pearls", 10);
            ItemStack insertCopy = insert.copyWithCount(insert.getCount());
            Optional<CompoundTag> matchingStackTag = getMatchingItem(insertCopy, listTag);


            if (matchingStackTag.isPresent()) {
                if(getCurrentAmount(totemStack) + insertCopy.getCount() <= MAX_PEARLS) {
                    CompoundTag matching = matchingStackTag.get();
                    ItemStack matchingStack = ItemStack.of(matching);

                    matchingStack.grow(insertCopy.getCount());
                    matchingStack.save(matching);
                    listTag.remove(matching);
                    listTag.add(matching);

                    insert.shrink(insertCopy.getCount());
                }
                if (getCurrentAmount(totemStack) + insertCopy.getCount() > MAX_PEARLS) {
                    int canAdd = MAX_PEARLS - getCurrentAmount(totemStack);
                    CompoundTag matching = matchingStackTag.get();
                    ItemStack matchingStack = ItemStack.of(matching);

                    matchingStack.grow(canAdd);
                    matchingStack.save(matching);
                    listTag.remove(matching);
                    listTag.add(matching);

                    insert.shrink(canAdd);
                }


            } else {
                if(getCurrentAmount(totemStack) + insertCopy.getCount() <= MAX_PEARLS) {
                    ItemStack itemStack = insert.copy();
                    CompoundTag newTag = new CompoundTag();
                    itemStack.save(newTag);
                    listTag.add(newTag);
                    insert.shrink(insertCopy.getCount());
                }

                if(getCurrentAmount(totemStack) + insertCopy.getCount() > MAX_PEARLS) {
                    int canAdd = MAX_PEARLS - getCurrentAmount(totemStack);

                    ItemStack itemStack = insert.copyWithCount(canAdd);
                    CompoundTag newTag = new CompoundTag();
                    itemStack.save(newTag);
                    listTag.add(newTag);
                    insert.shrink(canAdd);
                }
            }
        }
    }

    public static Optional<ItemStack> removeOne(ItemStack totemStack) {
        CompoundTag tag = totemStack.getOrCreateTag();
        if (!tag.contains("Pearls")) {
            return Optional.empty();
        }

        ListTag listTag = tag.getList("Pearls", 10);
        if (listTag.isEmpty()) {
            return Optional.empty();
        }

        CompoundTag compoundTag = listTag.getCompound(0);
        ItemStack itemStack = ItemStack.of(compoundTag);
        listTag.remove(0);
        if(listTag.isEmpty()) {
            tag.remove("Pearls");
        }
        return Optional.of(itemStack);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack pStack, Slot pSlot, ClickAction pAction, Player pPlayer) {
        if(pAction != ClickAction.SECONDARY) {
            return false;
        }

        if(pStack.getCount() != 1) {
            return false;
        }

        ItemStack clickedItem = pSlot.getItem();

        if(clickedItem.isEmpty()) {
            pPlayer.playSound(ModSounds.PEARL_REMOVED.get());
            removeOne(pStack).ifPresent((p_150740_) -> {
                add(pStack, pSlot.safeInsert(p_150740_));
            });

        } else if (clickedItem.is(Items.ENDER_PEARL)) {
            if(canAdd(pStack, clickedItem)) {
                add(pStack, clickedItem);
                pPlayer.playSound(ModSounds.PEARL_INSERTED.get());
            }
        }
        updateTexture(pStack);
        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack pStack, ItemStack pOther, Slot pSlot, ClickAction pAction, Player pPlayer, SlotAccess pAccess) {
        if(pAction != ClickAction.SECONDARY) {
            return false;
        }

        if(pStack.getCount() != 1) {
            return false;
        }

        if(pSlot.allowModification(pPlayer)) {
            if(pOther.isEmpty()) {
                removeOne(pStack).ifPresent((p_150740_) -> {
                    pPlayer.playSound(ModSounds.PEARL_REMOVED.get());
                    pAccess.set(p_150740_);
                });
                pPlayer.playSound(ModSounds.PEARL_REMOVED.get());
            } else {
                if(canAdd(pStack, pOther)) {
                    add(pStack, pOther);
                    pPlayer.playSound(ModSounds.PEARL_INSERTED.get());
                }
            }
        }
        updateTexture(pStack);
        return true;
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity, DamageSource damageSource) {
        ItemUtils.onContainerDestroyed(itemEntity, getContent(itemEntity.getItem()));
    }

    public boolean canAdd(ItemStack totemStack, ItemStack insert) {
        if (!totemStack.is(ModItems.AMULET_OF_CONNECTION.get())) {
            return false;
        }
        CompoundTag tag = totemStack.getOrCreateTag();
        if(!tag.contains("Pearls")) {
            tag.put("Pearls", new ListTag());
        }
        ListTag listTag = tag.getList("Pearls", 10);
        if(listTag.isEmpty()) {
            return true;
        } else {
            return getCurrentAmount(totemStack) < MAX_PEARLS;
        }
    }

    public static Optional<CompoundTag> getMatchingItem(ItemStack pStack, ListTag pList) {
        return pList.stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).filter((p_186350_) -> {
            return ItemStack.isSameItemSameTags(ItemStack.of(p_186350_), pStack);
        }).findFirst();
    }

    public static Optional<CompoundTag> getMatchingItemNoNbt(ItemStack pStack, ListTag pList) {
        return pList.stream().filter(CompoundTag.class::isInstance).map(CompoundTag.class::cast).filter((p_186350_) -> {
            return ItemStack.isSameItem(ItemStack.of(p_186350_), pStack);
        }).findFirst();
    }

    public static Stream<ItemStack> getContent(ItemStack totemStack) {
        CompoundTag tag = totemStack.getOrCreateTag();
        if (tag == null) {
            return Stream.empty();
        } else {
            ListTag listtag = tag.getList("Pearls", 10);
            return listtag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
        }
    }

    public static void usePearl(ItemStack totemStack, int amount) {
        CompoundTag tag = totemStack.getOrCreateTag();
        if(tag==null) {
            return;
        } else {
            ListTag listtag = tag.getList("Pearls", 10);
            Optional<CompoundTag> matchingItem = getMatchingItemNoNbt(new ItemStack(Items.ENDER_PEARL), listtag);
            if(matchingItem.isPresent()) {
                CompoundTag matchingItemTag = matchingItem.get();
                ItemStack matchingStack = ItemStack.of(matchingItemTag);
                matchingStack.shrink(amount);
                matchingStack.save(matchingItemTag);
                listtag.remove(matchingItemTag);
                listtag.add(matchingItemTag);
            }
        }

    }

    public static void updateTexture(ItemStack pStack) {
        if(getCurrentAmount(pStack) > 0) {
            pStack.getTag().putFloat("CustomModelData", 1F);
        }
        if(getCurrentAmount(pStack) == 0) {
            pStack.getTag().putFloat("CustomModelData", 0F);
        }
    }

    public static int getCurrentAmount(ItemStack totemStack) {
        return getContent(totemStack).mapToInt(ItemStack::getCount).sum();
    }

    public static void spawnParticles(Level level, RandomSource random, Player player) {
        int i = 128;

        for(int j = 0; j < 128; ++j) {
            double d0 = (double)j / 127.0D;
            float f = (random.nextFloat() - 0.5F) * 0.2F;
            float f1 = (random.nextFloat() - 0.5F) * 0.2F;
            float f2 = (random.nextFloat() - 0.5F) * 0.2F;
            double d1 = Mth.lerp(d0, player.xo, player.getX()) + (random.nextDouble() - 0.5D) * (double)player.getBbWidth() * 2.0D;
            double d2 = Mth.lerp(d0, player.yo, player.getY()) + random.nextDouble() * (double)player.getBbHeight();
            double d3 = Mth.lerp(d0, player.zo, player.getZ()) + (random.nextDouble() - 0.5D) * (double)player.getBbWidth() * 2.0D;
            if(!level.isClientSide) {
                ((ServerLevel) level).sendParticles(ParticleTypes.PORTAL, d1, d2, d3, 1, (double)f, (double)f1, (double)f2, 0D);
            }
        }
    }
}
