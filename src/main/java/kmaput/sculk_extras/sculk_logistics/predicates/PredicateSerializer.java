package kmaput.sculk_extras.sculk_logistics.predicates;

import net.minecraft.nbt.CompoundTag;

import java.util.function.Predicate;

public interface PredicateSerializer<T> {
    void save(Predicate<T> predicate, CompoundTag tag);
    Predicate<T> load(CompoundTag tag);
}
