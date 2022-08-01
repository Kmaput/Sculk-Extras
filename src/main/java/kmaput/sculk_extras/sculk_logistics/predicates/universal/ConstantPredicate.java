package kmaput.sculk_extras.sculk_logistics.predicates.universal;

import kmaput.sculk_extras.sculk_logistics.predicates.GlobalPredicateSerializer;
import kmaput.sculk_extras.sculk_logistics.predicates.PredicateSerializer;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Predicate;

public class ConstantPredicate<T> implements Predicate<T> {
    private final boolean value;

    public ConstantPredicate(boolean value) {
        this.value = value;
    }

    @Override
    public boolean test(T t) {
        return value;
    }

    public static <T> PredicateSerializer<T> getSerializer(GlobalPredicateSerializer<T> globalSerializer) {
        return new PredicateSerializer<>() {
            @Override
            public void save(Predicate<T> p, CompoundTag tag) {
                if (p instanceof ConstantPredicate<T> predicate) {
                    tag.putBoolean("value", predicate.value);
                }
            }

            @Override
            public Predicate<T> load(CompoundTag tag) {
                return new ConstantPredicate<>(tag.getBoolean("value"));
            }
        };
    }
}
