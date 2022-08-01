package kmaput.sculk_extras.block.jaw;

import kmaput.sculk_extras.init.SEBlockEntityTypes;
import kmaput.sculk_extras.block.cocoon.SculkCocoonBlockEntity;
import kmaput.sculk_extras.block.incubator.SculkIncubatorBlock;
import kmaput.sculk_extras.sculk_logistics.SculkItemMover;
import kmaput.sculk_extras.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.*;

public class SculkJawBlockEntity extends BlockEntity {
    // TODO move to server config?
    public static final int MAX_MOVED = 16;
    public static final int DEPLOY_TIME = 8;
    public static final int EATING_TIME = 60;
    public static final Vec3 SLOWDOWN = new Vec3(0.7, 0.5, 0.7);

    public static final DamageSource DAMAGE_SOURCE = new DamageSource("sculkJaw");

    protected LazyOptional<IItemHandler> cachedHandler = LazyOptional.empty();
    protected int currentItemHandlerSlot = 0;

    protected SculkItemMover mover = new SculkItemMover();
    protected int deployProgress = 0;
    protected int eatingTimer = 0;

    public static SculkJawBlockEntity create(BlockPos pos, BlockState state) {
        return new SculkJawBlockEntity(SEBlockEntityTypes.SCULK_JAW.get(), pos, state);
    }

    public SculkJawBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        this.mover.save(tag);
        tag.putInt("deployProgress", deployProgress);
        tag.putInt("eatingTimer", eatingTimer);
        tag.putInt("currentItemHandlerSlot", currentItemHandlerSlot);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        this.mover.load(tag);
        this.deployProgress = tag.getInt("deployProgress");
        this.eatingTimer = tag.getInt("eatingTimer");
        this.currentItemHandlerSlot = tag.getInt("currentItemHandlerSlot");
        super.load(tag);
    }

    public void onRemove(Level level) {
        this.mover.dropAllItems(level);
    }

    @OnlyIn(Dist.CLIENT)
    public static void clientTick(Level level, BlockPos pos, BlockState state, SculkJawBlockEntity blockEntity) {
        if (!state.getValue(SculkJawBlock.EATING)) return;
        Player player = net.minecraft.client.Minecraft.getInstance().player;
        if (player != null && !player.isCreative() && !player.isSpectator() && player.getBoundingBox().intersects(blockEntity.getAffectedVolume())) {
            player.makeStuckInBlock(state, SLOWDOWN);
        }
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, SculkJawBlockEntity blockEntity) {
        blockEntity.mover.updateCursors(level);
        BlockPos targetPos = pos.relative(blockEntity.getFacing());

        if (blockEntity.canSpawnCursor()) {
            LazyOptional<IItemHandler> optionalHandler = blockEntity.getTargetedHandler();
            if (optionalHandler.isPresent()) {
                IItemHandler handler = optionalHandler.orElse(new ItemStackHandler(0));
                if (blockEntity.currentItemHandlerSlot < 0) blockEntity.currentItemHandlerSlot = 0;
                if (++blockEntity.currentItemHandlerSlot >= handler.getSlots()) blockEntity.currentItemHandlerSlot = 0;
                if (blockEntity.eatFromItemHandler(handler)) {
                    blockEntity.startEating();
                }
                return;
            }
            BlockEntity targettedBlockEntity = level.getBlockEntity(targetPos);
            if (targettedBlockEntity instanceof SculkCocoonBlockEntity cocoon) {
                if (blockEntity.eatFromCocoon(cocoon)) {
                    blockEntity.startEating();
                }
                return;
            }
        }

        List<Entity> entities = blockEntity.getEntities();
        boolean extendJaw = false;
        for (Entity entity : entities) {
            if (entity instanceof ItemEntity item) {
                if (blockEntity.canSpawnCursor()) {
                    if (blockEntity.eatItemEntity(item)) {
                        blockEntity.startEating();
                    }
                }
            }
            else {
                if (blockEntity.eatEntity(entity)) {
                    extendJaw = true;
                    if (blockEntity.deployProgress == DEPLOY_TIME) {
                        blockEntity.startEating();
                    }
                }
            }
        }

        if (extendJaw) {
            blockEntity.deployProgress++;
            if (blockEntity.deployProgress > DEPLOY_TIME) blockEntity.deployProgress = DEPLOY_TIME;
        }
        else {
            blockEntity.deployProgress--;
            if (blockEntity.deployProgress < 0) blockEntity.deployProgress = 0;
        }

        if (--blockEntity.eatingTimer == 0) {
            blockEntity.stopEating();
        }
        if (blockEntity.eatingTimer < 0) blockEntity.eatingTimer = 0;
    }

    protected boolean eatFromItemHandler(IItemHandler handler) {
        ItemStack item = handler.getStackInSlot(this.currentItemHandlerSlot);
        ItemStack extracted = handler.extractItem(this.currentItemHandlerSlot, item.getMaxStackSize(), false);
        if(!extracted.isEmpty()) {
            this.spawnCursor(extracted);
            return true;
        }
        return false;
    }

    protected boolean eatFromCocoon(SculkCocoonBlockEntity cocoon) {
        ItemStack extracted = cocoon.popItem();
        if(!extracted.isEmpty()) {
            this.spawnCursor(extracted);
            return true;
        }
        return false;
    }

    protected boolean eatItemEntity(ItemEntity item) {
        this.spawnCursor(item);
        item.discard();
        return true;
    }

    protected boolean eatEntity(Entity entity) {
        if (!(entity instanceof Warden) && Util.isRealEntity(entity)) {
            if (this.deployProgress == DEPLOY_TIME) {
                entity.hurt(DAMAGE_SOURCE, 3.0f);
                entity.makeStuckInBlock(this.getBlockState(), SLOWDOWN);
                if (entity instanceof LivingEntity living) {
                    living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 40, 1));
                }
            }
            return true;
        }
        return false;
    }

    public void stopEating() {
        if (this.getBlockState().getValue(SculkJawBlock.EATING)) {
            BlockState newState = this.getBlockState().setValue(SculkJawBlock.EATING, false);
            this.level.setBlock(this.worldPosition, newState, Block.UPDATE_ALL);
            setChanged(this.level, this.worldPosition, newState);
        }
        this.eatingTimer = 0;
    }

    public void startEating() {
        if (!this.getBlockState().getValue(SculkJawBlock.EATING)) {
            BlockState newState = this.getBlockState().setValue(SculkJawBlock.EATING, true);
            this.level.setBlock(this.worldPosition, newState, Block.UPDATE_ALL);
            setChanged(this.level, this.worldPosition, newState);
        }
        this.eatingTimer = EATING_TIME;
    }

    protected boolean canSpawnCursor() {
        return this.mover.getMovedAmount() < MAX_MOVED;
    }

    protected void spawnCursor(ItemEntity item) {
        this.mover.addCursor(new SculkItemMover.ItemCursor(this.worldPosition, item));
    }

    protected void spawnCursor(ItemStack item) {
        this.mover.addCursor(new SculkItemMover.ItemCursor(this.worldPosition, item, item.getEntityLifespan(this.level)));
    }

    public List<Entity> getEntities() {
        return level.getEntities((Entity) null, this.getAffectedVolume(), EntitySelector.ENTITY_STILL_ALIVE);
    }

    public AABB getAffectedVolume() {
        BlockPos targetPos = this.worldPosition.relative(this.getFacing());
        return AABB.unitCubeFromLowerCorner(new Vec3(targetPos.getX(), targetPos.getY(), targetPos.getZ()));
    }

    private Direction getFacing() {
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
