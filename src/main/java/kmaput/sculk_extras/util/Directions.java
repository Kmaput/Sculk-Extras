package kmaput.sculk_extras.util;

import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.Objects;

public final class Directions {
    private static final Direction[][] CACHED_ARRAYS;
    static {
        Direction[][] builder = new Direction[64][];
        for (int i = 0; i < 64; i++) {
            ArrayList<Direction> elementBuilder = new ArrayList<>(6);
            for (int d = 0; d < 6; d++) {
                if (((i >> d) & 1) != 0) {
                    elementBuilder.add(Direction.values()[d]);
                }
            }
            builder[i] = elementBuilder.toArray(Direction[]::new);
        }
        CACHED_ARRAYS = builder;
    }

    private final int mask;
    private Directions(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return mask;
    }

    public static Directions fromMask(int mask) {
        return new Directions(mask);
    }

    public static Directions from(Direction direction) {
        return new Directions(1 << direction.ordinal());
    }

    public static Directions fromMany(Direction... directions) {
        int mask = 0;
        for (Direction direction : directions) {
            mask |= (1 << direction.ordinal());
        }
        return new Directions(mask);
    }

    public static Directions all() {
        return new Directions(63);
    }

    public static Directions none() {
        return new Directions(0);
    }

    public Directions complete() {
        return new Directions(this.mask ^ 63);
    }

    public Directions intersect(Directions other) {
        return new Directions(this.mask & other.mask);
    }

    public Directions union(Directions other) {
        return new Directions(this.mask | other.mask);
    }

    public Directions subtract(Directions other) {
        return new Directions(this.mask & ~other.mask);
    }

    public Direction[] toArray() {
        return CACHED_ARRAYS[this.mask];
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof Directions other) {
            return mask == other.mask;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return mask;
    }
}
