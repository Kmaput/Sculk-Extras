package kmaput.sculk_extras.sculk_logistics.predicates.item;

import kmaput.sculk_extras.sculk_logistics.predicates.sculk_tuning.SculkAction;
import kmaput.sculk_extras.sculk_logistics.predicates.GlobalPredicateSerializer;
import kmaput.sculk_extras.sculk_logistics.predicates.PredicateSerializer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Optional;
import java.util.function.Predicate;

public class SculkMemoryItemPredicate implements Predicate<ItemStack> {
    private boolean inverted;
    private final HashSet<Item> acceptedItems;

    public SculkMemoryItemPredicate() {
        this(new HashSet<>(), false);
    }

    public SculkMemoryItemPredicate(HashSet<Item> acceptedItems, boolean inverted) {
        this.acceptedItems = acceptedItems;
        this.inverted = inverted;
    }

    @Override
    public boolean test(ItemStack item) {
        return acceptedItems.contains(item.getItem()) == !inverted;
    }

    public void invert() {
        this.inverted = !this.inverted;
    }

    public void punish(SculkAction<Item> reason) {
        if (inverted == (reason.type() == SculkAction.Type.ACTION)) {
            acceptedItems.addAll(reason.targets());
        }
        else {
            reason.targets().forEach(acceptedItems::remove);
        }
    }

    public static PredicateSerializer<ItemStack> getSerializer(GlobalPredicateSerializer<ItemStack> globalSerializer) {
        return new PredicateSerializer<>() {
            @Override
            public void save(Predicate<ItemStack> p, CompoundTag tag) {
                if (p instanceof SculkMemoryItemPredicate predicate) {
                    ListTag itemsTag = new ListTag();
                    predicate.acceptedItems.stream()
                            .flatMap(item -> Optional.ofNullable(ForgeRegistries.ITEMS.getKey(item)).stream())
                            .sorted()
                            .forEach(resourceLocation -> itemsTag.add(StringTag.valueOf(resourceLocation.toString())));
                    tag.put("items", itemsTag);
                    tag.putBoolean("inverted", predicate.inverted);
                }
            }

            @Override
            public Predicate<ItemStack> load(CompoundTag tag) {
                ListTag itemsTag = tag.getList("items", Tag.TAG_STRING);
                HashSet<Item> builder = new HashSet<>();
                for (int i = 0; i < itemsTag.size(); i++) {
                    Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemsTag.getString(i)));
                    if (item != null) {
                        builder.add(item);
                    }
                }
                return new SculkMemoryItemPredicate(builder, tag.getBoolean("inverted"));
            }
        };
    }
}
