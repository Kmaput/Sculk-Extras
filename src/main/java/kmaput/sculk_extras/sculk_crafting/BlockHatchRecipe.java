package kmaput.sculk_extras.sculk_crafting;

import kmaput.sculk_extras.block.cocoon.SculkCocoonBlockEntity;
import kmaput.sculk_extras.util.NoPlayerPlaceContext;
import kmaput.sculk_extras.util.Util;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;

public class BlockHatchRecipe implements SculkRecipe {
    public static final int SCULK_MATERIAL = 30;
    public static final int SCULK_VEIN_MATERIAL = 3;

    private final int boneCost;
    private final int sculkCost;
    private final int xpCost;
    private final BlockItem output;

    public BlockHatchRecipe(int boneCost, int sculkCost, int xpCost, BlockItem output) {
        this.boneCost = boneCost;
        this.sculkCost = sculkCost;
        this.xpCost = xpCost;
        this.output = output;
    }

    @Override
    public void craft(SculkCocoonBlockEntity cocoon, @Nullable Direction incubatorFacing) {
        if (cocoon.getItems().size() < 3) return;
        ItemStack veins = cocoon.popItem();
        ItemStack catalyst = cocoon.popItem();
        if (!veins.is(Items.SCULK_VEIN) || !checkRequirements(cocoon)) {
            cocoon.addItem(catalyst);
            cocoon.addItem(veins);
            return;
        }
        cocoon.getItems().clear();
        Util.breakBlock(cocoon.getLevel(), cocoon.getBlockPos(), ItemStack.EMPTY);
        if (incubatorFacing == null) incubatorFacing = Direction.UP;
        output.place(new NoPlayerPlaceContext(cocoon.getLevel(), cocoon.getBlockPos(), incubatorFacing.getOpposite(), new ItemStack(output)));
    }

    private boolean checkRequirements(SculkCocoonBlockEntity cocoon) {
        int sculkMaterial = 0;
        int convertibleMaterial = 0;
        int boneMaterial = 0;
        int xpMaterial = cocoon.getStoredXp();
        for (ItemStack stack : cocoon.getItems()) {
            if (stack.is(Items.SCULK)) {
                sculkMaterial += stack.getCount()*SCULK_MATERIAL;
            }
            else if (stack.is(Items.SCULK_VEIN)) {
                sculkMaterial += stack.getCount()*SCULK_VEIN_MATERIAL;
            }
            else if (stack.is(Items.BONE_MEAL)) {
                boneMaterial += stack.getCount();
            }
            else if (stack.is(Items.BONE)) {
                boneMaterial += stack.getCount()*3;
            }
            else if (stack.is(Items.BONE_BLOCK)) {
                boneMaterial += stack.getCount()*9;
            }
            else {
                boolean isUsable = false;
                if (stack.getItem() instanceof BlockItem blockItem) {
                    if (blockItem.getBlock().builtInRegistryHolder().is(BlockTags.SCULK_REPLACEABLE)) {
                        isUsable = true;
                        convertibleMaterial += stack.getCount()*SCULK_MATERIAL;
                    }
                }
                if (!isUsable) {
                    return false;
                }
            }
        }
        if (boneCost > boneMaterial) return false;
        if (sculkCost > sculkMaterial) {
            int diff = sculkCost-sculkMaterial;
            convertibleMaterial -= diff;
            if (convertibleMaterial < 0) return false;
            xpMaterial -= (diff / SCULK_MATERIAL + (diff % SCULK_MATERIAL == 0 ? 0 : 1));
        }
        return xpCost <= xpMaterial;
    }
}
