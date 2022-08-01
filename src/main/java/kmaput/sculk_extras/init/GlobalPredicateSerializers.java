package kmaput.sculk_extras.init;

import kmaput.sculk_extras.sculk_logistics.predicates.GlobalPredicateSerializer;
import net.minecraft.world.item.ItemStack;

public class GlobalPredicateSerializers {
    private static GlobalPredicateSerializer<ItemStack> CACHED_ITEM;

    public static GlobalPredicateSerializer<ItemStack> item() {
        if (CACHED_ITEM == null) {
            if (SERegistries.ITEM_PREDICATE_SERIALIZERS == null) return null;
            CACHED_ITEM = new GlobalPredicateSerializer<>(SERegistries.ITEM_PREDICATE_SERIALIZERS);
        }
        return CACHED_ITEM;
    }
}
