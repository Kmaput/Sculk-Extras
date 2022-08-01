package kmaput.sculk_extras.sculk_logistics.predicates;

import kmaput.sculk_extras.SculkExtras;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.function.Supplier;

public class GlobalPredicateSerializer<T> {
    private final Supplier<IForgeRegistry<PredicateSerializer<T>>> registrySupplier;

    public GlobalPredicateSerializer(Supplier<IForgeRegistry<PredicateSerializer<T>>> registrySupplier) {
        this.registrySupplier = registrySupplier;
    }

    public LocatedPredicate<T> load(CompoundTag tag) {
        ResourceLocation resourceLocation = new ResourceLocation(tag.getString("type"));
        PredicateSerializer<T> serializer = registrySupplier.get().getValue(resourceLocation);
        if (serializer != null) {
            return new LocatedPredicate<>(serializer.load(tag), resourceLocation);
        }
        else {
            SculkExtras.LOGGER.error("No serializer found for predicate type " + resourceLocation);
            return null;
        }
    }

    public void save(LocatedPredicate<T> locatedPredicate, CompoundTag tag) {
        ResourceLocation resourceLocation = locatedPredicate.resourceLocation();
        tag.putString("type", resourceLocation.toString());
        PredicateSerializer<T> serializer = registrySupplier.get().getValue(resourceLocation);
        if (serializer != null) {
            serializer.save(locatedPredicate.predicate(), tag);
        }
        else {
            SculkExtras.LOGGER.error("No serializer found for predicate type " + resourceLocation);
        }
    }
}
