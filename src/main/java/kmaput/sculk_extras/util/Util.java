package kmaput.sculk_extras.util;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.items.IItemHandler;

import java.util.function.Predicate;

public class Util {
    public static boolean isRealEntity(Entity entity) {
        return !entity.isSpectator() && !(entity instanceof Player player && player.isCreative()) && !(entity instanceof ArmorStand armorStand && armorStand.isSpectator());
    }

    public static void dropItem(Level level, BlockPos pos, ItemStack item) {
        ItemEntity itemEntity = new ItemEntity(level, pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5, item);
        itemEntity.setDefaultPickUpDelay();
        level.addFreshEntity(itemEntity);
    }

    public static boolean breakBlock(Level level, BlockPos pos, ItemStack tool) {
        BlockState state = level.getBlockState(pos);
        FluidState fluidState = level.getFluidState(pos);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        Block.dropResources(state, level, pos, blockEntity, null, tool);
        if (level.setBlock(pos, fluidState.createLegacyBlock(), Block.UPDATE_ALL)) {
            level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(null, state));
            return true;
        }
        return false;
    }

    public static ItemStack extractStack(IItemHandler handler, Predicate<ItemStack> predicate) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack item = handler.getStackInSlot(i);
            if (!item.isEmpty() && predicate.test(item)) {
                ItemStack extracted = handler.extractItem(i, handler.getStackInSlot(i).getMaxStackSize(), false);
                if (!extracted.isEmpty()) {
                    return extracted;
                }
            }
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack extractStack(IItemHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            if (!handler.getStackInSlot(i).isEmpty()) {
                ItemStack extracted = handler.extractItem(i, handler.getStackInSlot(i).getMaxStackSize(), false);
                if (!extracted.isEmpty()) {
                    return extracted;
                }
            }
        }
        return ItemStack.EMPTY;
    }
}
