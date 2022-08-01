package kmaput.sculk_extras.sculk_logistics.predicates.sculk_tuning;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public interface SculkPunishable {
    PunishResult punish();
    PunishResult invert();

    enum PunishResult {
        SUCCESS,
        FAILURE,
        UNMODIFIABLE
    }

    static InteractionResult punish(Level level, BlockPos pos, Player player, ItemStack used, BlockHitResult hit) {
        if (used.is(Items.FLINT_AND_STEEL)) {
            if (level.getBlockEntity(pos) instanceof SculkPunishable punishable) {
                if (!player.isShiftKeyDown()) {
                    if (!level.isClientSide) {
                        ServerLevel serverLevel = (ServerLevel) level;
                        RandomSource random = level.getRandom();
                        SculkPunishable.PunishResult result = punishable.punish();
                        if (result == SculkPunishable.PunishResult.SUCCESS) {
                            if (!player.isCreative()) {
                                used.setDamageValue(used.getDamageValue()-1);
                            }
                            serverLevel.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0f, random.nextFloat() * 0.4f + 0.8f);
                            serverLevel.sendParticles(ParticleTypes.FLAME, hit.getLocation().x, hit.getLocation().y, hit.getLocation().z, 5, 0.125, 0.125, 0.125, 0);
                            serverLevel.sendParticles(ParticleTypes.ANGRY_VILLAGER, pos.getX()+0.5, pos.getY() + 1.05, pos.getZ(), 5, 0.3, 0.05, 0.3, 0.02);
                        }
                        else {
                            serverLevel.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (random.nextFloat()-random.nextFloat()) * 0.8F);
                            serverLevel.sendParticles(ParticleTypes.SMOKE, hit.getLocation().x, hit.getLocation().y, hit.getLocation().z, 5, 0.125, 0.125, 0.125, 0);
                            if (result == SculkPunishable.PunishResult.UNMODIFIABLE) {
                                player.displayClientMessage(Component.translatable("item.sculk_extras.echo_shard_fragment.change.unmodifiable"), true);
                            }
                            if (result == SculkPunishable.PunishResult.FAILURE) {
                                player.displayClientMessage(Component.translatable("item.sculk_extras.echo_shard_fragment.change.punish.fail"), true);
                            }
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }

    static InteractionResult invert(Level level, BlockPos pos, Player player, ItemStack used, BlockHitResult hit) {
        if (used.is(Items.FERMENTED_SPIDER_EYE)) {
            if (level.getBlockEntity(pos) instanceof SculkPunishable punishable) {
                if (!player.isShiftKeyDown()) {
                    if (!level.isClientSide) {
                        ServerLevel serverLevel = (ServerLevel) level;
                        RandomSource random = level.getRandom();
                        SculkPunishable.PunishResult result = punishable.invert();
                        if (result == SculkPunishable.PunishResult.SUCCESS) {
                            if (!player.isCreative()) {
                                used.shrink(1);
                            }
                            serverLevel.playSound(null, pos, SoundEvents.REDSTONE_TORCH_BURNOUT, SoundSource.BLOCKS, 0.5F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
                            serverLevel.sendParticles(DustParticleOptions.REDSTONE, hit.getLocation().x, hit.getLocation().y, hit.getLocation().z, 5, 0.125, 0.125, 0.125, 0);
                            player.displayClientMessage(Component.translatable("item.sculk_extras.echo_shard_fragment.change.invert.success"), true);
                        }
                        else {
                            serverLevel.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.6f + (random.nextFloat()-random.nextFloat()) * 0.8F);
                            serverLevel.sendParticles(ParticleTypes.SMOKE, hit.getLocation().x, hit.getLocation().y, hit.getLocation().z, 5, 0.125, 0.125, 0.125, 0);
                            if (result == SculkPunishable.PunishResult.UNMODIFIABLE) {
                                player.displayClientMessage(Component.translatable("item.sculk_extras.echo_shard_fragment.change.unmodifiable"), true);
                            }
                        }
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.PASS;
    }
}
