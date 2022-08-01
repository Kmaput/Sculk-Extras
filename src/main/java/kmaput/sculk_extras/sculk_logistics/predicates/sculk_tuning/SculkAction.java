package kmaput.sculk_extras.sculk_logistics.predicates.sculk_tuning;

import java.util.List;

public record SculkAction<T>(List<T> targets, Type type) {
    public enum Type {
        ACTION,
        IGNORE
    }
}
