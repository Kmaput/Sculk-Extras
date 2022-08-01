package kmaput.sculk_extras.item;

import kmaput.sculk_extras.init.GlobalPredicateSerializers;
import kmaput.sculk_extras.init.SEItemPredicateSerializers;
import kmaput.sculk_extras.sculk_logistics.predicates.sculk_tuning.SculkAction;
import kmaput.sculk_extras.sculk_logistics.predicates.sculk_tuning.SculkPunishable;
import kmaput.sculk_extras.sculk_logistics.predicates.item.SculkMemoryItemPredicate;
import kmaput.sculk_extras.sculk_logistics.predicates.LocatedPredicate;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Predicate;

public class EchoShardFragment extends Item {
    public static EchoShardFragment create() {
        return new EchoShardFragment(new Properties().tab(CreativeModeTab.TAB_MISC));
    }

    public EchoShardFragment(Properties properties) {
        super(properties);
    }

    public void appendHoverText(ItemStack item, Level level, List<Component> components, TooltipFlag tooltipFlag) {
        if (item.hasTag()) {
            LocatedPredicate<ItemStack> predicate = getItemPredicate(item);
            if (predicate.predicate() instanceof SculkMemoryItemPredicate) {
                components.add(Component.translatable("item.sculk_extras.echo_shard_fragment.natural").withStyle(ChatFormatting.GRAY));
            }
            else {
                components.add(Component.translatable("item.sculk_extras.echo_shard_fragment.artificial").withStyle(ChatFormatting.GRAY));
            }
        }
    }

    public static void setItemPredicate(ItemStack item, LocatedPredicate<ItemStack> predicate) {
        CompoundTag tag = item.getOrCreateTag();
        CompoundTag itemPredicateTag = new CompoundTag();
        GlobalPredicateSerializers.item().save(predicate, itemPredicateTag);
        tag.put("items", itemPredicateTag);
    }

    public static LocatedPredicate<ItemStack> getItemPredicate(ItemStack item) {
        if (item.hasTag()) {
            CompoundTag tag = item.getTag();
            if (tag.contains("items")) {
                LocatedPredicate<ItemStack> predicate = GlobalPredicateSerializers.item().load(tag.getCompound("items"));
                if (predicate != null) {
                    return predicate;
                }
            }
        }
        return getDefaultItemPredicate();
    }

    public static LocatedPredicate<ItemStack> getDefaultItemPredicate() {
        return new LocatedPredicate<>(new SculkMemoryItemPredicate(), SEItemPredicateSerializers.SCULK_MEMORY.getId());
    }

    public static SculkPunishable.PunishResult punish(ItemStack item, SculkAction<Item> reason) {
        LocatedPredicate<ItemStack> predicate = getItemPredicate(item);
        if (predicate.predicate() instanceof SculkMemoryItemPredicate sculkMemoryPredicate) {
            sculkMemoryPredicate.punish(reason);
            setItemPredicate(item, predicate);
            return SculkPunishable.PunishResult.SUCCESS;
        }
        return SculkPunishable.PunishResult.UNMODIFIABLE;
    }

    public static SculkPunishable.PunishResult invert(ItemStack item) {
        LocatedPredicate<ItemStack> predicate = getItemPredicate(item);
        if (predicate.predicate() instanceof SculkMemoryItemPredicate sculkMemoryPredicate) {
            sculkMemoryPredicate.invert();
            setItemPredicate(item, predicate);
            return SculkPunishable.PunishResult.SUCCESS;
        }
        return SculkPunishable.PunishResult.UNMODIFIABLE;
    }

    public static class Handler {
        public final ItemStack item;
        public final Predicate<ItemStack> predicate;

        public Handler(ItemStack item) {
            this.item = item;
            this.predicate = getItemPredicate(item).predicate();
        }

        public void save(CompoundTag tag) {
            item.save(tag);
        }

        public static Handler load(CompoundTag tag) {
            ItemStack item = ItemStack.of(tag);
            return new Handler(item);
        }
    }
}
