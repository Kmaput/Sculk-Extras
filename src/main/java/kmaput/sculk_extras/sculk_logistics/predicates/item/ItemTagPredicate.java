package kmaput.sculk_extras.sculk_logistics.predicates.item;

import kmaput.sculk_extras.sculk_logistics.predicates.GlobalPredicateSerializer;
import kmaput.sculk_extras.sculk_logistics.predicates.PredicateSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class ItemTagPredicate implements Predicate<ItemStack> {
    private final TagKey<Item> tag;

    public ItemTagPredicate(TagKey<Item> tag) {
        this.tag = tag;
    }

    @Override
    public boolean test(ItemStack item) {
        return item.is(tag);
    }

    public static PredicateSerializer<ItemStack> getSerializer(GlobalPredicateSerializer<ItemStack> globalSerializer) {
        return new PredicateSerializer<>() {
            @Override
            public void save(Predicate<ItemStack> p, CompoundTag tag) {
                if (p instanceof ItemTagPredicate predicate) {
                    tag.putString("tag", predicate.tag.location().toString());
                }
            }

            @Override
            public Predicate<ItemStack> load(CompoundTag tag) {
                return new ItemTagPredicate(ItemTags.create(new ResourceLocation(tag.getString("tag"))));
            }
        };
    }
}
