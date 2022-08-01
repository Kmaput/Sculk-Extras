package kmaput.sculk_extras.block.jaw;

import kmaput.sculk_extras.init.SEBlockEntityTypes;
import kmaput.sculk_extras.init.SEBlocks;
import kmaput.sculk_extras.init.SEItems;
import kmaput.sculk_extras.sculk_logistics.MoverHelper;
import kmaput.sculk_extras.sculk_logistics.SculkItemBehaviour;
import kmaput.sculk_extras.sculk_logistics.SculkItemMover;
import kmaput.sculk_extras.util.Directions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class SculkJawBlock extends BaseEntityBlock implements SculkItemBehaviour {
    public static SculkJawBlock create() {
        return new SculkJawBlock(BlockBehaviour.Properties.of(Material.SCULK).strength(3.0f, 3.0f).sound(SoundType.SCULK));
    }

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final BooleanProperty EATING = BooleanProperty.create("eating");

    public SculkJawBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP).setValue(EATING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, EATING);
    }

    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext placeContext) {
        return this.defaultBlockState().setValue(FACING, placeContext.getNearestLookingDirection().getOpposite());
    }

    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }



    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack usedStack = player.getItemInHand(hand);
        if (usedStack.is(SEItems.ECHO_SHARD_FRAGMENT.get())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SculkJawBlockEntity jaw) {
                if (!level.isClientSide) {
                    level.setBlock(pos, SEBlocks.ECHOING_SCULK_JAW.get().defaultBlockState().setValue(FACING, state.getValue(FACING)).setValue(EATING, state.getValue(EATING)), Block.UPDATE_CLIENTS);
                    if (level.getBlockEntity(pos) instanceof EchoingSculkJawBlockEntity echoingJaw) {
                        echoingJaw.upgradeFrom(jaw, usedStack.copy());
                    }
                    if (!player.isCreative()) {
                        usedStack.shrink(1);
                    }
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return SculkJawBlockEntity.create(pos, state);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, SEBlockEntityTypes.SCULK_JAW.get(), world.isClientSide ? SculkJawBlockEntity::clientTick : SculkJawBlockEntity::serverTick);
    }

    @Override
    public void handleStopItem(SculkItemMover.ItemCursor cursor, Level level) {
        Direction facing = level.getBlockState(cursor.getPos()).getValue(FACING);
        Directions validDirections = Directions.all().subtract(Directions.from(facing));
        MoverHelper.handleEjectItem(cursor, level, validDirections.toArray());
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockentity = level.getBlockEntity(pos);
            if (blockentity instanceof SculkJawBlockEntity jaw) {
                jaw.onRemove(level);
            }
            super.onRemove(state, level, pos, newState, moving);
        }
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        return silkTouchLevel == 0 ? 5 : 0;
    }
}
