package kmaput.sculk_extras.block.cocoon;

import kmaput.sculk_extras.init.SEBlockEntityTypes;
import kmaput.sculk_extras.init.SETags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import net.minecraft.nbt.Tag;
import net.minecraft.world.Containers;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class SculkCocoonBlockEntity extends BlockEntity {
    // TODO move to server config?
    public static final int MAX_STACKS = 128;
    public static final int MAX_XP = 80;
    public static final int XP_TRANSFER_LIMIT = 5;

    private NonNullList<ItemStack> items = NonNullList.create();
    private int xp = 0;

    public SculkCocoonBlockEntity(BlockPos pos, BlockState state) {
        super(SEBlockEntityTypes.SCULK_COCOON.get(), pos, state);
    }

    public void dropContent() {
        if(level == null || level.isClientSide) return;
        Containers.dropContents(level, worldPosition, items);
    }

    public boolean isEmpty() {
        return items.isEmpty() && xp == 0;
    }

    public boolean addItem(ItemStack item) {
        if (items.size() == MAX_STACKS || item.isEmpty()) return false;
        if (item.is(SETags.COCOON_DISALLOWED)) return false;
        items.add(item);
        return true;
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    public ItemStack popItem() {
        if (items.size() == 0) return ItemStack.EMPTY;
        return items.remove(items.size()-1);
    }

    public ItemStack peekItem() {
        if (items.size() == 0) return ItemStack.EMPTY;
        return items.get(items.size()-1);
    }

    public ItemStack popItemIf(Predicate<ItemStack> predicate) {
        if (predicate.test(this.peekItem())) {
            return this.popItem();
        }
        return ItemStack.EMPTY;
    }

    public int insertCharge(int charge) {
        int usedCharge = Math.min(charge, Math.min(XP_TRANSFER_LIMIT, MAX_XP-this.xp));
        this.xp += usedCharge;
        return charge-usedCharge;
    }

    public int getStoredXp() {
        return xp;
    }

    public int getAnalogOutput() {
        return (items.size()*14/MAX_STACKS)+(items.isEmpty() ? 0 : 1);
    }

    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ListTag itemsTag = new ListTag();
        for (ItemStack itemStack : this.items) {
            CompoundTag elementTag = new CompoundTag();
            itemStack.save(elementTag);
            itemsTag.add(elementTag);
        }
        tag.put("Items", itemsTag);
        tag.putInt("Xp", xp);
    }

    public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.create();
        ListTag itemsTag = tag.getList("Items", Tag.TAG_COMPOUND);
        for(int i = 0; i < itemsTag.size()/* && i < MAX_STACKS*/; i++) {
            items.add(ItemStack.of(itemsTag.getCompound(i)));
        }
        this.xp = tag.getInt("Xp");
    }
}
