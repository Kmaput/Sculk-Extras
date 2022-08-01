package kmaput.sculk_extras.sculk_logistics;

import kmaput.sculk_extras.util.NBTUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.*;

public class SculkItemMover {
    public static final int EJECT_TIME = 600;

    private List<ItemCursor> cursors = new ArrayList<>();

    public int getMovedAmount() {
        return cursors.size();
    }

    public void addCursor(ItemCursor cursor) {
        if (cursor.getItem().isEmpty()) return;
        this.cursors.add(cursor);
    }

    public void save(CompoundTag tag) {
        ListTag listTag = new ListTag();
        for (ItemCursor cursor : cursors) {
            listTag.add(cursor.save());
        }
        tag.put("cursors", listTag);
    }

    public void load(CompoundTag tag) {
        cursors.clear();
        ListTag listTag = tag.getList("cursors", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            cursors.add(ItemCursor.load(listTag.getCompound(i)));
        }
    }

    public void updateCursors(Level level) {
        if (!this.cursors.isEmpty()) {
            List<ItemCursor> newCursors = new ArrayList<>();
            for (ItemCursor cursor : this.cursors) {
                if (cursor.update(level)) {
                    newCursors.add(cursor);
                }
            }
            this.cursors = newCursors;
        }
    }

    public void dropAllItems(Level level) {
        for (ItemCursor cursor : this.cursors) {
            cursor.drop(level);
        }
    }

    public static class ItemCursor {

        private BlockPos pos;
        @Nullable
        private BlockPos lastPos;
        private ItemStack item;
        private int updateDelay;
        private int itemLifespan;
        private int ejectTime;

        public ItemCursor(BlockPos pos, ItemEntity itemEntity) {
            this(pos, itemEntity.getItem().copy(), itemEntity.lifespan);
        }

        public ItemCursor(BlockPos pos, ItemStack stack, int itemDespawnTimer) {
            this.pos = pos;
            this.lastPos = null;
            this.item = stack;
            this.itemLifespan = itemDespawnTimer;
            this.updateDelay = 1;
            this.ejectTime = EJECT_TIME;
        }

        public ItemCursor(BlockPos pos, @Nullable BlockPos lastPos, ItemStack item, int updateDelay, int itemLifespan, int ejectTime) {
            this.pos = pos;
            this.lastPos = lastPos;
            this.item = item;
            this.updateDelay = updateDelay;
            this.itemLifespan = itemLifespan;
            this.ejectTime = ejectTime;
        }

        public ItemStack getItem() {
            return item;
        }

        public BlockPos getPos() {
            return pos;
        }

        public @Nullable BlockPos getLastPos() {
            return this.lastPos;
        }

        public int getItemLifespan() {
            return itemLifespan;
        }

        public CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.put("pos", NBTUtil.saveBlockPos(this.pos));
            if (lastPos != null) {
                tag.put("last_pos", NBTUtil.saveBlockPos(this.lastPos));
            }
            CompoundTag itemTag = new CompoundTag();
            this.item.save(itemTag);
            tag.put("item", itemTag);
            tag.putInt("update_delay", this.updateDelay);
            tag.putInt("item_life", this.itemLifespan);
            tag.putInt("eject_time", this.ejectTime);
            return tag;
        }

        public static ItemCursor load(CompoundTag tag) {
            BlockPos pos = NBTUtil.loadBlockPos(tag.getIntArray("pos"));
            BlockPos lastPos = null;
            if (tag.contains("last_pos")) {
                lastPos = NBTUtil.loadBlockPos(tag.getIntArray("last_pos"));
            }
            ItemStack item = ItemStack.of(tag.getCompound("item"));
            int updateDelay = tag.getInt("update_delay");
            int itemLifespan = tag.getInt("item_life");
            int ejectTime = tag.getInt("eject_time");
            return new ItemCursor(pos, lastPos, item, updateDelay, itemLifespan, ejectTime);
        }

        public void drop(Level level) {
            ItemEntity entity = new ItemEntity(level, this.pos.getX()+0.5, this.pos.getY()+0.5, this.pos.getZ()+0.5, this.item);
            entity.setDefaultPickUpDelay();
            level.addFreshEntity(entity);
        }

        public boolean update(Level level) {
            if (level.isAreaLoaded(this.pos, 1)) {
                this.itemLifespan--;
                if(this.itemLifespan <= 0) return false;
                if (--this.updateDelay <= 0) {
                    SculkItemBehaviour behaviour = currentBehaviour(level);
                    if (this.move(behaviour.move(this, level))) {
                        level.levelEvent(LevelEvent.PARTICLES_SCULK_CHARGE, pos, 1);
                        behaviour = currentBehaviour(level);
                        this.item = behaviour.handleItem(this, level);
                        if (this.item.isEmpty()) return false;
                        this.updateDelay = behaviour.getUpdateDelay();
                    }
                    else {
                        behaviour.handleStopItem(this, level);
                        this.item = ItemStack.EMPTY;
                        return false;
                    }
                }
                this.ejectTime--;
                if (this.ejectTime <= 0) {
                    SculkItemBehaviour behaviour = currentBehaviour(level);
                    behaviour.handleStopItem(this, level);
                    this.item = ItemStack.EMPTY;
                    return false;
                }
            }
            return true;
        }

        private boolean move(BlockPos newPos) {
            if (newPos == null) return false;
            this.lastPos = this.pos;
            this.pos = newPos;
            return true;
        }

        private SculkItemBehaviour currentBehaviour(Level level) {
            BlockState state = level.getBlockState(this.pos);
            if (state.getBlock() instanceof SculkItemBehaviour sculkbehaviour) {
                return sculkbehaviour;
            }
            return SculkItemBehaviour.DEFAULT;
        }
    }
}
