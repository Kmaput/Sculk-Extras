package kmaput.sculk_extras.block.incubator;

import kmaput.sculk_extras.init.SEBlocks;
import kmaput.sculk_extras.init.SEItems;
import kmaput.sculk_extras.init.SETags;
import kmaput.sculk_extras.sculk_logistics.SculkItemBehaviour;
import kmaput.sculk_extras.sculk_logistics.SculkItemMover;
import kmaput.sculk_extras.util.DQuat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class SculkIncubatorBlock extends BaseEntityBlock implements SculkItemBehaviour, SculkBehaviour {
    public static SculkIncubatorBlock create() {
        return new SculkIncubatorBlock(BlockBehaviour.Properties.of(Material.SCULK).strength(3.0f, 3.0f).sound(SoundType.SCULK).randomTicks());
    }

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public SculkIncubatorBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any().setValue(FACING, Direction.UP));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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
            if (blockEntity instanceof SculkIncubatorBlockEntity incubator) {
                if (!level.isClientSide) {
                    level.setBlock(pos, SEBlocks.ECHOING_SCULK_INCUBATOR.get().defaultBlockState().setValue(FACING, state.getValue(FACING)), Block.UPDATE_CLIENTS);
                    if (level.getBlockEntity(pos) instanceof EchoingSculkIncubatorBlockEntity echoingIncubator) {
                        echoingIncubator.upgradeFrom(incubator, usedStack.copy());
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
        return SculkIncubatorBlockEntity.create(pos, state);
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (hasSoulEnergy(level, pos)) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SculkIncubatorBlockEntity incubator) {
                incubator.tryCraft();
            }
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (hasSoulEnergy(level, pos)) {
            if (random.nextInt(4) == 0) {
                Vec3 offset = DQuat.rotate(new Vec3(random.nextDouble()*1.1-0.55, 0.55, random.nextDouble()*1.1-0.55), DQuat.rotation(Direction.getRandom(random)));
                level.addParticle(ParticleTypes.SCULK_CHARGE_POP, pos.getX()+0.5+offset.x, pos.getY()+0.5+offset.y, pos.getZ()+0.5+offset.z, 0.0D, 0.0D, 0.0D);
            }
            Vec3 offset = DQuat.rotate(new Vec3(random.nextDouble()*0.6-0.3, 0.54, random.nextDouble()*0.6-0.3), DQuat.rotation(state.getValue(FACING)));
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, pos.getX()+0.5+offset.x, pos.getY()+0.5+offset.y, pos.getZ()+0.5+offset.z, 0.0D, 0.0D, 0.0D);
        }
    }

    public boolean hasSoulEnergy(Level level, BlockPos pos) {
        if (!level.isAreaLoaded(pos, 1)) return false;
        for(Direction direction : Direction.values()) {
            if (level.getBlockState(pos.relative(direction)).is(SETags.INCUBATOR_ENERGY_SOURCE)) {
                return true;
            }
        }
        return false;
    }

    @Nullable
    public <T extends BlockEntity> GameEventListener getListener(ServerLevel level, T blockEntity) {
        return blockEntity instanceof SculkIncubatorBlockEntity incubator ? incubator : null;
    }

    @Override
    public ItemStack handleItem(SculkItemMover.ItemCursor cursor, Level level) {
        BlockEntity blockEntity = level.getBlockEntity(cursor.getPos());
        if (blockEntity instanceof SculkIncubatorBlockEntity incubator) {
            return incubator.handleItem(cursor);
        }
        else {
            return SculkItemBehaviour.DEFAULT.handleItem(cursor, level);
        }
    }

    @Override
    public int attemptUseCharge(SculkSpreader.ChargeCursor cursor, LevelAccessor level, BlockPos sourcePos, RandomSource random, SculkSpreader spreader, boolean flag) {
        BlockEntity blockEntity = level.getBlockEntity(cursor.getPos());
        if (blockEntity instanceof SculkIncubatorBlockEntity incubator) {
            return incubator.handleCharge(cursor);
        }
        else {
            return cursor.getCharge();
        }
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader level, RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        return silkTouchLevel == 0 ? 10 : 0;
    }
}
