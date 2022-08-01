package kmaput.sculk_extras.sculk_logistics.predicates.item;

import kmaput.sculk_extras.sculk_logistics.predicates.GlobalPredicateSerializer;
import kmaput.sculk_extras.sculk_logistics.predicates.PredicateSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class ItemCountPredicate implements Predicate<ItemStack> {
    private final int requiredCount;

    public ItemCountPredicate(int requiredCount) {
        this.requiredCount = requiredCount;
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return itemStack.getCount() >= requiredCount;
    }

    public static PredicateSerializer<ItemStack> getSerializer(GlobalPredicateSerializer<ItemStack> globalSerializer) {
        return new PredicateSerializer<>() {
            @Override
            public void save(Predicate<ItemStack> p, CompoundTag tag) {
                if(p instanceof ItemCountPredicate predicate) {
                    tag.putInt("count", predicate.requiredCount);
                }
            }

            @Override
            public Predicate<ItemStack> load(CompoundTag tag) {
                return new ItemCountPredicate(tag.getInt("count"));
            }
        };
    }
}
