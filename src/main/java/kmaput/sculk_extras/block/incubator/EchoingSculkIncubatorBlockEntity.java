package kmaput.sculk_extras.block.incubator;

import kmaput.sculk_extras.init.SEBlockEntityTypes;
import kmaput.sculk_extras.init.SEItems;
import kmaput.sculk_extras.item.EchoShardFragment;
import kmaput.sculk_extras.sculk_logistics.*;
import kmaput.sculk_extras.sculk_logistics.predicates.sculk_tuning.SculkAction;
import kmaput.sculk_extras.sculk_logistics.predicates.sculk_tuning.SculkActionHandler;
import kmaput.sculk_extras.sculk_logistics.predicates.sculk_tuning.SculkPunishable;
import kmaput.sculk_extras.util.Directions;
import kmaput.sculk_extras.util.NBTUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;

public class EchoingSculkIncubatorBlockEntity extends SculkIncubatorBlockEntity implements SculkPunishable {
    protected EchoShardFragment.Handler echoHolder = new EchoShardFragment.Handler(new ItemStack(SEItems.ECHO_SHARD_FRAGMENT.get()));
    protected SculkActionHandler<Item> lastActionHandler = new SculkActionHandler<>();

    public static EchoingSculkIncubatorBlockEntity create(BlockPos pos, BlockState state) {
        return new EchoingSculkIncubatorBlockEntity(SEBlockEntityTypes.ECHOING_SCULK_INCUBATOR.get(), pos, state);
    }

    public EchoingSculkIncubatorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }


    public void upgradeFrom(SculkIncubatorBlockEntity incubator, ItemStack item) {
        this.echoHolder = new EchoShardFragment.Handler(item.split(1));
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
    @Nonnull
    protected ItemStack handleItem(SculkItemMover.ItemCursor cursor) {
        if (echoHolder.predicate.test(cursor.getItem())) {
            this.lastActionHandler.executeAction(cursor.getItem().getItem(), SculkAction.Type.ACTION, this.level.getGameTime());
            return super.handleItem(cursor);
        }
        else {
            this.lastActionHandler.executeAction(cursor.getItem().getItem(), SculkAction.Type.IGNORE, this.level.getGameTime());
            return cursor.getItem();
        }
    }

    protected void handleStopItem(SculkItemMover.ItemCursor cursor) {
        Directions validDirections = Directions.all();
        if (echoHolder.predicate.test(cursor.getItem())) {
            validDirections = validDirections.subtract(Directions.from(this.getFacing()));
        }
        MoverHelper.handleEjectItem(cursor, this.level, validDirections.toArray());
    }
}
