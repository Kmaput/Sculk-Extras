package kmaput.sculk_extras.sculk_logistics.predicates.universal;

import kmaput.sculk_extras.sculk_logistics.predicates.GlobalPredicateSerializer;
import kmaput.sculk_extras.sculk_logistics.predicates.PredicateSerializer;
import kmaput.sculk_extras.sculk_logistics.predicates.LocatedPredicate;
import net.minecraft.nbt.CompoundTag;

import java.util.function.Predicate;

public class NotPredicate<T> implements Predicate<T> {
    private final LocatedPredicate<T> child;

    public NotPredicate(LocatedPredicate<T> child) {
        this.child = child;
    }

    @Override
    public boolean test(T t) {
        return !child.predicate().test(t);
    }

    public static <T> PredicateSerializer<T> getSerializer(GlobalPredicateSerializer<T> globalSerializer) {
        return new PredicateSerializer<>() {
            @Override
            public void save(Predicate<T> p, CompoundTag tag) {
                if (p instanceof NotPredicate<T> predicate) {
                    CompoundTag childTag = new CompoundTag();
                    globalSerializer.save(predicate.child, childTag);
                    tag.put("child", childTag);
                }
            }

            @Override
            public Predicate<T> load(CompoundTag tag) {
                return new NotPredicate<>(globalSerializer.load(tag.getCompound("child")));
            }
        };
    }
}
