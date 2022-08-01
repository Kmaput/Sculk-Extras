package kmaput.sculk_extras.sculk_logistics;

import kmaput.sculk_extras.init.SETags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface SculkItemBehaviour {
    SculkItemBehaviour DEFAULT = new SculkItemBehaviour() {};

    default int getUpdateDelay() {
        return 2;
    }

    default @Nonnull ItemStack handleItem(SculkItemMover.ItemCursor cursor, Level level) {
        return cursor.getItem();
    }

    default @Nullable BlockPos move(SculkItemMover.ItemCursor cursor, Level level) {
        for(Vec3i offset : MoverHelper.getShuffledNeighbours(MoverHelper.getUnobstructedSides(level, cursor.getPos()), level.getRandom())) {
            BlockPos destination = cursor.getPos().offset(offset);
            if (cursor.getLastPos() != null && destination.distManhattan(cursor.getLastPos()) <= 1) continue;
            BlockState blockstate = level.getBlockState(destination);
            if (blockstate.is(SETags.TRASPORTS_ITEM_CHARGE)) {
                return destination;
            }
        }
        return null;
    }

    default void handleStopItem(SculkItemMover.ItemCursor cursor, Level level) {
        MoverHelper.handleEjectItem(cursor, level, Direction.values());
    }

}
