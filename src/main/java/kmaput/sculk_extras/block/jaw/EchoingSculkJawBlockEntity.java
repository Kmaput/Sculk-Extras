package kmaput.sculk_extras.block.jaw;

import kmaput.sculk_extras.block.cocoon.SculkCocoonBlockEntity;
import kmaput.sculk_extras.init.SEBlockEntityTypes;
import kmaput.sculk_extras.init.SEItems;
import kmaput.sculk_extras.item.EchoShardFragment;
import kmaput.sculk_extras.sculk_logistics.predicates.sculk_tuning.SculkAction;
import kmaput.sculk_extras.sculk_logistics.predicates.sculk_tuning.SculkActionHandler;
import kmaput.sculk_extras.sculk_logistics.predicates.sculk_tuning.SculkPunishable;
import kmaput.sculk_extras.util.NBTUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;

public class EchoingSculkJawBlockEntity extends SculkJawBlockEntity implements SculkPunishable {
    protected EchoShardFragment.Handler echoHolder = new EchoShardFragment.Handler(new ItemStack(SEItems.ECHO_SHARD_FRAGMENT.get()));
    protected SculkActionHandler<Item> lastActionHandler = new SculkActionHandler<>();

    public static EchoingSculkJawBlockEntity create(BlockPos pos, BlockState state) {
        return new EchoingSculkJawBlockEntity(SEBlockEntityTypes.ECHOING_SCULK_JAW.get(), pos, state);
    }

    public EchoingSculkJawBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void upgradeFrom(SculkJawBlockEntity jaw, ItemStack item) {
        this.echoHolder = new EchoShardFragment.Handler(item.split(1));
        this.mover = jaw.mover;
        this.deployProgress = jaw.deployProgress;
        this.eatingTimer = jaw.eatingTimer;
        this.currentItemHandlerSlot = jaw.currentItemHandlerSlot;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        CompoundTag echoFragmentTag = new CompoundTag();
        echoHolder.save(echoFragmentTag);
        tag.put("echoFragment", echoFragmentTag);
        tag.put("lastAction", SculkActionHandler.save(lastActionHandler, NBTUtil::saveItem));
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        this.echoHolder = EchoShardFragment.Handler.load(tag.getCompound("echoFragment"));
        this.lastActionHandler = SculkActionHandler.load(tag.getCompound("lastAction"), NBTUtil::loadItem, Tag.TAG_STRING);
        super.load(tag);
    }

    public void dropFragment(Level level) {
        Block.popResource(level, this.worldPosition, this.echoHolder.item);
    }

    public PunishResult punish() {
        SculkAction<Item> lastAction = lastActionHandler.getLastAction(this.level.getGameTime());
        if (lastAction == null) return PunishResult.FAILURE;
        ItemStack echoFragment = echoHolder.item;
        PunishResult result = EchoShardFragment.punish(echoFragment, lastAction);
        if (result == PunishResult.SUCCESS) {
            lastActionHandler.clear();
            echoHolder = new EchoShardFragment.Handler(echoFragment);
        }
        return result;
    }

    public PunishResult invert() {
        ItemStack echoFragment = echoHolder.item;
        PunishResult result = EchoShardFragment.invert(echoFragment);
        if (result == PunishResult.SUCCESS) {
            echoHolder = new EchoShardFragment.Handler(echoFragment);
        }
        return result;
    }

    @Override
    protected boolean eatFromItemHandler(IItemHandler handler) {
        ItemStack item = handler.getStackInSlot(this.currentItemHandlerSlot);
        ItemStack simulated = handler.extractItem(this.currentItemHandlerSlot, item.getMaxStackSize(), true);
        if (!simulated.isEmpty()) {
            if (this.echoHolder.predicate.test(simulated)) {
                ItemStack extracted = handler.extractItem(this.currentItemHandlerSlot, item.getMaxStackSize(), false);
                lastActionHandler.executeAction(simulated.getItem(), SculkAction.Type.ACTION, this.level.getGameTime());
                this.spawnCursor(extracted);
                return true;
            }
            else {
                lastActionHandler.executeAction(simulated.getItem(), SculkAction.Type.IGNORE, this.level.getGameTime());
                return false;
            }
        }
        return false;
    }

    @Override
    protected boolean eatFromCocoon(SculkCocoonBlockEntity cocoon) {
        ItemStack item = cocoon.peekItem();
        if (!item.isEmpty()) {
            if (this.echoHolder.predicate.test(item)) {
                lastActionHandler.executeAction(cocoon.popItem().getItem(), SculkAction.Type.ACTION, this.level.getGameTime());
                this.spawnCursor(item);
                return true;
            }
            else {
                lastActionHandler.executeAction(item.getItem(), SculkAction.Type.IGNORE, this.level.getGameTime());
                return false;
            }
        }
        return false;
    }

    @Override
    protected boolean eatItemEntity(ItemEntity item) {
        if (this.echoHolder.predicate.test(item.getItem())) {
            lastActionHandler.executeAction(item.getItem().getItem(), SculkAction.Type.ACTION, this.level.getGameTime());
            this.spawnCursor(item);
            item.discard();
            return true;
        }
        else {
            lastActionHandler.executeAction(item.getItem().getItem(), SculkAction.Type.IGNORE, this.level.getGameTime());
            return false;
        }
    }
}
