package kmaput.sculk_extras.sculk_crafting;

import kmaput.sculk_extras.block.cocoon.SculkCocoonBlockEntity;
import kmaput.sculk_extras.init.SEItemPredicateSerializers;
import kmaput.sculk_extras.init.SEItems;
import kmaput.sculk_extras.item.EchoShardFragment;
import kmaput.sculk_extras.sculk_logistics.predicates.LocatedPredicate;
import kmaput.sculk_extras.sculk_logistics.predicates.item.ItemCountPredicate;
import kmaput.sculk_extras.util.Util;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemCountRecipe implements SculkRecipe {
    @Override
    public void craft(SculkCocoonBlockEntity cocoon, @Nullable Direction incubatorFacing) {
        if (cocoon.getItems().size() != 3) return;
        ItemStack echoFragment = cocoon.popItem();
        ItemStack catalyst = cocoon.popItem();
        if (!echoFragment.is(SEItems.ECHO_SHARD_FRAGMENT.get()) || catalyst.getCount() < echoFragment.getCount()) {
            cocoon.addItem(catalyst);
            cocoon.addItem(echoFragment);
            return;
        }
        int count = cocoon.peekItem().getCount();
        EchoShardFragment.setItemPredicate(echoFragment, new LocatedPredicate<>(new ItemCountPredicate(count), SEItemPredicateSerializers.COUNT.getId()));
        catalyst.shrink(echoFragment.getCount());
        Util.dropItem(cocoon.getLevel(), cocoon.getBlockPos(), echoFragment);
        if (!catalyst.isEmpty()) {
            Util.dropItem(cocoon.getLevel(), cocoon.getBlockPos(), catalyst);
        }
        Util.breakBlock(cocoon.getLevel(), cocoon.getBlockPos(), ItemStack.EMPTY);
    }
}
