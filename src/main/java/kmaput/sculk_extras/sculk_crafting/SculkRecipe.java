package kmaput.sculk_extras.sculk_crafting;

import kmaput.sculk_extras.block.cocoon.SculkCocoonBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;

public interface SculkRecipe {
    void craft(SculkCocoonBlockEntity cocoon, @Nullable Direction incubatorFacing);
}
