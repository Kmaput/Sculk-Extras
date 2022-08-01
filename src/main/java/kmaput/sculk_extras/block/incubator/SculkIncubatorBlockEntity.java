package kmaput.sculk_extras.block.incubator;

import kmaput.sculk_extras.block.cocoon.SculkCocoonBlockEntity;
import kmaput.sculk_extras.init.SEBlockEntityTypes;
import kmaput.sculk_extras.init.SEBlocks;
import kmaput.sculk_extras.init.SETags;
import kmaput.sculk_extras.sculk_crafting.SculkCrafting;
import kmaput.sculk_extras.sculk_crafting.SculkRecipe;
import kmaput.sculk_extras.sculk_logistics.MoverHelper;
import kmaput.sculk_extras.sculk_logistics.SculkItemMover;
import kmaput.sculk_extras.util.NoPlayerPlaceContext;
import kmaput.sculk_extras.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SculkSpreader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class SculkIncubatorBlockEntity extends BlockEntity implements GameEventListener {
    public static final int COCOON_COST = 4; // TODO move to server config?

    private LazyOptional<IItemHandler> cachedHandler = LazyOptional.empty();
    private final BlockPositionSource blockPosSource = new BlockPositionSource(this.worldPosition);

    public static SculkIncubatorBlockEntity create(BlockPos pos, BlockState state) {
        return new SculkIncubatorBlockEntity(SEBlockEntityTypes.SCULK_INCUBATOR.get(), pos, state);
    }

    public SculkIncubatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Nonnull
    protected ItemStack handleItem(SculkItemMover.ItemCursor cursor) {
        Direction dir = this.getFacing();
        BlockPos targetPos = this.worldPosition.relative(dir);

        if(this.level.isInWorldBounds(targetPos) && this.level.getBlockState(targetPos).getMaterial().isReplaceable() && cursor.getItem().is(SETags.INCUBATOR_PLACEABLE)) {
            if(cursor.getItem().getItem() instanceof BlockItem blockItem) {
                InteractionResult result = blockItem.place(new NoPlayerPlaceContext(this.level, targetPos, dir.getOpposite(), cursor.getItem()));
                return cursor.getItem();
            }
        }

        BlockEntity targetBlockEntity = level.getBlockEntity(targetPos);
        if (targetBlockEntity instanceof SculkCocoonBlockEntity cocoon) {
            if (cocoon.addItem(cursor.getItem())) return ItemStack.EMPTY;
            return cursor.getItem();
        }

        LazyOptional<IItemHandler> optionalHandler = this.getTargetedHandler();
        if (optionalHandler.isPresent()) {
            IItemHandler handler = optionalHandler.orElse(new ItemStackHandler(0));
            return ItemHandlerHelper.insertItemStacked(handler, cursor.getItem(), false);
        }

        // the hopper code does not check container max stack size, and it is not worth rewriting that mess just for composter integration
//        Container container = HopperBlockEntity.getContainerAt(this.level, targettedPos);
//        if (container != null) {
//            return HopperBlockEntity.addItem(null, container, cursor.getItem(), dir.getOpposite());
//        }

        if (MoverHelper.checkSpaceEjectable(level, targetPos, dir.getOpposite())) {
            MoverHelper.ejectItem(cursor, level, dir);
            return ItemStack.EMPTY;
        }

        return cursor.getItem();
    }

    protected void tryCraft() {
        BlockPos targetPos = this.worldPosition.relative(this.getFacing());
        BlockEntity targetBlockEntity = this.level.getBlockEntity(targetPos);
        if (targetBlockEntity instanceof SculkCocoonBlockEntity cocoon) {
            SculkRecipe recipe = SculkCrafting.getRecipe(cocoon.getItems());
            if (recipe != null) {
                recipe.craft(cocoon, this.getFacing());
            }
        }
    }

    protected int handleCharge(SculkSpreader.ChargeCursor cursor) {
        if (this.level == null) return cursor.getCharge();
        BlockPos targetPos = cursor.getPos().relative(this.getFacing());

        if (cursor.getCharge() >= COCOON_COST && level.getBlockState(targetPos).getMaterial().isReplaceable()) {
            level.setBlock(targetPos, SEBlocks.SCULK_COCOON.get().defaultBlockState(), Block.UPDATE_ALL);
            return cursor.getCharge() - COCOON_COST;
        }

        BlockEntity blockEntity = level.getBlockEntity(targetPos);
        if (blockEntity instanceof SculkCocoonBlockEntity cocoon) {
            return cocoon.insertCharge(cursor.getCharge());
        }

        return cursor.getCharge();
    }

    @Override
    public PositionSource getListenerSource() {
        return this.blockPosSource;
    }

    @Override
    public int getListenerRadius() {
        return 8;
    }

    @Override
    public boolean handleGameEvent(ServerLevel level, GameEvent.Message message) {
        if (this.isRemoved()) return false;
        if (message.gameEvent() == GameEvent.EXPLODE) {
            BlockPos targetPos = this.worldPosition.relative(this.getFacing());
            BlockState targetState = this.level.getBlockState(targetPos);

            if (targetState.is(SETags.INCUBATOR_POPPABLE)) {
                ItemStack breaker = new ItemStack(Items.NETHERITE_HOE, 1);
                breaker.enchant(Enchantments.SILK_TOUCH, 1);
                if (Util.breakBlock(level, targetPos, breaker)) {
                    level.gameEvent(GameEvent.BLOCK_DESTROY, targetPos, GameEvent.Context.of(targetState));
                    level.playSound(null, targetPos, SoundEvents.SCULK_BLOCK_BREAK, SoundSource.BLOCKS, 1.0f, 1.0f);
                }
            }

            return true;
        }
        return false;
    }

    protected Direction getFacing() {
        return this.getBlockState().getValue(SculkIncubatorBlock.FACING);
    }

    public LazyOptional<IItemHandler> getTargetedHandler() {
        assert level != null;
        if (!cachedHandler.isPresent()) {
            Direction dir = this.getFacing();
            BlockEntity targetBlockEntity = this.level.getBlockEntity(this.worldPosition.relative(dir));
            if (targetBlockEntity != null) {
                LazyOptional<IItemHandler> handler = targetBlockEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite());
                if (handler.isPresent()) {
                    handler.addListener(this::clearCachedHandler);
                    this.cachedHandler = handler;
                }
            }
        }
        return cachedHandler;
    }

    public void clearCachedHandler(LazyOptional<IItemHandler> handler) {
        if (handler == this.cachedHandler) {
            this.cachedHandler = LazyOptional.empty();
        }
    }
}
