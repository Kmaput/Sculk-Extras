package kmaput.sculk_extras.init;

import kmaput.sculk_extras.SculkExtras;
import kmaput.sculk_extras.sculk_logistics.predicates.GlobalPredicateSerializer;
import kmaput.sculk_extras.sculk_logistics.predicates.PredicateSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class SERegistries {
    public static final ResourceLocation ITEM_PREDICATE_SERIALIZERS_NAME = new ResourceLocation(SculkExtras.MODID, "item_predicate_serializers");
    public static Supplier<IForgeRegistry<PredicateSerializer<ItemStack>>> ITEM_PREDICATE_SERIALIZERS;

    public static void onNewRegistry(NewRegistryEvent event) {
        ITEM_PREDICATE_SERIALIZERS = event.create(new RegistryBuilder<PredicateSerializer<ItemStack>>()
                .setName(ITEM_PREDICATE_SERIALIZERS_NAME)
                .disableSync());
    }
}
