package kmaput.sculk_extras.sculk_logistics.predicates.item;

import kmaput.sculk_extras.sculk_logistics.predicates.GlobalPredicateSerializer;
import kmaput.sculk_extras.sculk_logistics.predicates.PredicateSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class ItemMatchPredicate implements Predicate<ItemStack> {
    private final ItemStack expected;
    private final boolean checkNBT;

    public ItemMatchPredicate(ItemStack expected, boolean checkNBT) {
        this.expected = expected.copy();
        this.checkNBT = checkNBT;
    }

    @Override
    public boolean test(ItemStack item) {
        if (!expected.sameItem(item)) return false;
        if (checkNBT) {
            if(expected.hasTag() != item.hasTag()) return false;
            return !expected.hasTag() || expected.getTag().equals(item.getTag()) || expected.areCapsCompatible(item);
        }
        return true;
    }

    public static PredicateSerializer<ItemStack> getSerializer(GlobalPredicateSerializer<ItemStack> globalSerializer) {
        return new PredicateSerializer<>() {
            @Override
            public void save(Predicate<ItemStack> p, CompoundTag tag) {
                if (p instanceof ItemMatchPredicate predicate) {
                    CompoundTag itemTag = new CompoundTag();
                    predicate.expected.save(itemTag);
                    itemTag.remove("Count");
                    tag.put("item", itemTag);
                    tag.putBoolean("checkNBT", predicate.checkNBT);
                }
            }

            @Override
            public Predicate<ItemStack> load(CompoundTag tag) {
                ItemStack item = ItemStack.of(tag.getCompound("item"));
                item.setCount(1);
                return new ItemMatchPredicate(item, tag.getBoolean("checkNBT"));
            }
        };
    }
}
