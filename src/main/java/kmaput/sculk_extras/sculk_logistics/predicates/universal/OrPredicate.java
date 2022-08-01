package kmaput.sculk_extras.sculk_logistics.predicates.universal;

import kmaput.sculk_extras.sculk_logistics.predicates.GlobalPredicateSerializer;
import kmaput.sculk_extras.sculk_logistics.predicates.PredicateSerializer;
import kmaput.sculk_extras.sculk_logistics.predicates.LocatedPredicate;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class OrPredicate<T> implements Predicate<T> {
    private final List<LocatedPredicate<T>> children;

    public OrPredicate(List<LocatedPredicate<T>> children) {
        this.children = children;
    }

    @Override
    public boolean test(T t) {
        for (LocatedPredicate<T> child : children) {
            if (child.predicate().test(t)) return true;
        }
        return false;
    }

    public static <T> PredicateSerializer<T> getSerializer(GlobalPredicateSerializer<T> globalSerializer) {
        return new PredicateSerializer<>() {
            @Override
            public void save(Predicate<T> p, CompoundTag tag) {
                if (p instanceof OrPredicate<T> predicate) {
                    ListTag childrenTag = new ListTag();
                    for (LocatedPredicate<T> child : predicate.children) {
                        CompoundTag childTag = new CompoundTag();
                        globalSerializer.save(child, childTag);
                        childrenTag.add(childTag);
                    }
                    tag.put("children", childrenTag);
                }
            }

            @Override
            public Predicate<T> load(CompoundTag tag) {
                ListTag childrenTag = tag.getList("children", Tag.TAG_COMPOUND);
                List<LocatedPredicate<T>> children = new ArrayList<>();
                for (int i = 0; i < childrenTag.size(); i++) {
                    children.add(globalSerializer.load(childrenTag.getCompound(i)));
                }
                return new OrPredicate<>(children);
            }
        };
    }
}
