package kmaput.sculk_extras.block.cocoon;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.List;

public class SculkCocoonBlock extends BaseEntityBlock {
    public static SculkCocoonBlock create() {
        return new SculkCocoonBlock(Properties.of(Material.SCULK).strength(1.2f).sound(SoundType.SCULK));
    }

    public SculkCocoonBlock(Properties properties) {
        super(properties);
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }



    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SculkCocoonBlockEntity(pos, state);
    }

    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        BlockEntity blockentity = world.getBlockEntity(pos);
        if (blockentity instanceof SculkCocoonBlockEntity cocoon) {
            return cocoon.getAnalogOutput();
        }
        return 0;
    }

    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockentity = world.getBlockEntity(pos);
            if (blockentity instanceof SculkCocoonBlockEntity cocoon) {
                world.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, world, pos, newState, moving);
        }
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SculkCocoonBlockEntity cocoon) {
            if (!world.isClientSide && player.isCreative() && !cocoon.isEmpty()) {
                ItemStack itemstack = new ItemStack(this);
                cocoon.saveToItem(itemstack);
                ItemEntity itementity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemstack);
                itementity.setDefaultPickUpDelay();
                world.addFreshEntity(itementity);
            }
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder context) {
        ItemStack tool = context.getOptionalParameter(LootContextParams.TOOL);
        if (tool == null || tool.getEnchantmentLevel(Enchantments.SILK_TOUCH) == 0) {
            BlockEntity blockentity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            if (blockentity instanceof SculkCocoonBlockEntity cocoon) {
                cocoon.dropContent();
            }
        }
        return super.getDrops(state, context);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack itemStack = super.getCloneItemStack(state, target, level, pos, player);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SculkCocoonBlockEntity cocoon) {
            cocoon.saveToItem(itemStack);
        }
        return itemStack;
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        if (silkTouchLevel > 0) return 0;
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof SculkCocoonBlockEntity cocoon) {
            return cocoon.getStoredXp();
        }
        return 0;
    }
}
