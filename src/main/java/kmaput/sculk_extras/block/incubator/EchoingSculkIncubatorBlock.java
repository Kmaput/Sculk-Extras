package kmaput.sculk_extras.block.incubator;

import kmaput.sculk_extras.block.cocoon.SculkCocoonBlockEntity;
import kmaput.sculk_extras.sculk_logistics.SculkItemBehaviour;
import kmaput.sculk_extras.sculk_logistics.SculkItemMover;
import kmaput.sculk_extras.sculk_logistics.predicates.sculk_tuning.SculkPunishable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class EchoingSculkIncubatorBlock extends SculkIncubatorBlock {
    public static EchoingSculkIncubatorBlock create() {
        return new EchoingSculkIncubatorBlock(BlockBehaviour.Properties.of(Material.SCULK).strength(3.0f, 3.0f).sound(SoundType.SCULK).randomTicks());
    }

    public EchoingSculkIncubatorBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }



    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult tryInvertResult = SculkPunishable.invert(level, pos, player, player.getItemInHand(hand), hit);
        return tryInvertResult;
    }

    @Override @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return EchoingSculkIncubatorBlockEntity.create(pos, state);
    }

    @Override
    public void handleStopItem(SculkItemMover.ItemCursor cursor, Level level) {
        BlockEntity blockEntity = level.getBlockEntity(cursor.getPos());
        if (blockEntity instanceof EchoingSculkIncubatorBlockEntity incubator) {
            incubator.handleStopItem(cursor);
        }
        else {
            SculkItemBehaviour.DEFAULT.handleStopItem(cursor, level);
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder context) {
        ItemStack tool = context.getOptionalParameter(LootContextParams.TOOL);
        if (tool == null || tool.getEnchantmentLevel(Enchantments.SILK_TOUCH) == 0) {
            BlockEntity blockentity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            if (blockentity instanceof EchoingSculkIncubatorBlockEntity echoingIncubator) {
                echoingIncubator.dropFragment(context.getLevel());
            }
        }
        return super.getDrops(state, context);
    }
}
