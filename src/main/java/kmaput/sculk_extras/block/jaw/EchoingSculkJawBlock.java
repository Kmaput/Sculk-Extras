package kmaput.sculk_extras.block.jaw;

import kmaput.sculk_extras.block.incubator.EchoingSculkIncubatorBlockEntity;
import kmaput.sculk_extras.init.SEBlockEntityTypes;
import kmaput.sculk_extras.sculk_logistics.predicates.sculk_tuning.SculkPunishable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;
import java.util.List;

public class EchoingSculkJawBlock extends SculkJawBlock {
    public static EchoingSculkJawBlock create() {
        return new EchoingSculkJawBlock(BlockBehaviour.Properties.of(Material.SCULK).strength(3.0f, 3.0f).sound(SoundType.SCULK));
    }

    public EchoingSculkJawBlock(Properties properties) {
        super(properties);
    }



    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        InteractionResult tryInvertResult = SculkPunishable.invert(level, pos, player, player.getItemInHand(hand), hit);
        return tryInvertResult;
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return EchoingSculkJawBlockEntity.create(pos, state);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, SEBlockEntityTypes.ECHOING_SCULK_JAW.get(), world.isClientSide ? EchoingSculkJawBlockEntity::clientTick : EchoingSculkJawBlockEntity::serverTick);
    }
    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder context) {
        ItemStack tool = context.getOptionalParameter(LootContextParams.TOOL);
        if (tool == null || tool.getEnchantmentLevel(Enchantments.SILK_TOUCH) == 0) {
            BlockEntity blockentity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            if (blockentity instanceof EchoingSculkJawBlockEntity echoingJaw) {
                echoingJaw.dropFragment(context.getLevel());
            }
        }
        return super.getDrops(state, context);
    }
}
